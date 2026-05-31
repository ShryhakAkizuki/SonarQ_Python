import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.Set;

/**
 * Detecta numeros magicos (literales numericos sin contexto).
 * Sugiere extraer a constantes nombradas.
 */
public class MagicNumbersRule extends AnalysisRule {

    private static final Set<String> ALLOWED_NUMBERS = Set.of("0", "1", "-1", "0.0", "1.0");
    private boolean inConstantDefinition = false;
    private boolean inListLiteral = false;
    private String currentAssignmentTarget = null;

    @Override
    protected String name() {
        return "MagicNumbers";
    }

    @Override
    public void enterAssignment(PythonParser.AssignmentContext ctx) {
        // Detectar si estamos en una definicion de constante (UPPER_CASE)
        if (ctx.name() != null) {
            String varName = ctx.name().getText();
            inConstantDefinition = varName.equals(varName.toUpperCase());
            currentAssignmentTarget = varName;
        } else if (!ctx.star_targets().isEmpty()) {
            String varName = ctx.star_targets(0).getText();
            inConstantDefinition = varName.equals(varName.toUpperCase());
            currentAssignmentTarget = varName;
        }
    }

    @Override
    public void exitAssignment(PythonParser.AssignmentContext ctx) {
        inConstantDefinition = false;
        currentAssignmentTarget = null;
    }

    @Override
    public void enterList(PythonParser.ListContext ctx) {
        // Detectar cuando entramos a una lista literal
        inListLiteral = true;
        
        // Si la lista es grande (>10 elementos), sugerir extraer a metodo/constante
        // SOLO reportar la lista completa, no cada numero individual
        if (ctx.star_named_expressions() != null) {
            var expressions = ctx.star_named_expressions().star_named_expression();
            if (expressions != null && expressions.size() > 10) {
                flag(Severity.LOW,
                     ctx.start.getLine(),
                     ctx.start.getCharPositionInLine(),
                     String.format("Lista literal grande (%d elementos) - extraer a metodo o constante",
                         expressions.size()));
            }
        }
    }

    @Override
    public void exitList(PythonParser.ListContext ctx) {
        // Salir del contexto de lista literal
        inListLiteral = false;
    }

    @Override
    public void enterAtom(PythonParser.AtomContext ctx) {
        if (inConstantDefinition) return;
        
        // Ignorar TODOS los numeros en listas literales (pequeñas o grandes)
        // Si la lista es grande, ya se reporto en enterList()
        if (inListLiteral) return;
        
        // Verificar si el atom contiene un NUMBER terminal
        TerminalNode numberNode = ctx.NUMBER();
        if (numberNode == null) return;
        
        String number = numberNode.getText();
        
        // Ignorar numeros permitidos (0, 1, -1)
        if (ALLOWED_NUMBERS.contains(number)) return;
        
        // Ignorar asignaciones simples donde el numero coincide con el nombre
        // Ejemplo: linea_2 = 2, item_10 = 10
        if (currentAssignmentTarget != null && isSimpleNumberAssignment(currentAssignmentTarget, number)) {
            return;
        }
        
        // Ignorar numeros en notacion cientifica o hexadecimal
        if (number.contains("e") || number.contains("E") ||
            number.startsWith("0x") || number.startsWith("0X") ||
            number.startsWith("0b") || number.startsWith("0B") ||
            number.startsWith("0o") || number.startsWith("0O")) {
            return;
        }
        
        flag(Severity.LOW,
             numberNode.getSymbol().getLine(),
             numberNode.getSymbol().getCharPositionInLine(),
             String.format("Numero magico '%s' - extraer a constante nombrada", number));
    }

    /**
     * Verifica si estamos en una lista literal pequeña (probablemente datos de prueba).
     * Listas con <= 10 elementos se consideran "pequeñas".
     */
    private boolean isSmallListLiteral(PythonParser.AtomContext ctx) {
        // Buscar el contexto de lista padre
        ParserRuleContext parent = (ParserRuleContext) ctx.getParent();
        while (parent != null && !(parent instanceof PythonParser.ListContext)) {
            parent = parent.getParent();
        }
        
        if (parent instanceof PythonParser.ListContext) {
            PythonParser.ListContext listCtx = (PythonParser.ListContext) parent;
            // Contar elementos en la lista
            if (listCtx.star_named_expressions() != null) {
                var expressions = listCtx.star_named_expressions().star_named_expression();
                return expressions != null && expressions.size() <= 10;
            }
        }
        
        return false;
    }
    
    /**
     * Verifica si es una asignacion simple donde el numero coincide con el nombre.
     * Ejemplo: linea_2 = 2, item_10 = 10, valor_100 = 100
     */
    private boolean isSimpleNumberAssignment(String varName, String number) {
        // Extraer el sufijo numerico del nombre de variable
        String[] parts = varName.split("_");
        if (parts.length > 1) {
            String lastPart = parts[parts.length - 1];
            // Verificar si el ultimo segmento es el numero asignado
            try {
                int varNumber = Integer.parseInt(lastPart);
                int assignedNumber = Integer.parseInt(number);
                return varNumber == assignedNumber;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
}