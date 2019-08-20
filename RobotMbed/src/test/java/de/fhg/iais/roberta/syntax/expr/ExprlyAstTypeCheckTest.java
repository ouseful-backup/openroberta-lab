package de.fhg.iais.roberta.syntax.expr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.iais.roberta.exprly.generated.ExprlyLexer;
import de.fhg.iais.roberta.exprly.generated.ExprlyParser;
import de.fhg.iais.roberta.exprly.generated.ExprlyParser.ExpressionContext;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.VarDeclaration;
import de.fhg.iais.roberta.syntax.lang.expr.eval.resources.ExprlyAST;
import de.fhg.iais.roberta.syntax.lang.expr.eval.resources.ExprlyTypechecker;
import de.fhg.iais.roberta.syntax.lang.expr.eval.resources.TcError;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.test.GenericHelperForXmlTest;

public class ExprlyAstTypeCheckTest {
    private static final Logger LOG = LoggerFactory.getLogger(ExprlyAstTypeCheckTest.class);
    private final GenericHelperForXmlTest h = new GenericHelperForXmlTest();

    /**
     * The purpose of this set of test is to try out the typeChecker for the exprBlock.
     * All the messages from the bad expression are printed.
     */

    @Test
    public void typeCheckEq() throws Exception {
        typeCheck("\"Hello \" + 42 == isEmpty([0,0,0])", BlocklyType.BOOLEAN);
    }

    @Test
    public void typeCheckMathSingleFunctCall() throws Exception {
        typeCheck("phi^true", BlocklyType.BOOLEAN);
    }

    @Test
    public void typeCheckMathOnListFunct() throws Exception {
        typeCheck("sum(1, 2, 3, 4, 5)+avg([])-median([1, randFloat()])", BlocklyType.NUMBER);
    }

    @Test
    public void typeCheckMathOnListFunct1() throws Exception {
        typeCheck("sum(1, 2, 3, 4, 5)+avg([])-median([x, randFloat()])", BlocklyType.NUMBER);
    }

    @Test
    public void typeCheckMathRandFloatFunct() throws Exception {
        typeCheck("randFloat(0, randInt(0, 2))", BlocklyType.NUMBER);
    }

    @Test
    public void typeCheckBadArgument0() throws Exception {
        typeCheck("sum([1,\"hel\",\"lo\"])", BlocklyType.NUMBER);
    }

    @Test
    public void typeCheckBadArgument1() throws Exception {
        typeCheck("sum([1,\"hel\",2])", BlocklyType.NUMBER);
    }

    @Test
    public void typeCheckBadArgument2() throws Exception {
        typeCheck("sum([1,\"hel\",\"lo\",2])", BlocklyType.NUMBER);
    }

    @Test
    public void typeCheckBadRGB() throws Exception {
        typeCheck("sum(getRGB(1)+!22)", BlocklyType.NUMBER);
    }

    @Test
    public void typeCheckBadOp() throws Exception {
        typeCheck("sqrt(repeatList(true,4)+subList([1,2,false],\"hello\"))", BlocklyType.NUMBER);
    }

    @Test
    public void typeCheckListList() throws Exception {
        typeCheck("[[1,2], 1]", BlocklyType.ARRAY);
    }

    @Test
    public void typeCheckTernary() throws Exception {
        typeCheck("\"one\"?1:false", BlocklyType.NUMBER);
    }

    @Test
    public void typeCheckTernaryArg() throws Exception {
        typeCheck("subList(true?[1,true]:[2,3],0,1)", BlocklyType.ARRAY_NUMBER);
    }

    @Test
    public void typeCheckWeDo() throws Exception {
        typeCheck("max([0, 1])", BlocklyType.NUMBER, "wedo");
    }

    @Test
    public void typeCheckNxt() throws Exception {
        typeCheck("repeatList(5,5)", BlocklyType.ARRAY_NUMBER, "nxt");
        typeCheck("subList([1,2,3,4],0,2)", BlocklyType.ARRAY_NUMBER, "nxt");
        typeCheck("createTextWith(\"Error\", \"test\", \"1\")", BlocklyType.STRING, "nxt");
    }

    @Test
    public void typeCheckMicrobit() throws Exception {
        typeCheck("#000000", BlocklyType.COLOR, "microbit");
    }

    @Test
    public void typeCheckNao() throws Exception {
        typeCheck("getRGB(0,0)", BlocklyType.COLOR, "nao");
    }

    @Test
    public void typeCheckArduino() throws Exception {
        typeCheck("#123456", BlocklyType.COLOR, "arduino");
    }

    @Test
    public void typeCheckCalliope() throws Exception {
        typeCheck("getRGB(0,0)", BlocklyType.COLOR, "calliope");
    }

    /**
     * Function to create ast
     */
    private Expr<Void> expr2AST(String expr) throws Exception {
        ExprlyParser parser = mkParser(expr);
        ExprlyAST<Void> eval = new ExprlyAST<>();
        ExpressionContext expression = parser.expression();
        Expr<Void> block = eval.visitExpression(expression);
        return block;
    }

    /**
     * Function to create the parser for the expression
     */
    private ExprlyParser mkParser(String expr) throws UnsupportedEncodingException, IOException {
        InputStream inputStream = new ByteArrayInputStream(expr.getBytes("UTF-8"));
        ANTLRInputStream input = new ANTLRInputStream(inputStream);
        ExprlyLexer lexer = new ExprlyLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprlyParser parser = new ExprlyParser(tokens);
        return parser;
    }

    /**
     * Function to typeCheck an expression and print it's errors
     */
    public void typeCheck(String expr, BlocklyType result) throws Exception {
        Expr<Void> e = expr2AST(expr);
        ExprlyTypechecker<Void> c = new ExprlyTypechecker<Void>(e, result);
        c.check();
        System.out.println("Typecheck Test for " + expr + " :");
        List<TcError> errors = c.getErrors();
        for ( TcError s : errors ) {
            System.out.println(s.getError());
        }
        System.out.println("");
    }

    /**
     * Function to typeCheck an expression and print it's errors
     */
    public void typeCheck(String expr, BlocklyType result, String robotName) throws Exception {
        Expr<Void> e = expr2AST(expr);
        ExprlyTypechecker<Void> c = new ExprlyTypechecker<Void>(e, result, new ArrayList<VarDeclaration<Void>>(), robotName);
        c.check();
        System.out.println("Typecheck Test for " + expr + " on " + robotName + " :");
        List<TcError> errors = c.getErrors();
        for ( TcError s : errors ) {
            System.out.println(s.getError());
        }
        System.out.println("");
    }
}