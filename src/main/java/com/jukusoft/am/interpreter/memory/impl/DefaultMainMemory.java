package com.jukusoft.am.interpreter.memory.impl;

import com.jukusoft.am.interpreter.exception.AMMainMemoryException;
import com.jukusoft.am.interpreter.memory.MainMemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Justin on 19.07.2016.
 */
public class DefaultMainMemory implements MainMemory {

    protected List<Integer> memory = new ArrayList<>();

    @Override
    public void push(int index, int i) throws AMMainMemoryException {
        if (this.memory.size() >= index - 1 && this.memory.size() != 0 && index != 1) {
            //specific element first
            try {
                this.memory.remove(index - 1);
            } catch (IndexOutOfBoundsException e) {
                //
            }
        }

        if (index - 1 == this.memory.size()) {
            this.memory.add(i);
            return;
        }

        try {
            this.memory.add(index - 1, i);
        } catch (IndexOutOfBoundsException e) {
            throw new AMMainMemoryException("Cannot add " + i + " to index " + index + ", because memory size is only " + memory.size() + ", this means max. possible index is " + memory.size() + ".");
        }
    }

    @Override
    public void push(int i) {
        this.memory.add(i);
    }

    @Override
    public int get(int index) throws AMMainMemoryException {
        index = index - 1;

        if (this.memory.size() >= index) {
            //get value on index
            int value = this.memory.get(index);

            //remove element from list
            //this.memory.remove(index);

            return value;
        } else {
            throw new AMMainMemoryException("memory only contains " + memory.size() + " elements, so index " + index + " is invalide.");
        }
    }

    @Override
    public List<Integer> listValues() {
        return Collections.unmodifiableList(this.memory);
    }

    @Override
    public int size() {
        return this.memory.size();
    }

    @Override
    public void reset() {
        this.memory.clear();
    }

}
