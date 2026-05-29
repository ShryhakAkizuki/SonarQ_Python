import org.antlr.v4.runtime.ParserRuleContext;

public class InsecureTempFileUsageRule extends AnalysisRule {

    private void checkCall(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx);
        if (callable.isEmpty()) return;
        String text = (callable + "(" + SecurityRuleUtil.argsText(ctx) + ")").replace(" ", "").toLowerCase();

        if (callable.endsWith("tempfile.mktemp") || callable.equals("mktemp")) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Uso inseguro de mktemp(); usar NamedTemporaryFile o mkstemp");
            return;
        }

        if (text.contains("tempfile.gettempdir(") || text.contains("\"/tmp/") || text.contains("'/tmp/")) {
            flag(Severity.MEDIUM, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Archivo temporal predecible en directorio compartido");
        }
    }

    private void checkAssignment(PythonParser.AssignmentContext ctx) {
        ParserRuleContext value = SecurityRuleUtil.assignmentValue(ctx);
        if (value == null) return;
        String text = value.getText().toLowerCase();
        if ((text.contains("\"/tmp/") || text.contains("'/tmp/")) && (text.contains("+") || text.contains("format("))) {
            flag(Severity.MEDIUM, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Ruta temporal predecible construida manualmente");
        }
    }

    @Override protected String name() { return "InsecureTempFileUsage"; }

    @Override public void enterAssignment(PythonParser.AssignmentContext ctx) {
        checkAssignment(ctx);
    }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkCall(ctx);
    }
}
