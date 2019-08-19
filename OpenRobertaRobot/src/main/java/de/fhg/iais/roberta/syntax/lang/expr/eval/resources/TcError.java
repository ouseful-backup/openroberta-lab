package de.fhg.iais.roberta.syntax.lang.expr.eval.resources;

import java.util.HashMap;

/**
 * This class is used to keep errors from the ExprlyTypeChecker
 */
public class TcError {
    private final EvalExprError key;
    private final HashMap<String, String> error;
    private final String name;

    /**
     * Constructor for the class
     *
     * @param Message enum instance
     */
    private TcError(EvalExprError key) {
        this.key = key;
        this.name = null;
        this.error = null;
    }

    /**
     * Constructor for the class
     *
     * @param Message enum instance
     * @param name of the placeholder
     * @param value to be replaced
     */
    private TcError(EvalExprError key, String name, String value) {
        this.key = key;
        this.name = name;
        this.error = new HashMap<String, String>();
        this.error.put(name, value);
    }

    /**
     * Function to make an instance of a TypeCheck error
     *
     * @param Message enum instance
     */
    public static TcError setError(EvalExprError key) {
        return new TcError(key);
    }

    /**
     * Function to make an instance of a TypeCheck error
     *
     * @param Message enum instance
     * @param name of the placeholder
     * @param value to be replaced
     */
    public static TcError setError(EvalExprError key, String name, String value) {
        return new TcError(key, name, value);
    }

    /**
     * @return Error key
     */
    public EvalExprError getKey() {
        return this.key;
    }

    /**
     * @return Value to be replaced for a placeholder
     */
    public String getValue() {
        return this.error.get(this.name);
    }

    /**
     * @return Error message with the placeholder replaced
     */
    public String getError() {
        if ( this.name == null ) {
            return this.key.getMsg();
        } else {
            String message = "";
            String[] splitMessage = this.key.getMsg().split("[.{.}.]");
            for ( String s : splitMessage ) {
                message += this.error.get(s) == null ? s : "»" + this.error.get(s) + "«";
            }
            return message;
        }
    }
}
