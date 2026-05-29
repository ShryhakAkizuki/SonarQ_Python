import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.Arrays;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Orquesta las reglas y genera el reporte.
 * Cada regla recorre el árbol de forma independiente con ParseTreeWalker.
 */
public class RulesListener {

    private final List<AnalysisRule> rules = List.of(
            new HardcodedCredentialsRule(),
            new SQLInjectionConcatRule(),
            new InsecureDeserializationPickleRule(),
            new InsecureYamlLoadRule(),
            new InsecureCookieConfigRule(),
            new CyclomaticComplexityRule()
    );

    public void analyze(ParseTree tree) {
        rules.forEach(r -> ParseTreeWalker.DEFAULT.walk(r, tree));
    }

    private List<Issue> issues() {
        return rules.stream()
                .flatMap(r -> r.getIssues().stream())
                .sorted(Comparator.comparing(Issue::severity).thenComparingInt(Issue::line))
                .toList();
    }

    // ── Reporte en consola ────────────────────────────────────────────────

    private static final String SEP = "─".repeat(70);

    public int report() {
        var issues = issues();
        System.out.println(SEP);
        if (issues.isEmpty()) {
            System.out.println("OK: Sin incidencias detectadas.");
        } else {
            issues.forEach(i -> System.out.println("  " + i));
            System.out.println(SEP);

            var counts = issues.stream()
                    .collect(Collectors.groupingBy(Issue::severity, Collectors.counting()));
            System.out.print("  Total: " + issues.size() + "  |  ");
            Arrays.stream(Severity.values())
                    .filter(counts::containsKey)
                    .forEach(s -> System.out.print(s + ": " + counts.get(s) + "  "));
            System.out.println();
        }
        System.out.println(SEP);
        // Exit code: 1 si hay CRITICAL o HIGH, 0 si no
        return issues.stream().anyMatch(i -> i.severity().ordinal() < Severity.MEDIUM.ordinal()) ? 1 : 0;
    }
}
