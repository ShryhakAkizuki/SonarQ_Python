import org.antlr.v4.runtime.ParserRuleContext;
import java.util.Set;

public class HardcodedCredentialsRule extends AnalysisRule {

    // ─────────── Variables ───────────

    private static final Set<String> KEYS = Set.of(
            "password", "passwd", "pwd", "secret", "api_key", "apikey", "token",
            "private_key", "access_key", "client_secret", "auth_key", "encryption_key"
    );

    private static final Set<String> PLACEHOLDERS = Set.of(
            "changeme", "todo", "fixme", "xxx", "dummy", "password", "secret",
            "yourpassword", "replace_me", "example", "insert_here"
    );

    private static final Set<String> TEST_PREFIXES = Set.of(
            "test_", "example_", "demo_", "sample_", "fake_", "mock_", "dummy_"
    );

    // ─────────── Metodos ───────────

    private void check(String left, ParserRuleContext rightCtx, int line, int col) {

        // El nombre contiene una palabra clave sensible?
        String leftNorm = left.toLowerCase();
        if (KEYS.stream().noneMatch(leftNorm::contains)) return;

        // El nombre tiene un prefijo de prueba/ejemplo?
        if (TEST_PREFIXES.stream().anyMatch(leftNorm::startsWith)) return;

        String right = rightCtx.getText();

        // El RightSide parece un literal de cadena?
        if (!isStringLit(right)) return;

        // Es una concatenación?
        if (hasPlusConcat(rightCtx)) return;

        // Es un f-string con interpolación?
        if (hasDescendant(rightCtx, PythonParser.Fstring_replacement_fieldContext.class)) return;

        String value = stripQuotes(right);

        // El valor contiene comillas internas?
        if (value.contains("\"") || value.contains("'")) return;

        // El valor es demasiado corto para ser una credencial real?
        if (value.length() <= 4) return;

        // El valor es un placeholder conocido?
        if (PLACEHOLDERS.contains(value.toLowerCase())) return;

        // El valor tiene contenido legible tras eliminar secuencias de escape?
        if (!hasReadableContent(value)) return;

        flag(Severity.CRITICAL, line, col, "Credencial hardcodeada en '" + left + "'");
    }

    // ─────────── Helpers ───────────

    /** Quita prefijos de tipo (b, r, f, u…) y comillas externas del texto. */
    private static String stripQuotes(String text) {
        String s = text.replaceFirst("(?i)^[brurf]{0,3}", "");
        if (s.length() >= 6 && (s.startsWith("\"\"\"") || s.startsWith("'''")))
            return s.substring(3, s.length() - 3);
        if (s.length() >= 2)
            return s.substring(1, s.length() - 1);
        return s;
    }

    /**
     * El valor contiene caracteres alfanuméricos legibles tras eliminar secuencias de escape?
     */
    private static boolean hasReadableContent(String value) {
        String clean = value
                .replaceAll("\\\\[xuU][0-9a-fA-F]+", "")   // \xFF  \uFFFF  \UFFFFFFFF
                .replaceAll("\\\\[nrtbf0\\\\'\"]", "");      // \n \r \t \b \f \0 \\ \' \"
        return clean.chars().anyMatch(Character::isLetterOrDigit);
    }

    // ─────────── Overrides ───────────

    @Override protected String name() { return "HardcodedCredentials"; }

    @Override
    public void enterAssignment(PythonParser.AssignmentContext ctx) {

        // Forma 1:  variable = "valor"
        if (!ctx.star_targets().isEmpty() && ctx.star_expressions() != null)
            check(ctx.star_targets(0).getText(),
                    ctx.star_expressions(),
                    ctx.start.getLine(), ctx.start.getCharPositionInLine());

        // Forma 2:  variable: Tipo = "valor"
        if (ctx.name() != null && ctx.annotated_rhs() != null)
            check(ctx.name().getText(),
                    ctx.annotated_rhs(),
                    ctx.start.getLine(), ctx.start.getCharPositionInLine());
    }
}