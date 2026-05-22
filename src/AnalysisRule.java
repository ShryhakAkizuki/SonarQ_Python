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

    /** El texto parece un literal de cadena Python? */
    protected static boolean isStringLit(String t) {
        return t != null && t.replaceFirst("(?i)^[brurf]{0,3}", "").matches("^[\"'].*");
    }

}