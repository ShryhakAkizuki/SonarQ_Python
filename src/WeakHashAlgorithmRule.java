import java.util.Set;

public class WeakHashAlgorithmRule extends AnalysisRule {

    private static final Set<String> WEAK_HASHES = Set.of("md5", "sha1");

    private void checkHash(PythonParser.PrimaryContext ctx) {
        String callable = SecurityRuleUtil.callable(ctx).toLowerCase();
        if (callable.isEmpty()) return;

        boolean directWeakHash = callable.equals("md5")
                || callable.equals("sha1")
                || callable.endsWith(".md5")
                || callable.endsWith(".sha1")
                || callable.endsWith(".md5.new")
                || callable.endsWith(".sha1.new");

        String args = SecurityRuleUtil.argsText(ctx).toLowerCase();
        boolean hmacWeakDigest = callable.endsWith("hmac.new")
                && (args.contains("digestmod=hashlib.md5")
                || args.contains("digestmod=hashlib.sha1")
                || args.contains("digestmod=\"md5\"")
                || args.contains("digestmod='md5'")
                || args.contains("digestmod=\"sha1\"")
                || args.contains("digestmod='sha1'"));

        if (directWeakHash || hmacWeakDigest) {
            String algorithm = SecurityRuleUtil.containsAny(callable + args, WEAK_HASHES) ? "MD5/SHA1" : "hash debil";
            flag(Severity.HIGH, ctx.start.getLine(), ctx.start.getCharPositionInLine(),
                    "Algoritmo de hash debil usado en contexto de seguridad: " + algorithm);
        }
    }

    @Override protected String name() { return "WeakHashAlgorithm"; }

    @Override public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkHash(ctx);
    }
}
