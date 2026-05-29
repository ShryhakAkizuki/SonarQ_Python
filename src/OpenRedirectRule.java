import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashSet;
import java.util.Set;

public class OpenRedirectRule extends AnalysisRule {

    private final Set<String> taintedRedirectVars = new HashSet<>();

    private void remember(PythonParser.AssignmentContext ctx) {
        String target = SecurityRuleUtil.simpleName(SecurityRuleUtil.assignmentTarget(ctx));
        ParserRuleContext value = SecurityRuleUtil.assignmentValue(ctx);
        if (target == null || value == null) return;

        String text = value.getText();
        if (SecurityRuleUtil.containsExternalInput(text) || text.contains("request.args.get") || text.contains("request.form.get")) {
            taintedRedirectVars.add(target);
        } else {
            taintedRedirectVars.remove(target);
        }
    }

    private void checkRedirect(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (!callable.endsWith("redirect") && !callable.equals("redirect")) return;
        ParserRuleContext firstArg = SecurityRuleUtil.firstArgument(ctx.arguments());
        if (firstArg == null) return;

        String arg = firstArg.getText();
        if (SecurityRuleUtil.containsExternalInput(arg) || taintedRedirectVars.contains(arg)) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Redireccion construida con parametro de usuario sin allowlist visible");
        }
    }

    @Override protected String name() { return "OpenRedirect"; }

    @Override public void enterAssignment(PythonParser.AssignmentContext ctx) {
        remember(ctx);
    }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkRedirect(ctx);
    }
}
