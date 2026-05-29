import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Set;

public class InsecureHttpUsageRule extends AnalysisRule {

    private static final Set<String> SENSITIVE = Set.of(
            "auth", "login", "token", "password", "secret", "api", "webhook", "callback", "credential"
    );

    private void checkAssignment(PythonParser.AssignmentContext ctx) {
        String target = SecurityRuleUtil.assignmentTarget(ctx);
        ParserRuleContext value = SecurityRuleUtil.assignmentValue(ctx);
        if (target == null || value == null) return;
        String text = value.getText();

        if (text.contains("http://") && SecurityRuleUtil.containsAny(target + text, SENSITIVE)) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "URL HTTP usada para transmitir datos sensibles en '" + target + "'");
        }
    }

    private void checkCall(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.isEmpty()) return;
        String args = SecurityRuleUtil.argsText(ctx);
        if (args.contains("http://") && SecurityRuleUtil.containsAny(args, SENSITIVE)) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "URL HTTP usada en llamada sensible");
        }
    }

    @Override protected String name() { return "InsecureHttpUsage"; }

    @Override public void enterAssignment(PythonParser.AssignmentContext ctx) {
        checkAssignment(ctx);
    }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkCall(ctx);
    }
}
