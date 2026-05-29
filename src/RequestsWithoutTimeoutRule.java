import java.util.Set;

public class RequestsWithoutTimeoutRule extends AnalysisRule {

    private static final Set<String> METHODS = Set.of(
            "get", "post", "put", "delete", "patch", "head", "options", "request"
    );

    private void checkRequest(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.isEmpty()) return;
        String lower = callable.toLowerCase();
        int dot = lower.lastIndexOf('.');
        String method = dot >= 0 ? lower.substring(dot + 1) : lower;

        boolean networkClient = lower.startsWith("requests.")
                || lower.startsWith("httpx.")
                || lower.contains(".session.")
                || lower.endsWith(".request");

        if (networkClient && METHODS.contains(method) && !SecurityRuleUtil.hasKeyword(SecurityRuleUtil.argsText(ctx), "timeout")) {
            flag(Severity.MEDIUM, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Llamada de red sin timeout explicito");
        }
    }

    @Override protected String name() { return "RequestsWithoutTimeout"; }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkRequest(ctx);
    }
}
