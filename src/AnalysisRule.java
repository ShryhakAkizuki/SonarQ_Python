import org.antlr.v4.runtime.tree.ParseTree;
import java.util.ArrayList;
import java.util.List;

public abstract class AnalysisRule extends PythonParserBaseListener {

    // ─────────── Variables ───────────
    protected final List<Issue> issues = new ArrayList<>();

    // ─────────── Metodos Base ───────────
    public    List<Issue> getIssues()  { return issues; }
    protected abstract String name();

    protected void flag(Severity s, int line, int col, String msg) {
        issues.add(new Issue(s, name(), line, col, msg));
    }

    // ─────────── Funciones utiles - Compartidas ───────────

    /** ¿El texto parece un literal de cadena Python? */
    protected static boolean isStringLit(String t) {
        return t != null && t.replaceFirst("(?i)^[brurf]{0,3}", "").matches("^[\"'].*");
    }

    /** ¿Algún nodo del subárbol es instancia de {@code type}? */
    protected static boolean hasDescendant(ParseTree node, Class<?> type) {
        if (type.isInstance(node)) return true;
        for (int i = 0; i < node.getChildCount(); i++)
            if (hasDescendant(node.getChild(i), type)) return true;
        return false;
    }

    /** ¿Hay un operador + en algún SumContext del subárbol? */
    protected static boolean hasPlusConcat(ParseTree n) {
        if (n instanceof PythonParser.SumContext s && s.PLUS() != null) return true;
        for (int i = 0; i < n.getChildCount(); i++)
            if (hasPlusConcat(n.getChild(i))) return true;
        return false;
    }
}