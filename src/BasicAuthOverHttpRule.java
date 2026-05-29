import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashSet;
import java.util.Set;

public class BasicAuthOverHttpRule extends AnalysisRule {

    private final Set<String> authVars = new HashSet<>();

    private void rememberAuth(PythonParser.AssignmentContext ctx) {
        String target = SecurityRuleUtil.simpleName(SecurityRuleUtil.assignmentTarget(ctx));
        ParserRuleContext value = SecurityRuleUtil.assignmentValue(ctx);
        if (target == null || value == null) return;

        String text = (target + value.getText()).toLowerCase();
        if (text.contains("authorization") || text.contains("bearer") || text.contains("basic") || text.contains("token")) {
            authVars.add(target);
        } else {
            authVars.remove(target);
        }
    }

    private void checkCall(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.isEmpty()) return;

        String args = SecurityRuleUtil.argsText(ctx);
        String normalized = args.replace(" ", "").toLowerCase();
        boolean http = normalized.contains("http://");
        boolean auth = normalized.contains("auth=")
                || normalized.contains("authorization")
                || normalized.contains("basic ")
                || normalized.contains("bearer ")
                || normalized.contains("token")
                || authVars.stream().anyMatch(v -> SecurityRuleUtil.referencesName(args, v));

        if (http && auth) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Autenticacion o token enviado sobre HTTP sin TLS");
        }
    }

    @Override protected String name() { return "BasicAuthOverHttp"; }

    @Override public void enterAssignment(PythonParser.AssignmentContext ctx) {
        rememberAuth(ctx);
    }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkCall(ctx);
    }
}
