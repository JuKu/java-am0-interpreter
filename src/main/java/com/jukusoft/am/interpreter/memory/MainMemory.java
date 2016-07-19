package com.jukusoft.am.interpreter.memory;

import com.jukusoft.am.interpreter.exception.AMMainMemoryException;

import java.util.List;

/**
 * runtime keller
 *
 * Created by Justin on 19.07.2016.
 */
public interface MainMemory {

    public void push (int index, int i) throws AMMainMemoryException;

    public void push (int i);

    public int get (int index) throws AMMainMemoryException;

    public List<Integer> listValues ();

    public int size ();

}
