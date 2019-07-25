package de.fhg.iais.roberta.syntax.lang.expr.eval.resources;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

public class EvalExprErrorListener extends BaseErrorListener {
    private final List<String> error = new LinkedList<String>();

    @Override
    public void syntaxError(
        @NotNull Recognizer<?, ?> recognizer,
        @Nullable Object offendingSymbol,
        int line,
        int charPositionInLine,
        @NotNull String msg,
        @Nullable RecognitionException e) {
        String s = "";
        List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
        Collections.reverse(stack);
        s += "rule stack: " + stack + "-> ";
        s += "line " + line + ":" + charPositionInLine + " at " + offendingSymbol + ": " + msg;
        this.error.add(s);
    }

    public List<String> getError() {
        return this.error;
    }
}
