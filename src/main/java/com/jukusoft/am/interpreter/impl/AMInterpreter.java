package com.jukusoft.am.interpreter.impl;

import com.jukusoft.am.interpreter.Interpreter;
import com.jukusoft.am.interpreter.exception.AMMainMemoryException;
import com.jukusoft.am.interpreter.exception.IllegalCommandArgumentException;
import com.jukusoft.am.interpreter.exception.InterpreterRuntimeException;
import com.jukusoft.am.interpreter.exception.UnknownCommandException;
import com.jukusoft.am.interpreter.listener.CommandExecutedListener;
import com.jukusoft.am.interpreter.memory.MainMemory;
import com.jukusoft.am.interpreter.memory.impl.DefaultMainMemory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Justin on 19.07.2016.
 */
public class AMInterpreter implements Interpreter {

    /**
    * stack / data keller
    */
    protected Deque<Integer> stack = new ArrayDeque<>();

    /**
    * main memory
    */
    protected MainMemory memory = new DefaultMainMemory();

    /**
    * list with command executed listeners
    */
    protected List<CommandExecutedListener> listenerList = new ArrayList<>();

    public static final int INPUT_QUEUE_CAPACITY = 50;

    /**
    * list with input numbers
    */
    protected Queue<Integer> inputQueue = new ArrayBlockingQueue<Integer>(INPUT_QUEUE_CAPACITY);

    /**
    * list with output numbers
    */
    protected List<Integer> outputList = new ArrayList<>();

    protected int currentLineNumber = 1;

    protected int bz = 1;
    protected int lastBZ = 1;

    protected int ref = 1;

    protected String lastCommand = "";

    protected Map<String,Integer> commandParams = new HashMap<>();

    public static final int FALSE_INT_VALUE = 0;
    public static final int TRUE_INT_VALUE = 1;

    /**
    * map with command history
     *
     * key - BZ
     * value - command incl. params
    */
    protected Map<Integer,String> commandHistory = new HashMap<>();

    public AMInterpreter () {
        //put number of required params per command to map
        this.commandParams.put("LIT", 1);
        this.commandParams.put("REF", 1);
        this.commandParams.put("LOAD", 1);
        this.commandParams.put("WRITE", 1);
        this.commandParams.put("ADD", 0);
        this.commandParams.put("SUB", 0);
        this.commandParams.put("MULT", 0);
        this.commandParams.put("MUL", 0);
        this.commandParams.put("DIV", 0);
        this.commandParams.put("MOD", 0);
        this.commandParams.put("EQ", 0);
        this.commandParams.put("NE", 0);
        this.commandParams.put("LT", 0);
        this.commandParams.put("GT", 0);
        this.commandParams.put("LE", 0);
        this.commandParams.put("GE", 0);
        this.commandParams.put("JMP", 1);
    }

    public void executeLine (String line, int lineNumber) throws NumberFormatException {
        //split commands by semicoleon
        String[] commands = line.split(";");

        for (String command : commands) {
            //remove whitespaces at end of command
            command = trimEnd(trimStart(command));

            //split command and params
            String[] array = command.split(" ");

            //get command and convert to upper case
            String cmd = array[0].toUpperCase();

            //create new array for params
            String[] params = new String[array.length - 1];

            //param string for debugging
            String paramStr = "";

            //convert array to params array
            for (int i = 0; i < array.length - 1; i++) {
                params[i] = array[i + 1];
                paramStr += array[i + 1] + " ";
            }

            cmd = trimEnd(trimStart(cmd));

            try {
                if (!cmd.isEmpty() && !cmd.equals(" ")) {
                    //execute command
                    this.executeCommand(cmd, params);
                }
            } catch (UnknownCommandException e) {
                if (lineNumber != 0) {
                    System.err.println("Unknown command in line " + lineNumber + ": " + cmd + " with params: " + trimEnd(trimStart(paramStr)));
                } else {
                    System.err.println("Unknown command: " + cmd + " with params: " + trimEnd(trimStart(paramStr)));
                }

                System.out.println();
            } catch (IllegalCommandArgumentException e) {
                if (lineNumber != 0) {
                    System.err.println("command params for command " + e.getCommand() + " in line " + lineNumber + " are invalid: " + e.getMessage());
                } else {
                    System.err.println("command params for command " + e.getCommand() + " are invalid: " + e.getMessage());
                }

                System.out.println();
            } catch (InterpreterRuntimeException e) {
                this.notifyListeners();

                if (lineNumber != 0) {
                    System.err.println("Runtime Error in line " + lineNumber + ": " + e.getMessage());
                } else {
                    System.err.println("Runtime Error: " + e.getMessage());
                }

                System.out.println();
            }
        }

        //increment line number
        this.currentLineNumber++;
    }

