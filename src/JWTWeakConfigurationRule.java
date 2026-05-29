public class JWTWeakConfigurationRule extends AnalysisRule {

    private void checkJwt(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.isEmpty()) return;
        String lowerCallable = callable.toLowerCase();
        String args = SecurityRuleUtil.argsText(ctx);
        String normalized = args.replace(" ", "").toLowerCase();

        if ((lowerCallable.endsWith("jwt.decode") || lowerCallable.equals("decode"))
                && (normalized.contains("verify=false")
                || normalized.contains("verify_signature:false")
                || normalized.contains("\"verify_signature\":false")
                || normalized.contains("'verify_signature':false"))) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "JWT decodificado con verificacion de firma deshabilitada");
            return;
        }

        if (normalized.contains("algorithms=[\"none\"]")
                || normalized.contains("algorithms=['none']")
                || normalized.contains("algorithm=\"none\"")
                || normalized.contains("algorithm='none'")) {
            flag(Severity.CRITICAL, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "JWT configurado con algoritmo inseguro 'none'");
            return;
        }

        if ((lowerCallable.endsWith("jwt.encode") || lowerCallable.equals("encode"))
                && SecurityRuleUtil.hasHardcodedString(ctx)
                && (normalized.contains("secret") || normalized.contains("key") || normalized.contains("jwt"))) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "JWT firmado con secreto hardcodeado");
        }
    }

    @Override protected String name() { return "JWTWeakConfiguration"; }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkJwt(ctx);
    }
}
