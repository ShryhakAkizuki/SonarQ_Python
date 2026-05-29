import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Set;

public final class SecurityRuleUtil {

    private SecurityRuleUtil() { }

    public static String callable(PythonParser.PrimaryContext ctx) {
        return ctx != null && ctx.LPAR() != null && ctx.primary() != null
                ? ctx.primary().getText()
                : "";
    }

    public static String argsText(PythonParser.PrimaryContext ctx) {
        return ctx != null && ctx.arguments() != null ? ctx.arguments().getText() : "";
    }

    public static ParserRuleContext firstArgument(PythonParser.ArgumentsContext arguments) {
        if (arguments == null || arguments.args() == null) return null;

        PythonParser.ArgsContext args = arguments.args();
        if (!args.expression().isEmpty()) return args.expression(0);
        if (!args.assignment_expression().isEmpty()) return args.assignment_expression(0);
        if (!args.starred_expression().isEmpty()) return args.starred_expression(0);
        return null;
    }

    public static String simpleName(String text) {
        if (text == null || !text.matches("[A-Za-z_][A-Za-z0-9_]*")) return null;
        return text;
    }

    public static boolean containsAny(String text, Set<String> words) {
        if (text == null) return false;
        String normalized = text.toLowerCase();
        return words.stream().anyMatch(normalized::contains);
    }

    public static boolean containsAnyToken(String text, Set<String> words) {
        if (text == null) return false;
        String normalized = text.toLowerCase();
        return words.stream().anyMatch(w -> normalized.matches(".*\\b" + w + "\\b.*"));
    }

    public static boolean referencesName(String text, String name) {
        if (text == null || name == null) return false;
        return text.matches(".*(?<![A-Za-z0-9_\\.])" + name + "(?![A-Za-z0-9_]).*");
    }

    public static boolean isStringOrBytesLiteral(String text) {
        return text != null && text.replaceFirst("(?i)^[bruf]{0,4}", "").matches("^[\"'].*");
    }

    public static String stripQuotes(String text) {
        if (text == null) return "";
        String s = text.replaceFirst("(?i)^[bruf]{0,4}", "");
        if (s.length() >= 6 && (s.startsWith("\"\"\"") || s.startsWith("'''")))
            return s.substring(3, s.length() - 3);
        if (s.length() >= 2 && (s.startsWith("\"") || s.startsWith("'")))
            return s.substring(1, s.length() - 1);
        return s;
    }

    public static boolean isHardcodedString(ParseTree node) {
        if (node == null) return false;
        if (node instanceof PythonParser.StringsContext || node instanceof PythonParser.StringContext) return true;
        return isStringOrBytesLiteral(node.getText());
    }

    public static boolean hasHardcodedString(ParseTree node) {
        if (node == null) return false;
        if (isHardcodedString(node)) return true;
        for (int i = 0; i < node.getChildCount(); i++)
            if (hasHardcodedString(node.getChild(i))) return true;
        return false;
    }

    public static boolean containsExternalInput(String text) {
        if (text == null) return false;
        return text.contains("input(")
                || text.contains("request.")
                || text.contains("request[")
                || text.contains("request.args")
                || text.contains("request.form")
                || text.contains("request.json")
                || text.contains("request.get_json(")
                || text.contains("sys.argv")
                || text.contains("os.environ")
                || text.contains(".recv(")
                || text.contains(".recvfrom(")
                || text.contains(".read()");
    }

    public static boolean hasKeyword(String args, String keyword) {
        return args != null && args.replace(" ", "").toLowerCase().contains(keyword.toLowerCase() + "=");
    }

    public static boolean hasFalseKeyword(String args, String keyword) {
        String n = args == null ? "" : args.replace(" ", "").toLowerCase();
        return n.contains(keyword.toLowerCase() + "=false");
    }

    public static boolean hasTruthyKeyword(String args, String keyword) {
        String n = args == null ? "" : args.replace(" ", "").toLowerCase();
        return n.contains(keyword.toLowerCase() + "=true") || n.contains(keyword.toLowerCase() + "=1");
    }

    public static String assignmentTarget(PythonParser.AssignmentContext ctx) {
        if (ctx == null) return null;
        if (!ctx.star_targets().isEmpty()) return ctx.star_targets(0).getText();
        if (ctx.name() != null) return ctx.name().getText();
        return null;
    }

    public static ParserRuleContext assignmentValue(PythonParser.AssignmentContext ctx) {
        if (ctx == null) return null;
        if (ctx.star_expressions() != null) return ctx.star_expressions();
        if (ctx.annotated_rhs() != null) return ctx.annotated_rhs();
        return null;
    }
}