    @Override
    public void executeLine (String line) throws NumberFormatException {
        this.executeLine(line, this.currentLineNumber);
    }

    public void executeCommand (String cmd, String... params) throws UnknownCommandException, NumberFormatException, IllegalCommandArgumentException, InterpreterRuntimeException {
        this.lastCommand = cmd + " " + String.join(" ", params);

        //add command to history
        this.commandHistory.put(this.bz, cmd + " " + String.join(" ", params));

        if (this.commandParams.containsKey(cmd)) {
            int requiredParams = this.commandParams.get(cmd);

            //check, if required params are available
            if (params.length < requiredParams) {
                throw new IllegalCommandArgumentException(cmd, "command " + cmd + " requires " + requiredParams + " integer param.");
            }
        }

        int[] intParams = this.toIntArray(params);

        int i = 0;
        int k = 0;
        int a = 0;
        int b = 0;
        int res = 0;

        switch (cmd) {
            case "LIT":
                //push integer in param to stack
                i = Integer.parseInt(params[0]);
                this.stack.push(i);

                break;

            case "REF":
                if (params.length < 1) {
                    throw new IllegalCommandArgumentException("REF", "command REF requires 1 integer param.");
                }

                //convert string to integer
                i = Integer.parseInt(params[0]);

                //set reference index
                this.setRef(i);

                break;

            case "LOAD":
                try {
                    this.stack.push(memory.get(intParams[0]));
                } catch (AMMainMemoryException e) {
                    throw new InterpreterRuntimeException(cmd, "LK has only " + this.memory.size() + " elements, so index " + intParams[0] + " is invalid.");
                }

                break;

            case "STORE":
                if (stack.size() < 1) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute STORE, because stack has no element anymore.");
                }

                //get element from stack
                i = stack.poll();

                //add value to main memory
                try {
                    this.memory.push(intParams[0], i);
                } catch (AMMainMemoryException e) {
                    throw new InterpreterRuntimeException(cmd, e.getMessage());
                }

                break;

            case "ADD":
                if (stack.size() < 2) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute ADD, because 2 elements on stack required.");
                }

                i = stack.poll();
                k = stack.poll();

                //add
                res = i + k;

                //push result to stack
                this.stack.push(res);

                break;

