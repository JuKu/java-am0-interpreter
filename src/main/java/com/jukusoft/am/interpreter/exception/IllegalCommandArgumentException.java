package com.jukusoft.am.interpreter.exception;

/**
 * Created by Justin on 19.07.2016.
 */
public class IllegalCommandArgumentException extends Exception {

    protected String cmd = "";

    public IllegalCommandArgumentException (String cmd, String message) {
        super(message);
        this.cmd = cmd;
    }

    public String getCommand () {
        return this.cmd;
    }

}
