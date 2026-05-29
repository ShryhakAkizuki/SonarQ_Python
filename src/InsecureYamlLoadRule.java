import java.util.HashSet;
import java.util.Set;

public class InsecureYamlLoadRule extends AnalysisRule {

    // ----------- Variables -----------
    private final Set<String> yamlAliases = new HashSet<>(Set.of("yaml"));
    private final Set<String> loadAliases = new HashSet<>();
    private final Set<String> fullLoadAliases = new HashSet<>();
    private final Set<String> unsafeLoadAliases = new HashSet<>();

    // ----------- Metodos -----------
    private void checkYamlCall(PythonParser.PrimaryContext ctx) {
        if (ctx.LPAR() == null || ctx.primary() == null) return;

        String callable = ctx.primary().getText();
        String args = ctx.arguments() != null ? ctx.arguments().getText() : "";

        if (isSafeLoadCall(callable)) return;

        if (isFullLoadCall(callable)) {
            flagYaml(ctx, "yaml.full_load() permite construir objetos Python mas alla de tipos seguros");
            return;
        }

        if (isUnsafeLoadCall(callable)) {
            flagYaml(ctx, "yaml.unsafe_load() permite deserializacion insegura de objetos");
            return;
        }

        if (isYamlLoadCall(callable) && !usesSafeLoader(args)) {
            flagYaml(ctx, "yaml.load() sin SafeLoader/safe_load");
        }
    }

    private void flagYaml(PythonParser.PrimaryContext ctx, String reason) {
        flag(Severity.HIGH,
                ctx.start.getLine(),
                ctx.start.getCharPositionInLine(),
                "Carga YAML insegura: " + reason);
    }

    private void registerYamlImport(String text) {
        if (text.startsWith("importyamlas")) {
            yamlAliases.add(text.substring("importyamlas".length()));
            return;
        }

        if (text.equals("importyaml") || text.startsWith("importyaml,")) {
            yamlAliases.add("yaml");
            return;
        }

        if (!text.startsWith("fromyamlimport")) return;

        String imported = text.substring("fromyamlimport".length());
        for (String part : imported.split(",")) {
            registerImportedName(part);
        }
    }

    private void registerImportedName(String part) {
        if (part.isEmpty()) return;

        String name = part;
        String alias = null;
        int asIndex = part.indexOf("as");
        if (asIndex > 0) {
            name = part.substring(0, asIndex);
            alias = part.substring(asIndex + 2);
        }

        String effectiveName = alias != null && !alias.isEmpty() ? alias : name;
        switch (name) {
            case "load" -> loadAliases.add(effectiveName);
            case "full_load" -> fullLoadAliases.add(effectiveName);
            case "unsafe_load" -> unsafeLoadAliases.add(effectiveName);
            default -> { }
        }
    }

    // ----------- Helpers -----------
    private boolean isYamlLoadCall(String callable) {
        if (loadAliases.contains(callable)) return true;
        return yamlAliases.stream().anyMatch(alias -> callable.equals(alias + ".load"));
    }

    private boolean isFullLoadCall(String callable) {
        if (fullLoadAliases.contains(callable)) return true;
        return yamlAliases.stream().anyMatch(alias -> callable.equals(alias + ".full_load"));
    }

    private boolean isUnsafeLoadCall(String callable) {
        if (unsafeLoadAliases.contains(callable)) return true;
        return yamlAliases.stream().anyMatch(alias -> callable.equals(alias + ".unsafe_load"));
    }

    private boolean isSafeLoadCall(String callable) {
        return yamlAliases.stream().anyMatch(alias -> callable.equals(alias + ".safe_load"))
                || callable.equals("safe_load");
    }

    private static boolean usesSafeLoader(String args) {
        String normalized = args.replace(" ", "");
        return normalized.contains("SafeLoader")
                || normalized.contains("CSafeLoader")
                || normalized.contains("loader=SafeLoader")
                || normalized.contains("Loader=SafeLoader")
                || normalized.contains("Loader=yaml.SafeLoader")
                || normalized.contains("Loader=yaml.CSafeLoader");
    }

    // ----------- Overrides -----------
    @Override protected String name() { return "InsecureYamlLoad"; }

    @Override
    public void enterImport_stmt(PythonParser.Import_stmtContext ctx) {
        registerYamlImport(ctx.getText());
    }

    @Override
    public void enterPrimary(PythonParser.PrimaryContext ctx) {
        checkYamlCall(ctx);
    }
}
