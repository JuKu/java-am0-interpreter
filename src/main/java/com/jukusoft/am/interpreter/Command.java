package com.jukusoft.am.interpreter;

import com.jukusoft.am.interpreter.exception.InterpreterRuntimeException;
import com.jukusoft.am.interpreter.memory.MainMemory;

import java.util.Deque;
import java.util.List;
import java.util.Queue;

/**
 * Created by Justin on 23.07.2016.
 */
public interface Command {

    public void execute (String cmd, int[] intParams, Integer ref, Integer bz, Deque<Integer> stack, MainMemory memory,Queue<Integer> inputQueue, List<Integer> outputList) throws InterpreterRuntimeException;

}
