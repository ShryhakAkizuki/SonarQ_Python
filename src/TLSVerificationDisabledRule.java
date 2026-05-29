public class TLSVerificationDisabledRule extends AnalysisRule {

    private void checkTls(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.isEmpty()) return;
        String args = SecurityRuleUtil.argsText(ctx);
        String text = (callable + "(" + args + ")").replace(" ", "");

        if (SecurityRuleUtil.hasFalseKeyword(args, "verify")
                || text.contains("_create_unverified_context(")
                || text.contains("CERT_NONE")
                || text.contains("check_hostname=False")) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Verificacion TLS/certificados deshabilitada");
        }
    }

    @Override protected String name() { return "TLSVerificationDisabled"; }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkTls(ctx);
    }
}
