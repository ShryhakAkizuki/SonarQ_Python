import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Uso: java Main <archivo>");
            return;
        }
        CharStream input = CharStreams.fromFileName(args[0]);
        PythonLexer lexer = new PythonLexer(input);

        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> rec, Object sym,
                                    int line, int col,
                                    String msg, RecognitionException e) {
                System.err.printf("[Error Léxico] línea %d:%d %s%n", line, col, msg);
            }
        });

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PythonParser parser = new PythonParser(tokens);

        parser.removeErrorListeners();
        final boolean[] hasError = {false};
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> rec, Object sym,
                                    int line, int col,
                                    String msg, RecognitionException e) {
                hasError[0] = true;
                System.err.printf("[Error Sintáctico] línea %d:%d %s%n", line, col, msg);
            }
        });

        ParseTree tree = parser.file_input();

        if (hasError[0]) {
            return;
        }

        // Analisis del codigo
        var listener = new RulesListener();
        listener.analyze(tree, tokens);
        System.exit(listener.report());
    }
}


