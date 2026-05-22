import java.util.Set;

public class HardcodedCredentialsRule extends AnalysisRule {

    // ─────────── Variables ───────────
    private static final Set<String> KEYS = Set.of(
            "password","passwd","pwd","secret","api_key","apikey","token",
            "private_key","access_key","client_secret","auth_key","encryption_key"
    );

    // ─────────── Metodos ───────────
    /**  Revisa tanto la variable como el contenido para ver si es Hardcoded  */
    private void check(String left, String right, int line, int col) {
        if (KEYS.stream().noneMatch(left.toLowerCase()::contains)) return;
        if (!isStringLit(right) || right.length() <= 4) return;
        flag(Severity.CRITICAL, line, col, "Credencial hardcodeada en '" + left + "'");
    }

    // ─────────── Overrides ───────────
    @Override protected String name() { return "HardcodedCredentials"; }

    @Override
    public void enterAssignment(PythonParser.AssignmentContext ctx) {
        // Forma 1:  variable = "valor"
        if (!ctx.star_targets().isEmpty() && ctx.star_expressions() != null)
            check(ctx.star_targets(0).getText(),
                    ctx.star_expressions().getText(),
                    ctx.start.getLine(), ctx.start.getCharPositionInLine());

        // Forma 2:  variable: Tipo = "valor"
        if (ctx.name() != null && ctx.annotated_rhs() != null)
            check(ctx.name().getText(),
                    ctx.annotated_rhs().getText(),
                    ctx.start.getLine(), ctx.start.getCharPositionInLine());
    }


}
 