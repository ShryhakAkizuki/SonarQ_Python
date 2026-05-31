/**
 * Detecta uso de variables globales que dificultan el testing y mantenimiento.
 * Sugiere usar parametros, clases o patrones de diseño.
 */
public class GlobalVariableRule extends AnalysisRule {

    @Override
    protected String name() {
        return "GlobalVariable";
    }

    @Override
    public void enterGlobal_stmt(PythonParser.Global_stmtContext ctx) {
        // Obtener nombres de variables declaradas como global
        if (ctx.name() != null && !ctx.name().isEmpty()) {
            StringBuilder varNames = new StringBuilder();
            for (int i = 0; i < ctx.name().size(); i++) {
                if (i > 0) varNames.append(", ");
                varNames.append(ctx.name(i).getText());
            }
            
            flag(Severity.MEDIUM,
                 ctx.start.getLine(),
                 ctx.start.getCharPositionInLine(),
                 String.format("Uso de variable(s) global(es): %s - considerar usar parametros, clases o inyeccion de dependencias",
                     varNames.toString()));
        }
    }

    @Override
    public void enterNonlocal_stmt(PythonParser.Nonlocal_stmtContext ctx) {
        // Detectar uso de nonlocal (similar problema a global)
        if (ctx.name() != null && !ctx.name().isEmpty()) {
            StringBuilder varNames = new StringBuilder();
            for (int i = 0; i < ctx.name().size(); i++) {
                if (i > 0) varNames.append(", ");
                varNames.append(ctx.name(i).getText());
            }
            
            flag(Severity.LOW,
                 ctx.start.getLine(),
                 ctx.start.getCharPositionInLine(),
                 String.format("Uso de variable(s) nonlocal: %s - considerar refactorizar a clase o retornar valores",
                     varNames.toString()));
        }
    }
}