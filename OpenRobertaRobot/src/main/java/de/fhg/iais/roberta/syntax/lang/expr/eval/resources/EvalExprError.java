package de.fhg.iais.roberta.syntax.lang.expr.eval.resources;

public enum EvalExprError {
    EXPRBLOCK_TYPECHECK_ERROR_INVALID_OPERAND_TYPE( "Invalid type of operand in expression!" ),
    EXPRBLOCK_TYPECHECK_ERROR_UNEXPECTED_RETURN_TYPE( "Wrong type of return value from expression!" ),
    EXPRBLOCK_TYPECHECK_ERROR_INVALID_TYPE_FOR_LIST_ELEMENT( "All elements on the list must have the same type." ),
    EXPRBLOCK_TYPECHECK_ERROR_INVALID_ARGUMENT_NUMBER(
        "Wrong number of arguments in function call (Check help for more info on the arguments of the function)." ),
    EXPRBLOCK_TYPECHECK_ERROR_INVALID_ARGUMENT_TYPE( "The expression {EXPR} has the wrong type of argument in function call." ),
    EXPRBLOCK_TYPECHECK_ERROR_UNDECLARED_VARIABLE( "Variable {NAME} not declared" ),
    EXPRBLOCK_TYPECHECK_ERROR_UNEXPECTED_METHOD( "You cannot use void methods in that expression." );

    private String msg;

    private EvalExprError(String keyStr) {
        this.msg = keyStr;
    }

    public String getMsg() {
        return this.msg;
    }
}
