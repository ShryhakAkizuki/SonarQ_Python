import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Set;

public class HardcodedCryptoKeyRule extends AnalysisRule {

    private static final Set<String> CRYPTO_NAMES = Set.of(
            "key", "crypto_key", "encryption_key", "signing_key", "private_key",
            "secret_key", "iv", "nonce", "salt", "aes_key", "jwt_secret"
    );

    private void checkAssignment(PythonParser.AssignmentContext ctx) {
        String target = SecurityRuleUtil.assignmentTarget(ctx);
        ParserRuleContext value = SecurityRuleUtil.assignmentValue(ctx);
        if (target == null || value == null) return;
        if (!SecurityRuleUtil.containsAny(target, CRYPTO_NAMES)) return;
        if (!SecurityRuleUtil.isHardcodedString(value)) return;

        String literal = SecurityRuleUtil.stripQuotes(value.getText());
        if (literal.length() < 8) return;

        flag(Severity.CRITICAL, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                "Clave, IV, nonce o salt criptografico hardcodeado en '" + target + "'");
    }

    @Override protected String name() { return "HardcodedCryptoKey"; }

    @Override public void enterAssignment(PythonParser.AssignmentContext ctx) {
        checkAssignment(ctx);
    }
}
