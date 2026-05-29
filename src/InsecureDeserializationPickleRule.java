import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.Map;

public class InsecureDeserializationPickleRule extends AnalysisRule {

    // ----------- Variables -----------
    private final Map<String, String> taintedVars = new HashMap<>();
    private final Map<String, String> fileVars = new HashMap<>();

    // ----------- Metodos -----------
    private void rememberSource(String target, ParserRuleContext value) {
        String variable = simpleName(target);
        if (variable == null) return;

        String reason = sourceReason(value);
        if (reason != null) {
            taintedVars.put(variable, reason);
        } else {
            taintedVars.remove(variable);
        }

        if (isOpenCall(value)) {
            fileVars.put(variable, "archivo externo");
            taintedVars.put(variable, "archivo externo");
        } else if (!taintedVars.containsKey(variable)) {
            fileVars.remove(variable);
        }
    }

    private void checkPickleUse(PythonParser.PrimaryContext ctx) {
        if (ctx.LPAR() == null || ctx.primary() == null) return;

        String callable = ctx.primary().getText();
        if (!isDangerousPickleCall(callable)) return;

        ParserRuleContext firstArg = firstArgument(ctx.arguments());
        if (firstArg == null) return;

        String reason = sourceReason(firstArg);
        if (reason == null) return;

        String action = callable.endsWith("Unpickler") ? "pickle.Unpickler" : callable;
        flag(Severity.HIGH,
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine(),
                "Deserializacion insegura con " + action + " sobre datos no confiables (" + reason + ")");
    }

    // ----------- Helpers -----------
    private static String simpleName(String text) {
        if (text == null || !text.matches("[A-Za-z_][A-Za-z0-9_]*")) return null;
        return text;
    }

    private static boolean isDangerousPickleCall(String callable) {
        return callable.endsWith("pickle.load")
                || callable.endsWith("pickle.loads")
                || callable.equals("pickle.load")
                || callable.equals("pickle.loads")
                || callable.endsWith("pickle.Unpickler")
                || callable.equals("pickle.Unpickler")
                || callable.equals("load")
                || callable.equals("loads")
                || callable.equals("Unpickler");
    }

    private String sourceReason(ParseTree node) {
        if (node == null) return null;

        String text = node.getText();
        if (taintedVars.containsKey(text)) return "variable '" + text + "' proveniente de " + taintedVars.get(text);
        if (fileVars.containsKey(text)) return "archivo externo";
        if (isOpenCall(node)) return "archivo externo";
        if (isReadFromTrackedFile(text)) return "lectura de archivo externo";
        if (containsExternalInput(text)) return "entrada externa";

        for (int i = 0; i < node.getChildCount(); i++) {
            String childReason = sourceReason(node.getChild(i));
            if (childReason != null) return childReason;
        }
        return null;
    }

    private static boolean isOpenCall(ParseTree node) {
        if (node == null) return false;
        String text = node.getText();
        return text.startsWith("open(") || text.contains("=open(");
    }

    private boolean isReadFromTrackedFile(String text) {
        if (!text.endsWith(".read()") && !text.contains(".read(")) return false;
        int dot = text.indexOf('.');
        if (dot <= 0) return false;
        String owner = text.substring(0, dot);
        return fileVars.containsKey(owner) || taintedVars.containsKey(owner);
    }

    private static boolean containsExternalInput(String text) {
        return text.contains("input(")
                || text.contains("request.")
                || text.contains("request[")
                || text.contains("sys.stdin")
                || text.contains(".recv(")
                || text.contains(".recvfrom(")
                || text.contains("socket.")
                || text.contains("get_data(")
                || text.contains("get_json(");
    }

    private static ParserRuleContext firstArgument(PythonParser.ArgumentsContext arguments) {
        if (arguments == null || arguments.args() == null) return null;

        PythonParser.ArgsContext args = arguments.args();
        if (!args.expression().isEmpty()) return args.expression(0);
        if (!args.assignment_expression().isEmpty()) return args.assignment_expression(0);
        if (!args.starred_expression().isEmpty()) return args.starred_expression(0);
        return null;
    }

    // ----------- Overrides -----------
    @Override protected String name() { return "InsecureDeserializationPickle"; }

    @Override
    public void enterAssignment(PythonParser.AssignmentContext ctx) {
        if (!ctx.star_targets().isEmpty() && ctx.star_expressions() != null)
            rememberSource(ctx.star_targets(0).getText(), ctx.star_expressions());

        if (ctx.name() != null && ctx.annotated_rhs() != null)
            rememberSource(ctx.name().getText(), ctx.annotated_rhs());
    }

    @Override
    public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkPickleUse(ctx);
    }
}
