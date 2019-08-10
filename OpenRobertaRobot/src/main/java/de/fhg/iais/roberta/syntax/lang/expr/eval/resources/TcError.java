package de.fhg.iais.roberta.syntax.lang.expr.eval.resources;

import java.util.HashMap;

public class TcError {
    private final EvalExprError key;
    private final HashMap<String, String> error;
    private final String name;

    private TcError(EvalExprError key) {
        this.key = key;
        this.name = null;
        this.error = null;
    }

    private TcError(EvalExprError key, String name, String value) {
        this.key = key;
        this.name = name;
        this.error = new HashMap<String, String>();
        this.error.put(name, value);
    }

    public static TcError setError(EvalExprError key) {
        return new TcError(key);
    }

    public static TcError setError(EvalExprError key, String name, String value) {
        return new TcError(key, name, value);
    }

    public EvalExprError getKey() {
        return this.key;
    }

    public String getValue() {
        return this.error.get(this.name);
    }

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
