import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SQLInjectionConcatRule extends AnalysisRule {

    // ----------- Variables -----------
    private static final Set<String> EXECUTE_METHODS = Set.of(
            "execute", "executemany", "executescript", "raw", "query"
    );

    private static final Pattern SQL_KEYWORDS = Pattern.compile(
            "(?i).*(SELECT|INSERT|UPDATE|DELETE|DROP|ALTER|CREATE|REPLACE|TRUNCATE)\\b.*"
    );

    private final Map<String, String> dynamicSqlVars = new HashMap<>();

    // ----------- Metodos -----------
    private void rememberDynamicSql(String target, ParserRuleContext value) {
        String variable = simpleName(target);
        if (variable == null) return;

        if (isDynamicSql(value)) {
            dynamicSqlVars.put(variable, unsafeKind(value));
        } else if (isTrackedDynamicSql(value.getText())) {
            dynamicSqlVars.put(variable, "variable previamente marcada");
        } else {
            dynamicSqlVars.remove(variable);
        }
    }

    private void checkSqlExecution(PythonParser.PrimaryContext ctx) {
        if (!isSqlExecutionCall(ctx)) return;

        ParserRuleContext firstArg = firstArgument(ctx.arguments());
        if (firstArg == null) return;

        String reason = null;
        if (isDynamicSql(firstArg)) {
            reason = unsafeKind(firstArg);
        } else if (isTrackedDynamicSql(firstArg.getText())) {
            reason = "variable '" + firstArg.getText() + "' construida dinamicamente";
        }

        if (reason != null) {
            flag(Severity.CRITICAL,
                    ctx.start.getLine(),
                    ctx.start.getCharPositionInLine(),
                    "Consulta SQL construida por " + reason + " antes de ejecutarse");
        }
    }

    // ----------- Helpers -----------
    private static String simpleName(String text) {
        if (text == null || !text.matches("[A-Za-z_][A-Za-z0-9_]*")) return null;
        return text;
    }

    private boolean isTrackedDynamicSql(String text) {
        return dynamicSqlVars.containsKey(text);
    }

    private static boolean isSqlExecutionCall(PythonParser.PrimaryContext ctx) {
        if (ctx.LPAR() == null || ctx.primary() == null) return false;

        String callable = ctx.primary().getText();
        int dot = callable.lastIndexOf('.');
        String method = dot >= 0 ? callable.substring(dot + 1) : callable;
        return EXECUTE_METHODS.contains(method);
    }

    private static ParserRuleContext firstArgument(PythonParser.ArgumentsContext arguments) {
        if (arguments == null || arguments.args() == null) return null;

        PythonParser.ArgsContext args = arguments.args();
        if (!args.expression().isEmpty()) return args.expression(0);
        if (!args.assignment_expression().isEmpty()) return args.assignment_expression(0);
        if (!args.starred_expression().isEmpty()) return args.starred_expression(0);
        return null;
    }

    private boolean isDynamicSql(ParseTree node) {
        return looksLikeSql(node) && (
                hasUnsafeConcat(node)
                        || hasUnsafeFString(node)
                        || hasUnsafeFormatCall(node)
                        || hasPercentFormatting(node)
                        || isTrackedDynamicSql(node.getText())
        );
    }

    private static String unsafeKind(ParseTree node) {
        if (hasUnsafeFString(node)) return "f-string con interpolacion";
        if (hasUnsafeFormatCall(node)) return "format()";
        if (hasPercentFormatting(node)) return "formateo con %";
        if (hasUnsafeConcat(node)) return "concatenacion";
        return "cadena dinamica";
    }

    private static boolean looksLikeSql(ParseTree node) {
        if (node == null) return false;
        String text = node.getText().replace('_', ' ');
        return SQL_KEYWORDS.matcher(text).matches();
    }

    private static boolean hasUnsafeConcat(ParseTree node) {
        return hasPlusConcat(node) && hasStringLiteral(node) && hasVariableLikeNode(node);
    }

    private static boolean hasUnsafeFString(ParseTree node) {
        return hasDescendant(node, PythonParser.Fstring_replacement_fieldContext.class);
    }

    private static boolean hasUnsafeFormatCall(ParseTree node) {
        String text = node.getText();
        return text.contains(".format(") && hasStringLiteral(node);
    }

    private static boolean hasPercentFormatting(ParseTree node) {
        String text = node.getText();
        return text.contains("%") && hasStringLiteral(node) && hasVariableLikeNode(node);
    }

    private static boolean hasStringLiteral(ParseTree node) {
        if (node == null) return false;
        if (node instanceof PythonParser.StringsContext || node instanceof PythonParser.StringContext) return true;
        if (isStringLit(node.getText())) return true;
        for (int i = 0; i < node.getChildCount(); i++)
            if (hasStringLiteral(node.getChild(i))) return true;
        return false;
    }

    private static boolean hasVariableLikeNode(ParseTree node) {
        if (node == null) return false;
        if (node instanceof PythonParser.NameContext) return true;
        if (node instanceof PythonParser.PrimaryContext p && p.name() != null) return true;
        for (int i = 0; i < node.getChildCount(); i++)
            if (hasVariableLikeNode(node.getChild(i))) return true;
        return false;
    }

    // ----------- Overrides -----------
    @Override protected String name() { return "SQLInjectionConcat"; }

    @Override
    public void enterAssignment(PythonParser.AssignmentContext ctx) {
        if (!ctx.star_targets().isEmpty() && ctx.star_expressions() != null)
            rememberDynamicSql(ctx.star_targets(0).getText(), ctx.star_expressions());

        if (ctx.name() != null && ctx.annotated_rhs() != null)
            rememberDynamicSql(ctx.name().getText(), ctx.annotated_rhs());
    }

    @Override
    public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkSqlExecution(ctx);
    }
}
