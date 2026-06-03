import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Detecta comentarios TODO, FIXME, HACK, XXX, BUG que indican trabajo pendiente.
 * Severidad segun tipo: BUG/FIXME=MEDIUM, HACK=LOW, TODO/XXX=INFO
 *
 * Nota: Los comentarios estan en el canal oculto, por lo que necesitamos
 * acceder al TokenStream directamente.
 */
public class TodoCommentsRule extends AnalysisRule {

    private static final Pattern TODO_PATTERN = Pattern.compile(
        "#\\s*(TODO|FIXME|HACK|XXX|BUG)\\s*:?\\s*(.+)",
        Pattern.CASE_INSENSITIVE
    );
    
    private CommonTokenStream tokenStream;
    private boolean analyzed = false;

    @Override
    protected String name() {
        return "TodoComments";
    }
    
    public void setTokenStream(CommonTokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    @Override
    public void enterFile_input(PythonParser.File_inputContext ctx) {
        // Solo analizar una vez por archivo
        if (analyzed || tokenStream == null) return;
        analyzed = true;
        
        // Obtener todos los tokens del archivo (incluyendo canal oculto)
        List<Token> allTokens = tokenStream.getTokens();
        
        for (Token token : allTokens) {
            // Solo procesar comentarios
            if (token.getType() == PythonLexer.COMMENT) {
                checkComment(token);
            }
        }
    }
    
    private void checkComment(Token token) {
        String comment = token.getText();
        Matcher matcher = TODO_PATTERN.matcher(comment);
        
        if (matcher.find()) {
            String type = matcher.group(1).toUpperCase();
            String description = matcher.group(2).trim();
            
            // Limitar longitud de descripcion para el reporte
            if (description.length() > 60) {
                description = description.substring(0, 57) + "...";
            }
            
            Severity severity = switch (type) {
                case "BUG", "FIXME" -> Severity.MEDIUM;
                case "HACK" -> Severity.LOW;
                default -> Severity.INFO;
            };
            
            flag(severity,
                 token.getLine(),
                 token.getCharPositionInLine(),
                 String.format("%s: %s", type, description));
        }
    }
}