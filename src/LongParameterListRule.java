/**
 * Detecta funciones con demasiados parametros.
 * Sugiere refactorizar usando objetos o patrones de diseño.
 */
public class LongParameterListRule extends AnalysisRule {

    private static final int THRESHOLD_LOW = 5;
    private static final int THRESHOLD_MEDIUM = 7;

    @Override
    protected String name() {
        return "LongParameterList";
    }

    @Override
    public void enterFunction_def_raw(PythonParser.Function_def_rawContext ctx) {
        // Obtener nombre de la funcion
        String functionName = ctx.name() != null ? ctx.name().getText() : "<anonima>";
        
        // Contar parametros
        int paramCount = 0;
        if (ctx.params() != null && ctx.params().parameters() != null) {
            PythonParser.ParametersContext params = ctx.params().parameters();
            
            // Contar parametros posicionales
            if (params.slash_no_default() != null) {
                paramCount += params.slash_no_default().param_no_default().size();
            }
            if (params.slash_with_default() != null) {
                paramCount += params.slash_with_default().param_no_default().size();
                paramCount += params.slash_with_default().param_with_default().size();
            }
            
            // Contar parametros normales
            if (params.param_no_default() != null) {
                paramCount += params.param_no_default().size();
            }
            if (params.param_with_default() != null) {
                paramCount += params.param_with_default().size();
            }
            
            // Contar *args y **kwargs
            if (params.star_etc() != null) {
                if (params.star_etc().param_no_default() != null) {
                    paramCount += 1; // *args cuenta como 1
                }
                if (params.star_etc().kwds() != null) {
                    paramCount += 1; // **kwargs cuenta como 1
                }
            }
        }
        
        // Reportar si excede umbrales
        if (paramCount > THRESHOLD_MEDIUM) {
            flag(Severity.MEDIUM,
                 ctx.start.getLine(),
                 ctx.start.getCharPositionInLine(),
                 String.format("Funcion '%s' tiene %d parametros (>%d) - considerar usar objeto de configuracion o patron Builder",
                     functionName, paramCount, THRESHOLD_MEDIUM));
        } else if (paramCount > THRESHOLD_LOW) {
            flag(Severity.LOW,
                 ctx.start.getLine(),
                 ctx.start.getCharPositionInLine(),
                 String.format("Funcion '%s' tiene %d parametros (>%d) - considerar agrupar parametros relacionados",
                     functionName, paramCount, THRESHOLD_LOW));
        }
    }
}