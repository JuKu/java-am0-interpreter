package com.jukusoft.am.interpreter.exception;

/**
 * Created by Justin on 19.07.2016.
 */
public class InterpreterRuntimeException extends Exception {

    public InterpreterRuntimeException (String cmd, String message) {
        super(message);
    }

}
