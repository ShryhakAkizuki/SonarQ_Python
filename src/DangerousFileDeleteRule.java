import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashSet;
import java.util.Set;

public class DangerousFileDeleteRule extends AnalysisRule {

    private final Set<String> taintedDeletePaths = new HashSet<>();

    private void remember(PythonParser.AssignmentContext ctx) {
        String target = SecurityRuleUtil.simpleName(SecurityRuleUtil.assignmentTarget(ctx));
        ParserRuleContext value = SecurityRuleUtil.assignmentValue(ctx);
        if (target == null || value == null) return;
        if (SecurityRuleUtil.containsExternalInput(value.getText())) taintedDeletePaths.add(target);
        else taintedDeletePaths.remove(target);
    }

    private void checkDelete(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.isEmpty()) return;
        boolean deleteCall = callable.endsWith("os.remove")
                || callable.endsWith("os.unlink")
                || callable.endsWith("shutil.rmtree")
                || callable.equals("remove")
                || callable.equals("unlink")
                || callable.equals("rmtree");
        if (!deleteCall) return;

        ParserRuleContext firstArg = SecurityRuleUtil.firstArgument(ctx.arguments());
        String arg = firstArg != null ? firstArg.getText() : "";
        if (SecurityRuleUtil.containsExternalInput(arg) || taintedDeletePaths.contains(arg)) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Borrado de archivos usando ruta controlada por usuario");
        }
    }

    @Override protected String name() { return "DangerousFileDelete"; }

    @Override public void enterAssignment(PythonParser.AssignmentContext ctx) {
        remember(ctx);
    }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkDelete(ctx);
    }
}
