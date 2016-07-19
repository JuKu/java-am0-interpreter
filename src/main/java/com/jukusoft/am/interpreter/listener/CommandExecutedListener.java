package com.jukusoft.am.interpreter.listener;

import com.jukusoft.am.interpreter.memory.MainMemory;

import java.util.List;

/**
 * Created by Justin on 19.07.2016.
 */
public interface CommandExecutedListener {

    public void afterExecute (int lineNumber,String command, List<Integer> stack, MainMemory memory, int ref, List<Integer> input, List<Integer> output);
}
