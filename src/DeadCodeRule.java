import java.util.*;

/**
 * Detecta codigo muerto (no utilizado) en el programa.
 * Version simplificada: detecta bloques if False y codigo despues de return.
 */
public class DeadCodeRule extends AnalysisRule {

    private final Map<String, FunctionInfo> definedFunctions = new HashMap<>();
    private final Set<String> calledFunctions = new HashSet<>();
    private boolean inMainBlock = false;
    
    private record FunctionInfo(String name, int line, int column) {}

    @Override
    protected String name() {
        return "DeadCode";
    }

    @Override
    public void enterFile_input(PythonParser.File_inputContext ctx) {
        // Limpiar estado para nuevo archivo
        definedFunctions.clear();
        calledFunctions.clear();
    }

    @Override
    public void enterFunction_def_raw(PythonParser.Function_def_rawContext ctx) {
        // Registrar funcion definida con su ubicacion
        if (ctx.name() != null) {
            String funcName = ctx.name().getText();
            definedFunctions.put(funcName, new FunctionInfo(
                funcName,
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine()
            ));
        }
    }

    @Override
    public void enterIf_stmt(PythonParser.If_stmtContext ctx) {
        // Detectar if __name__ == "__main__": para excluir funciones llamadas ahi
        if (ctx.named_expression() != null) {
            String condition = ctx.named_expression().getText();
            
            // Detectar bloque if __name__ == "__main__":
            if (condition.contains("__name__") && condition.contains("__main__")) {
                inMainBlock = true;
            }
            
            // Detectar if False: o if 0:
            if (condition.equals("False") || condition.equals("0")) {
                flag(Severity.LOW,
                     ctx.start.getLine(),
                     ctx.start.getCharPositionInLine(),
                     String.format("Bloque 'if %s:' nunca se ejecuta - eliminar codigo muerto", condition));
            }
        }
    }

    @Override
    public void exitIf_stmt(PythonParser.If_stmtContext ctx) {
        // Salir del bloque if __name__ == "__main__":
        if (ctx.named_expression() != null) {
            String condition = ctx.named_expression().getText();
            if (condition.contains("__name__") && condition.contains("__main__")) {
                inMainBlock = false;
            }
        }
    }

    @Override
    public void enterReturn_stmt(PythonParser.Return_stmtContext ctx) {
        // Detectar codigo despues de return en la misma funcion
        // Buscar el contexto de la funcion padre
        var parent = ctx.getParent();
        while (parent != null && !(parent instanceof PythonParser.Function_def_rawContext)) {
            parent = parent.getParent();
        }
        
        if (parent instanceof PythonParser.Function_def_rawContext) {
            PythonParser.Function_def_rawContext funcCtx = (PythonParser.Function_def_rawContext) parent;
            
            // Verificar si hay statements despues del return
            if (funcCtx.block() != null && funcCtx.block().statements() != null) {
                var statements = funcCtx.block().statements().statement();
                boolean foundReturn = false;
                
                for (var stmt : statements) {
                    if (foundReturn && stmt.simple_stmts() != null) {
                        // Hay codigo despues del return
                        flag(Severity.MEDIUM,
                             stmt.start.getLine(),
                             stmt.start.getCharPositionInLine(),
                             "Codigo inalcanzable despues de 'return' - eliminar codigo muerto");
                        break;
                    }
                    
                    // Verificar si este statement contiene el return actual
                    if (stmt.simple_stmts() != null) {
                        for (var simpleStmt : stmt.simple_stmts().simple_stmt()) {
                            if (simpleStmt.return_stmt() != null && simpleStmt.return_stmt() == ctx) {
                                foundReturn = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void enterPrimary(PythonParser.PrimaryContext ctx) {
        // Detectar llamadas a funciones: primary '(' arguments? ')'
        // Si primary tiene un atom con NAME y luego hay '(', es una llamada
        if (ctx.atom() != null && ctx.atom().name() != null) {
            String funcName = ctx.atom().name().getText();
            
            // Verificar si hay '(' despues del nombre (indica llamada)
            String primaryText = ctx.getText();
            if (primaryText.contains("(")) {
                calledFunctions.add(funcName);
                
                // Si estamos en bloque if __name__ == "__main__":,
                // marcar todas las funciones llamadas ahi como usadas
                if (inMainBlock) {
                    // Agregar a un conjunto especial para no reportarlas
                    calledFunctions.add(funcName);
                }
            }
        }
    }

    @Override
    public void exitFile_input(PythonParser.File_inputContext ctx) {
        // NOTA: La deteccion de funciones no usadas esta DESHABILITADA
        // Razon: Requiere analisis de flujo completo para evitar falsos positivos
        // - Funciones llamadas en if __name__ == "__main__"
        // - Callbacks y handlers
        // - Funciones pasadas como parametros
        // - Llamadas dinamicas (getattr, eval, etc)
        // - Metodos de clase
        // - APIs publicas

        
        // Descomentar para habilitar la deteccion de funciones no usadas

        /*
        Set<String> unusedFunctionNames = new HashSet<>(definedFunctions.keySet());
        unusedFunctionNames.removeAll(calledFunctions);
        
        // Excluir patrones comunes
        unusedFunctionNames.removeIf(name ->
            name.startsWith("__") && name.endsWith("__") ||
            name.equals("main") || name.startsWith("test_") ||
            name.startsWith("on_") || name.startsWith("handle_") ||
            name.endsWith("_handler") || name.endsWith("_callback") ||
            name.startsWith("get_") || name.startsWith("post_") ||
            name.startsWith("put_") || name.startsWith("delete_") ||
            name.startsWith("patch_")
        );
        
        for (String funcName : unusedFunctionNames) {
            FunctionInfo info = definedFunctions.get(funcName);
            flag(Severity.LOW, info.line(), info.column(),
                 String.format("Funcion '%s' posiblemente no utilizada", funcName));
        }
        */
    }
}