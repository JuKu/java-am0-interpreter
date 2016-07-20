package com.jukusoft.am.interpreter;

import com.jukusoft.am.interpreter.exception.InterpreterRuntimeException;
import com.jukusoft.am.interpreter.exception.ScriptEndReachedException;
import com.jukusoft.am.interpreter.listener.CommandExecutedListener;

/**
 * Created by Justin on 19.07.2016.
 */
public interface Interpreter {

    public void executeLine (String line, int lineNumber) throws ScriptEndReachedException;

    public void executeLine (String line) throws ScriptEndReachedException;

    public void reset ();

    public void registerCommandListener (CommandExecutedListener listener);

    public void removeCommandListener (CommandExecutedListener listener);

    public int getCurrentLineNumber ();

    public int getBZ ();

    public void setBZ (int bz) throws InterpreterRuntimeException;

    public int getLastBZ ();

    public void addInputNumber (int value);

    public int setCommandLine (int i, String cmdStr);

    public void setRef (int ref);

    public void print ();

    public void resetHistory ();

    public void printHistory ();

    public void resetInput ();

}