            case "SUB":
                if (stack.size() < 2) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute SUB, because 2 elements on stack required.");
                }

                i = stack.poll();
                k = stack.poll();

                //add
                res = i - k;

                //push result to stack
                this.stack.push(res);

                break;

            case "MULT":
            case "MUL":
                if (stack.size() < 2) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute MULT, because 2 elements on stack required.");
                }

                i = stack.poll();
                k = stack.poll();

                //add
                res = i * k;

                //push result to stack
                this.stack.push(res);

                break;

            case "DIV":
                if (stack.size() < 2) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute DIV, because 2 elements on stack required.");
                }

                i = stack.poll();
                k = stack.poll();

                //add
                res = i / k;

                //push result to stack
                this.stack.push(res);

                break;

            case "MOD":
                if (stack.size() < 2) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute MOD, because 2 elements on stack required.");
                }

                i = stack.poll();
                k = stack.poll();

                //add
                res = i % k;

                //push result to stack
                this.stack.push(res);

                break;

            case "EQ":
                if (stack.size() < 2) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute EQ, because 2 elements on stack required.");
                }

                i = stack.poll();
                k = stack.poll();

                if (i == k) {
                    //push result to stack
                    this.stack.push(TRUE_INT_VALUE);
                } else {
                    //push result to stack
                    this.stack.push(FALSE_INT_VALUE);
                }

                break;

            case "NE":
                if (stack.size() < 2) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute NE, because 2 elements on stack required.");
                }

                i = stack.poll();
                k = stack.poll();

                if (i == k) {
                    //push result to stack
                    this.stack.push(FALSE_INT_VALUE);
                } else {
                    //push result to stack
                    this.stack.push(TRUE_INT_VALUE);
                }

                break;

            case "LT":
                if (stack.size() < 2) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute LT, because 2 elements on stack required.");
                }

                a = stack.poll();
                b = stack.poll();

                if (a < b) {
                    //push result to stack
                    this.stack.push(TRUE_INT_VALUE);
                } else {
                    //push result to stack
                    this.stack.push(FALSE_INT_VALUE);
                }

                break;

            case "GT":
                if (stack.size() < 2) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute GT, because 2 elements on stack required.");
                }

                a = stack.poll();
                b = stack.poll();

                if (a > b) {
                    //push result to stack
                    this.stack.push(TRUE_INT_VALUE);
                } else {
                    //push result to stack
                    this.stack.push(FALSE_INT_VALUE);
                }

                break;

            case "LE":
                if (stack.size() < 2) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute LE, because 2 elements on stack required.");
                }

                a = stack.poll();
                b = stack.poll();

                if (a <= b) {
                    //push result to stack
                    this.stack.push(TRUE_INT_VALUE);
                } else {
                    //push result to stack
                    this.stack.push(FALSE_INT_VALUE);
                }

                break;

            case "GE":
                if (stack.size() < 2) {
                    throw new InterpreterRuntimeException(cmd, "Cannot execute GE, because 2 elements on stack required.");
                }

                a = stack.poll();
                b = stack.poll();

                if (a >= b) {
                    //push result to stack
                    this.stack.push(TRUE_INT_VALUE);
                } else {
                    //push result to stack
                    this.stack.push(FALSE_INT_VALUE);
                }

                break;

            case "JMP":
                //check, if bz exists
                if (!this.commandHistory.containsKey(intParams[0])) {
                    this.jmp(intParams[0]);

                    //notify listeners
                    this.notifyListeners();

                    //incremnt command counter
                    this.bz++;

                    if (this.bz > this.lastBZ) {
                        this.lastBZ = this.bz;
                    }

                    return;
                } else {
                    throw new InterpreterRuntimeException(cmd, "Cannot jump to " + intParams[0] + ", this command line wasnt inserted yet.");
                }

            default:
                throw new UnknownCommandException("Command " + cmd + " isnt supported yet.");
        }

        //notify listeners
        this.notifyListeners();

        //incremnt command counter
        this.bz++;

        if (this.bz > this.lastBZ) {
            this.lastBZ = this.bz;
        }
    }

    public void reset() {
        //clear stack
        this.stack.clear();

        //clear runtime keller
        this.memory.reset();

        this.currentLineNumber = 1;
        this.bz = 1;
        this.ref = 1;

        //reset last command
        this.lastCommand = "";

        this.inputQueue.clear();
        this.outputList.clear();

        //clear input queue
        this.resetInput();
    }

    @Override
    public void registerCommandListener (CommandExecutedListener listener) {
        this.listenerList.add(listener);
    }

    @Override
    public void removeCommandListener (CommandExecutedListener listener) {
        this.listenerList.remove(listener);
    }

    @Override
    public int getCurrentLineNumber () {
        return this.currentLineNumber;
    }

    @Override
    public int getBZ() {
        return this.bz;
    }

    @Override
    public int getLastBZ() {
        return this.lastBZ;
    }

    @Override
    public void addInputNumber (int value) {
        //add input number to input queue
        this.inputQueue.add(value);
    }

    @Override
    public void setRef(int ref) {
        this.ref = ref;
    }

    @Override
    public void print() {
        this.notifyListeners();
    }

    @Override
    public void printHistory() {
        for (Map.Entry<Integer,String> entry : this.commandHistory.entrySet()) {
            int bz = entry.getKey();
            String cmdStr = entry.getValue();

            System.out.println("#" + bz + " : " + cmdStr);
        }
    }

    @Override
    public void resetInput() {
        //clear input queue
        this.inputQueue.clear();
    }

    protected void notifyListeners () {
        //notfiy executed command listeners

        List<Integer> dataKeller = new ArrayList<>();
        List<Integer> inputList = new ArrayList<>();

        for (int i : this.stack) {
            dataKeller.add(i);
        }

        for (int i : this.inputQueue) {
            inputList.add(i);
        }

        if (!this.lastCommand.contains(";")) {
            this.lastCommand = trimEnd(trimStart(this.lastCommand));
            this.lastCommand += ";";
        }

        for (CommandExecutedListener listener : this.listenerList) {
            listener.afterExecute(this.bz, this.lastCommand, dataKeller, this.memory, this.ref, inputList, Collections.unmodifiableList(this.outputList));
        }
    }

    protected int[] toIntArray (String[] array) throws NumberFormatException {
        int[] intArray = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            intArray[0] = Integer.parseInt(array[i]);
        }

        return intArray;
    }

    protected void jmp (int newBZ) {
        this.bz = newBZ;

        while (this.commandHistory.containsKey(newBZ)) {
            this.bz++;

            //get command
            String cmdStr = this.commandHistory.get(this.bz - 1);

            //execute command
            this.executeLine(cmdStr);
        }
    }

    public static String trimEnd (String value) {
        // Use replaceFirst to remove trailing spaces.
        return value.replaceFirst("\\s+$", "");
    }

    public static String trimStart (String value) {
        // Remove leading spaces.
        return value.replaceFirst("^\\s+", "");
    }

}
