public class InsecurePasswordHashingRule extends AnalysisRule {

    private void checkHashCall(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx).toLowerCase();
        if (callable.isEmpty()) return;

        String args = SecurityRuleUtil.argsText(ctx).toLowerCase();
        boolean fastHash = callable.equals("md5")
                || callable.equals("sha1")
                || callable.equals("sha256")
                || callable.endsWith(".md5")
                || callable.endsWith(".sha1")
                || callable.endsWith(".sha256");

        if (fastHash && (args.contains("password") || args.contains("passwd") || args.contains("pwd"))) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Contrasena procesada con hash rapido; usar bcrypt, scrypt o Argon2 con salt");
        }

        if (callable.endsWith("pbkdf2_hmac") && (args.contains("\"\"") || args.contains("''") || !args.contains("salt"))) {
            flag(Severity.MEDIUM, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Hash de contrasena sin salt claro o con salt vacio");
        }
    }

    @Override protected String name() { return "InsecurePasswordHashing"; }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkHashCall(ctx);
    }
}
