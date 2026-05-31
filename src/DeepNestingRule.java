import org.antlr.v4.runtime.ParserRuleContext;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Detecta anidamiento excesivo de estructuras de control.
 * Umbral MEDIUM: >4 niveles, HIGH: >6 niveles
 */
public class DeepNestingRule extends AnalysisRule {

    private static final int MEDIUM_THRESHOLD = 4;
    private static final int HIGH_THRESHOLD = 6;
    
    private final Deque<NestingContext> nestingStack = new ArrayDeque<>();
    private int maxNesting = 0;
    private ParserRuleContext maxNestingContext = null;

    private record NestingContext(String type, int line) {}

    @Override
    protected String name() {
        return "DeepNesting";
    }

    // Incrementar nivel de anidamiento
    private void enterNesting(String type, ParserRuleContext ctx) {
        nestingStack.push(new NestingContext(type, ctx.start.getLine()));
        
        int currentLevel = nestingStack.size();
        if (currentLevel > maxNesting) {
            maxNesting = currentLevel;
            maxNestingContext = ctx;
        }
    }

    private void exitNesting() {
        if (!nestingStack.isEmpty()) {
            nestingStack.pop();
        }
    }

    // Estructuras de control que incrementan anidamiento
    @Override
    public void enterIf_stmt(PythonParser.If_stmtContext ctx) {
        enterNesting("if", ctx);
    }

    @Override
    public void exitIf_stmt(PythonParser.If_stmtContext ctx) {
        exitNesting();
    }

    @Override
    public void enterWhile_stmt(PythonParser.While_stmtContext ctx) {
        enterNesting("while", ctx);
    }

    @Override
    public void exitWhile_stmt(PythonParser.While_stmtContext ctx) {
        exitNesting();
    }

    @Override
    public void enterFor_stmt(PythonParser.For_stmtContext ctx) {
        enterNesting("for", ctx);
    }

    @Override
    public void exitFor_stmt(PythonParser.For_stmtContext ctx) {
        exitNesting();
    }

    @Override
    public void enterWith_stmt(PythonParser.With_stmtContext ctx) {
        enterNesting("with", ctx);
    }

    @Override
    public void exitWith_stmt(PythonParser.With_stmtContext ctx) {
        exitNesting();
    }

    @Override
    public void enterTry_stmt(PythonParser.Try_stmtContext ctx) {
        enterNesting("try", ctx);
    }

    @Override
    public void exitTry_stmt(PythonParser.Try_stmtContext ctx) {
        exitNesting();
    }

    // Al salir de cada funcion, reportar si hubo anidamiento excesivo
    @Override
    public void exitFunction_def_raw(PythonParser.Function_def_rawContext ctx) {
        if (maxNesting > HIGH_THRESHOLD && maxNestingContext != null) {
            flag(Severity.HIGH,
                 maxNestingContext.start.getLine(),
                 maxNestingContext.start.getCharPositionInLine(),
                 String.format("Anidamiento de %d niveles - refactorizar con early returns o extraer funciones", maxNesting));
        } else if (maxNesting > MEDIUM_THRESHOLD && maxNestingContext != null) {
            flag(Severity.MEDIUM,
                 maxNestingContext.start.getLine(),
                 maxNestingContext.start.getCharPositionInLine(),
                 String.format("Anidamiento de %d niveles - considerar simplificar", maxNesting));
        }
        
        // Reset para la siguiente funcion
        maxNesting = 0;
        maxNestingContext = null;
        nestingStack.clear();
    }
}
