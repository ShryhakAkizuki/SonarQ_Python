import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Set;

public class InsecureRandomForSecretsRule extends AnalysisRule {

    private static final Set<String> SECRET_NAMES = Set.of(
            "token", "secret", "key", "password", "passwd", "pwd", "otp", "code", "pin", "nonce"
    );

    private void checkAssignment(PythonParser.AssignmentContext ctx) {
        String target = SecurityRuleUtil.assignmentTarget(ctx);
        ParserRuleContext value = SecurityRuleUtil.assignmentValue(ctx);
        if (target == null || value == null) return;
        if (!SecurityRuleUtil.containsAny(target, SECRET_NAMES)) return;

        if (usesInsecureRandom(value.getText())) {
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Generador no criptografico usado para crear secreto en '" + target + "'");
        }
    }

    private static boolean usesInsecureRandom(String text) {
        String n = text.toLowerCase();
        return n.contains("random.")
                || n.contains("randint(")
                || n.contains("choice(")
                || n.contains("choices(")
                || n.contains("randrange(")
                || n.contains("getrandbits(")
                || n.contains("randbytes(");
    }

    @Override protected String name() { return "InsecureRandomForSecrets"; }

    @Override public void enterAssignment(PythonParser.AssignmentContext ctx) {
        checkAssignment(ctx);
    }
}
