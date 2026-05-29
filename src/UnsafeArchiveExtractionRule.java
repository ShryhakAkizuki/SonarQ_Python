public class UnsafeArchiveExtractionRule extends AnalysisRule {

    private void checkExtraction(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.endsWith(".extractall") || callable.endsWith(".extract")) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Extraccion de archivo comprimido sin validar rutas internas (Zip Slip)");
        }
    }

    @Override protected String name() { return "UnsafeArchiveExtraction"; }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkExtraction(ctx);
    }
}
