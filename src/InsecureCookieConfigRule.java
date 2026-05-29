public class InsecureCookieConfigRule extends AnalysisRule {

    // ----------- Metodos -----------
    private void checkCookieCreation(PythonParser.PrimaryContext ctx) {
        if (ctx.LPAR() == null || ctx.primary() == null) return;

        String callable = ctx.primary().getText();
        if (!isCookieSetter(callable)) return;

        String args = ctx.arguments() != null ? ctx.arguments().getText() : "";
        String missing = missingSecurityAttributes(args);
        if (missing == null) return;

        flag(Severity.HIGH,
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine(),
                "Cookie creada sin atributos de seguridad: falta " + missing);
    }

    // ----------- Helpers -----------
    private static boolean isCookieSetter(String callable) {
        return callable.equals("set_cookie")
                || callable.equals("set_signed_cookie")
                || callable.endsWith(".set_cookie")
                || callable.endsWith(".set_signed_cookie")
                || callable.endsWith(".cookies.set")
                || callable.endsWith(".cookies.set_cookie");
    }

    private static String missingSecurityAttributes(String args) {
        boolean httpOnly = hasTruthyKeyword(args, "httponly") || hasTruthyKeyword(args, "http_only");
        boolean secure = hasTruthyKeyword(args, "secure");
        boolean sameSite = hasSameSite(args);

        StringBuilder missing = new StringBuilder();
        appendMissing(missing, httpOnly, "HttpOnly");
        appendMissing(missing, secure, "Secure");
        appendMissing(missing, sameSite, "SameSite");

        return missing.isEmpty() ? null : missing.toString();
    }

    private static void appendMissing(StringBuilder missing, boolean present, String name) {
        if (present) return;
        if (!missing.isEmpty()) missing.append(", ");
        missing.append(name);
    }

    private static boolean hasTruthyKeyword(String args, String keyword) {
        String normalized = args.replace(" ", "").toLowerCase();
        return normalized.contains(keyword.toLowerCase() + "=true")
                || normalized.contains(keyword.toLowerCase() + "=1");
    }

    private static boolean hasSameSite(String args) {
        String normalized = args.replace(" ", "").toLowerCase();
        if (!normalized.contains("samesite=")) return false;
        return !normalized.contains("samesite=none")
                && !normalized.contains("samesite=\"none\"")
                && !normalized.contains("samesite='none'")
                && !normalized.contains("samesite=null");
    }

    // ----------- Overrides -----------
    @Override protected String name() { return "InsecureCookieConfig"; }

    @Override
    public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkCookieCreation(ctx);
    }
}
