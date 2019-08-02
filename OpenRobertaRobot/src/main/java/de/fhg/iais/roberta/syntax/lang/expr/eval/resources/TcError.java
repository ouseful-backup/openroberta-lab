package de.fhg.iais.roberta.syntax.lang.expr.eval.resources;

import org.json.JSONObject;

import de.fhg.iais.roberta.util.Key;

public class TcError {
    private final Key key;
    private final JSONObject error;

    private TcError(Key key) {
        this.key = key;
        this.error = null;
    }

    private TcError(Key key, String name, String value) {
        this.key = key;
        this.error = new JSONObject();
        this.error.put(name, value);
    }

    public static TcError setError(Key key) {
        return new TcError(key);
    }

    public static TcError setError(Key key, String name, String value) {
        return new TcError(key, name, value);
    }

    public Key getKey() {
        return this.key;
    }

    public JSONObject getName() {
        return this.error;
    }
}
