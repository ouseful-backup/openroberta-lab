package de.fhg.iais.roberta.syntax.lang.expr.eval.resources;

public enum EvalExprError {
    EXPRBLOCK_TYPECHECK_ERROR_INVALID_OPERAND_TYPE( "Invalid type of operand in expression!" ),
    EXPRBLOCK_TYPECHECK_ERROR_UNEXPECTED_RETURN_TYPE( "Wrong type of return value from expression!" ),
    EXPRBLOCK_TYPECHECK_ERROR_INVALID_TYPE_FOR_LIST_ELEMENT( "All elements on the list must have the same type." ),
    EXPRBLOCK_TYPECHECK_ERROR_INVALID_ARGUMENT_NUMBER(
        "Wrong number of arguments in function call (Check help for more info on the arguments of the function)." ),
    EXPRBLOCK_TYPECHECK_ERROR_INVALID_ARGUMENT_TYPE( "The expression {EXPR} is the wrong type of argument for the function call." ),
    EXPRBLOCK_TYPECHECK_ERROR_UNDECLARED_VARIABLE( "Variable {NAME} not declared." ),
    EXPRBLOCK_TYPECHECK_ERROR_INVALID_COLOR( "The color {COLOR} is invalid for the current robot." ),
    EXPRBLOCK_TYPECHECK_ERROR_ILLEGAL_RGB( "The current robot can't use RGB colors." ),
    EXPRBLOCK_TYPECHECK_ERROR_INVALID_RGB_RGBA( "The getRGB function takes {NUM} parameters for this robot." ),
    EXPRBLOCK_TYPECHECK_ERROR_INVALID_BLOCK_FOR_ROBOT( "The expression {EXPR} isn't valid for the current robot." ),
    EXPRBLOCK_TYPECHECK_ERROR_UNEXPECTED_METHOD( "You cannot use void methods in that expression." ),
    EXPRBLOCK_TYPECHECK_ERROR_NO_TYPE( "The current robot doesn't support the use of {TYPE}." ),
    EXPRBLOCK_TYPECHECK_ERROR_NO_FUNCT( "The {FUNCT} function isn't supported for the current robot." );

    private String msg;

    /**
     * Constructor for the class, sets the message
     *
     * @param message to be stored
     */
    private EvalExprError(String keyStr) {
        this.msg = keyStr;
    }

    /**
     * Function to get message for the key
     *
     * @return message
     */
    public String getMsg() {
        return this.msg;
    }
}
