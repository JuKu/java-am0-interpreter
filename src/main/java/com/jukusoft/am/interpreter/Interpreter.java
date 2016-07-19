package com.jukusoft.am.interpreter;

import com.jukusoft.am.interpreter.listener.CommandExecutedListener;

/**
 * Created by Justin on 19.07.2016.
 */
public interface Interpreter {

    public void executeLine (String line, int lineNumber);

    public void executeLine (String line);

    public void reset ();

    public void registerCommandListener (CommandExecutedListener listener);

    public void removeCommandListener (CommandExecutedListener listener);

    public int getCurrentLineNumber ();

    public int getBZ ();

    public int getLastBZ ();

    public void addInputNumber (int value);

    public void setRef (int ref);

    public void print ();

    public void resetInput ();

}
