import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashSet;
import java.util.Set;

public class PathTraversalRule extends AnalysisRule {

    private final Set<String> taintedPaths = new HashSet<>();

    private void remember(PythonParser.AssignmentContext ctx) {
        String target = SecurityRuleUtil.simpleName(SecurityRuleUtil.assignmentTarget(ctx));
        ParserRuleContext value = SecurityRuleUtil.assignmentValue(ctx);
        if (target == null || value == null) return;

        String text = value.getText();
        boolean pathBuild = text.contains("os.path.join(") || text.contains("Path(") || text.contains("+");
        boolean usesTaintedPath = taintedPaths.stream().anyMatch(v -> SecurityRuleUtil.referencesName(text, v));
        if ((pathBuild && (SecurityRuleUtil.containsExternalInput(text) || usesTaintedPath))
                || SecurityRuleUtil.containsExternalInput(text)) {
            taintedPaths.add(target);
        } else {
            taintedPaths.remove(target);
        }
    }

    private void checkSink(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.isEmpty()) return;
        if (!callable.equals("open") && !callable.endsWith("send_file") && !callable.endsWith("send_from_directory")) return;
        if (callable.equals("open") && isWriteOpen(SecurityRuleUtil.argsText(ctx))) return;

        ParserRuleContext firstArg = SecurityRuleUtil.firstArgument(ctx.arguments());
        if (firstArg == null) return;
        String arg = firstArg.getText();
        if (SecurityRuleUtil.containsExternalInput(arg) || taintedPaths.contains(arg)) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Ruta construida con entrada externa usada sin normalizacion o validacion");
        }
    }

    private static boolean isWriteOpen(String args) {
        String n = args == null ? "" : args.replace(" ", "").toLowerCase();
        return n.contains(",\"w\"")
                || n.contains(",'w'")
                || n.contains(",\"a\"")
                || n.contains(",'a'")
                || n.contains(",\"x\"")
                || n.contains(",'x'")
                || n.contains(",\"wb\"")
                || n.contains(",'wb'")
                || n.contains(",\"ab\"")
                || n.contains(",'ab'");
    }

    @Override protected String name() { return "PathTraversal"; }

    @Override public void enterAssignment(PythonParser.AssignmentContext ctx) {
        remember(ctx);
    }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkSink(ctx);
    }
}
