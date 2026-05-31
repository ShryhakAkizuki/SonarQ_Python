import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import java.util.HashSet;
import java.util.Set;

/**
 * Detecta funciones con demasiadas lineas de codigo.
 * Umbral HIGH: >50 lineas, CRITICAL: >100 lineas
 * Funciones largas son dificiles de mantener, probar y entender.
 */
public class LongMethodRule extends AnalysisRule {

    private static final int HIGH_THRESHOLD = 50;
    private static final int CRITICAL_THRESHOLD = 100;

    @Override
    protected String name() {
        return "LongMethod";
    }

    @Override
    public void exitFunction_def_raw(PythonParser.Function_def_rawContext ctx) {
        String functionName = ctx.name() != null ? ctx.name().getText() : "<anonima>";
        int lineCount = countCodeLines(ctx);
        
        if (lineCount > CRITICAL_THRESHOLD) {
            flag(Severity.CRITICAL,
                 ctx.start.getLine(),
                 ctx.start.getCharPositionInLine(),
                 String.format("Funcion '%s': %d lineas - URGENTE: dividir en funciones mas pequenas",
                     functionName, lineCount));
        } else if (lineCount > HIGH_THRESHOLD) {
            flag(Severity.HIGH,
                 ctx.start.getLine(),
                 ctx.start.getCharPositionInLine(),
                 String.format("Funcion '%s': %d lineas - refactorizar para mejorar mantenibilidad",
                     functionName, lineCount));
        }
    }

    /**
     * Cuenta lineas de codigo simplemente calculando el rango de lineas.
     * Metodo simple y efectivo: linea final - linea inicial.
     */
    private int countCodeLines(ParserRuleContext ctx) {
        if (ctx.start == null || ctx.stop == null) return 0;
        
        int startLine = ctx.start.getLine();
        int endLine = ctx.stop.getLine();
        
        // Contar lineas en el rango (inclusive)
        // Esto incluye todas las lineas de la funcion, incluyendo def y return
        int totalLines = endLine - startLine + 1;
        
        return totalLines;
    }
}