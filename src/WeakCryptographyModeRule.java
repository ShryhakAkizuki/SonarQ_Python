public class WeakCryptographyModeRule extends AnalysisRule {

    private void checkCryptoCall(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.isEmpty()) return;

        String text = (callable + "(" + SecurityRuleUtil.argsText(ctx) + ")").replace(" ", "");
        if (text.contains("MODE_ECB")) {
            flag(Severity.CRITICAL, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Modo criptografico inseguro ECB usado para cifrado");
            return;
        }

        if (text.contains("MODE_CBC") && !text.contains("HMAC") && !text.contains("encrypt_and_digest")) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Modo CBC usado sin autenticacion visible del ciphertext");
            return;
        }

        if ((callable.endsWith(".pad") || callable.equals("pad")) && text.toLowerCase().contains("pkcs7")) {
            flag(Severity.MEDIUM, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Padding manual detectado; validar uso autenticado y manejo de errores");
        }
    }

    @Override protected String name() { return "WeakCryptographyMode"; }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkCryptoCall(ctx);
    }
}
