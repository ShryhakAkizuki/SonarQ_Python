import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashSet;
import java.util.Set;

public class FileOverwriteRiskRule extends AnalysisRule {

    private final Set<String> taintedFiles = new HashSet<>();

    private void remember(PythonParser.AssignmentContext ctx) {
        String target = SecurityRuleUtil.simpleName(SecurityRuleUtil.assignmentTarget(ctx));
        ParserRuleContext value = SecurityRuleUtil.assignmentValue(ctx);
        if (target == null || value == null) return;
        String text = value.getText();
        if (SecurityRuleUtil.containsExternalInput(text)
                || taintedFiles.stream().anyMatch(v -> SecurityRuleUtil.referencesName(text, v))) taintedFiles.add(target);
        else taintedFiles.remove(target);
    }

    private void checkFileWrite(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.isEmpty()) return;

        String argsOriginal = SecurityRuleUtil.argsText(ctx);
        ParserRuleContext firstArg = SecurityRuleUtil.firstArgument(ctx.arguments());
        String arg = firstArg != null ? firstArg.getText() : "";
        String args = SecurityRuleUtil.argsText(ctx).replace(" ", "").toLowerCase();

        boolean writeOpen = callable.equals("open")
                && (args.contains(",\"w\"") || args.contains(",'w'") || args.contains(",\"a\"")
                || args.contains(",'a'") || args.contains(",\"wb\"") || args.contains(",'wb'"));
        boolean destructive = callable.endsWith("os.rename")
                || callable.endsWith("os.replace")
                || callable.endsWith("shutil.move");

        boolean taintedArg = SecurityRuleUtil.containsExternalInput(arg)
                || taintedFiles.contains(arg)
                || taintedFiles.stream().anyMatch(v -> SecurityRuleUtil.referencesName(argsOriginal, v));
        if ((writeOpen || destructive) && taintedArg) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Escritura o sobrescritura de archivo usando ruta no confiable");
        }
    }

    @Override protected String name() { return "FileOverwriteRisk"; }

    @Override public void enterAssignment(PythonParser.AssignmentContext ctx) {
        remember(ctx);
    }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkFileWrite(ctx);
    }
}
