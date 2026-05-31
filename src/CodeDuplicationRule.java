import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import java.util.*;

/**
 * Detecta bloques de codigo duplicados que deberian extraerse a funciones.
 * Compara secuencias de statements para encontrar duplicacion.
 */
public class CodeDuplicationRule extends AnalysisRule {

    private static final int MIN_DUPLICATE_LINES = 5;
    private final List<CodeBlock> codeBlocks = new ArrayList<>();
    
    private record CodeBlock(String signature, int startLine, int endLine, String context) {}

    @Override
    protected String name() {
        return "CodeDuplication";
    }

    @Override
    public void enterFunction_def_raw(PythonParser.Function_def_rawContext ctx) {
        // Extraer bloques de codigo de la funcion
        if (ctx.block() != null) {
            extractCodeBlocks(ctx, ctx.block(), ctx.name() != null ? ctx.name().getText() : "<anonima>");
        }
    }

    @Override
    public void exitFile_input(PythonParser.File_inputContext ctx) {
        // Al final del archivo, comparar todos los bloques encontrados
        findDuplicates();
    }

    /**
     * Extrae bloques de codigo de un contexto de bloque.
     * Usa el bloque completo de la funcion para evitar superposiciones.
     */
    private void extractCodeBlocks(PythonParser.Function_def_rawContext funcCtx, PythonParser.BlockContext block, String functionName) {
        if (block.statements() == null) return;
        
        List<PythonParser.StatementContext> statements = block.statements().statement();
        if (statements == null || statements.size() < MIN_DUPLICATE_LINES) return;
        
        // Extraer el bloque completo de la funcion
        String signature = generateSignature(statements);
        if (signature != null && !signature.isEmpty()) {
            // Usar la linea de la definicion de la funcion (def), no la primera statement
            int startLine = funcCtx.start.getLine();
            int endLine = statements.get(statements.size() - 1).stop.getLine();
            
            codeBlocks.add(new CodeBlock(signature, startLine, endLine, functionName));
        }
    }

    /**
     * Genera una firma normalizada de un bloque de codigo.
     * Ignora nombres de variables pero mantiene estructura.
     */
    private String generateSignature(List<PythonParser.StatementContext> statements) {
        StringBuilder signature = new StringBuilder();
        
        for (PythonParser.StatementContext stmt : statements) {
            String normalized = normalizeStatement(stmt);
            if (normalized != null && !normalized.isEmpty()) {
                signature.append(normalized).append(";");
            }
        }
        
        return signature.toString();
    }

    /**
     * Normaliza un statement para comparacion.
     * Reemplaza identificadores con placeholders pero mantiene estructura.
     */
    private String normalizeStatement(PythonParser.StatementContext stmt) {
        if (stmt == null) return "";
        
        String text = stmt.getText();
        
        // Ignorar comentarios y lineas vacias
        if (text.trim().isEmpty() || text.trim().startsWith("#")) {
            return "";
        }
        
        // Normalizar: mantener palabras clave y operadores, reemplazar identificadores
        text = text.replaceAll("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b", "VAR");
        text = text.replaceAll("\\d+", "NUM");
        text = text.replaceAll("\"[^\"]*\"", "STR");
        text = text.replaceAll("'[^']*'", "STR");
        
        return text;
    }

    /**
     * Encuentra y reporta bloques duplicados.
     * Solo reporta la primera ocurrencia de cada bloque duplicado para evitar inflar el conteo.
     */
    private void findDuplicates() {
        Map<String, List<CodeBlock>> signatureMap = new HashMap<>();
        
        // Agrupar bloques por firma
        for (CodeBlock block : codeBlocks) {
            signatureMap.computeIfAbsent(block.signature(), k -> new ArrayList<>()).add(block);
        }
        
        // Reportar duplicados - solo la primera ocurrencia de cada grupo
        Set<String> reported = new HashSet<>();
        
        for (Map.Entry<String, List<CodeBlock>> entry : signatureMap.entrySet()) {
            List<CodeBlock> blocks = entry.getValue();
            
            if (blocks.size() > 1) {
                String signature = entry.getKey();
                
                // Evitar reportar el mismo bloque multiples veces
                if (reported.contains(signature)) continue;
                reported.add(signature);
                
                // Reportar SOLO la primera ocurrencia del bloque duplicado
                CodeBlock firstBlock = blocks.get(0);
                int lines = firstBlock.endLine() - firstBlock.startLine() + 1;
                
                // Construir lista de ubicaciones donde se encontro
                StringBuilder locations = new StringBuilder();
                for (int i = 0; i < Math.min(blocks.size(), 3); i++) {
                    if (i > 0) locations.append(", ");
                    locations.append(blocks.get(i).context()).append(":").append(blocks.get(i).startLine());
                }
                if (blocks.size() > 3) {
                    locations.append("...");
                }
                
                flag(Severity.MEDIUM,
                     firstBlock.startLine(),
                     0,
                     String.format("Codigo duplicado (%d lineas) encontrado %d veces en: %s - extraer a funcion reutilizable",
                         lines, blocks.size(), locations.toString()));
            }
        }
    }
}