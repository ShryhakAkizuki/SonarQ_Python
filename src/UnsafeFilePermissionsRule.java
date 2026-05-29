public class UnsafeFilePermissionsRule extends AnalysisRule {

    private void checkPermissions(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.isEmpty()) return;
        String args = SecurityRuleUtil.argsText(ctx).replace(" ", "");

        if ((callable.endsWith("chmod") || callable.endsWith("mkdir") || callable.endsWith("open"))
                && (args.contains("0o777") || args.contains("0o666") || args.contains("511") || args.contains("438"))) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Permisos de archivo o directorio demasiado amplios");
        }
    }

    @Override protected String name() { return "UnsafeFilePermissions"; }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkPermissions(ctx);
    }
}
