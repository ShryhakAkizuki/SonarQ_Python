import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayDeque;
import java.util.Deque;





public class CyclomaticComplexityRule extends AnalysisRule {

    // ─────────── Variables ───────────
    private static final int CC_MEDIUM  = 10;
    private static final int CC_HIGH    = 20;
    private static final int PARAM_MAX  =  7;

    private record Fn(String name, int line, boolean isLambda, int cc, int params) {
        Fn inc(int n) { return new Fn(name, line, isLambda, cc + n, params); }
    }

    private final Deque<Fn> stack = new ArrayDeque<>();

    // ─────────── Metodos ───────────
    private void reportAndPop(Fn fn) {

        // ── Complejidad ciclomática ──
        if (fn.cc() > CC_HIGH)
            flag(Severity.HIGH  , fn.line(), 0, label(fn) + ": CC=" + fn.cc() + " — refactorizar urgente");
        else if (fn.cc() > CC_MEDIUM)
            flag(Severity.MEDIUM, fn.line(), 0, label(fn) + ": CC=" + fn.cc() + " — considerar refactorizar");

        // ── Exceso de parámetros (solo funciones regulares) ──
        if (!fn.isLambda() && fn.params() > PARAM_MAX)
            flag(Severity.LOW   , fn.line(), 0, label(fn) + ": " + fn.params() + " parámetros — agrupar en dataclass/objeto");
    }

    private static <T extends ParserRuleContext> int countNodes(ParseTree node, Class<T> type) {
        int count = type.isInstance(node) ? 1 : 0;
        for (int i = 0; i < node.getChildCount(); i++)
            count += countNodes(node.getChild(i), type);
        return count;
    }

    private static int countParams(PythonParser.Function_def_rawContext ctx) {
        if (ctx.params() == null) return 0;
        return countNodes(ctx.params(), PythonParser.ParamContext.class);
    }

    // ─────────── Helpers ───────────
    private void inc(int n) {
        if (n <= 0 || stack.isEmpty()) return;
        Fn top = stack.pop();
        stack.push(top.inc(n));
    }

    private static String label(Fn fn) {
        return (fn.isLambda() ? "Lambda" : "Función") + " '" + fn.name();
    }

    // ─────────── Overrides ───────────
    @Override protected String name() { return "CyclomaticComplexity"; }


    // Funciones regulares
    @Override
    public void enterFunction_def_raw(PythonParser.Function_def_rawContext ctx) {
        String fname = ctx.name() != null ? ctx.name().getText() : "<anónima>";
        stack.push(new Fn(fname, ctx.start.getLine(), false, 1, countParams(ctx)));
    }

    @Override
    public void exitFunction_def_raw(PythonParser.Function_def_rawContext ctx) {
        if (stack.isEmpty()) return;
        reportAndPop(stack.pop());
    }

    // Funciones lambda
    @Override
    public void enterLambdef(PythonParser.LambdefContext ctx) {
        int params = ctx.lambda_params() != null ? countNodes(ctx.lambda_params(), PythonParser.Lambda_paramContext.class) : 0;
        stack.push(new Fn("<lambda>", ctx.start.getLine(), true, 1, params));
    }

    @Override
    public void exitLambdef(PythonParser.LambdefContext ctx) {
        if (stack.isEmpty()) return;
        reportAndPop(stack.pop());
    }

    // Estructuras de control
    @Override public void enterIf_stmt           (PythonParser.If_stmtContext ctx)            { inc(1); }
    @Override public void enterElif_stmt         (PythonParser.Elif_stmtContext ctx)          { inc(1); }
    @Override public void enterWhile_stmt        (PythonParser.While_stmtContext ctx)         { inc(1); }
    @Override public void enterFor_stmt          (PythonParser.For_stmtContext ctx)           { inc(1); }
    @Override public void enterExcept_block      (PythonParser.Except_blockContext ctx)       { inc(1); }
    @Override public void enterExcept_star_block (PythonParser.Except_star_blockContext ctx)  { inc(1); }
    @Override public void enterCase_block        (PythonParser.Case_blockContext ctx)         { inc(1); }
    @Override public void enterGuard(PythonParser.GuardContext ctx)                           { inc(1); }

    // if a and b
    @Override
    public void enterConjunction(PythonParser.ConjunctionContext ctx) {
        inc((ctx.getChildCount() - 1) / 2);
    }

    // if a or b
    @Override
    public void enterDisjunction(PythonParser.DisjunctionContext ctx) {
        inc((ctx.getChildCount() - 1) / 2);
    }

    // expresion ternaria
    @Override
    public void enterExpression(PythonParser.ExpressionContext ctx) {
        if (ctx.disjunction().size() > 1) inc(1);
    }

    // for list comprehension [... if .. if ... ]
    @Override
    public void enterFor_if_clause(PythonParser.For_if_clauseContext ctx) {
        int filters = ctx.disjunction().size() - 1;
        if (filters > 0) inc(filters);
    }

}
