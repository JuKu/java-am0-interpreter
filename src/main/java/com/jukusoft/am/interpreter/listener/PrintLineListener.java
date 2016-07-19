package com.jukusoft.am.interpreter.listener;

import com.jukusoft.am.interpreter.memory.MainMemory;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 19.07.2016.
 */
public class PrintLineListener implements CommandExecutedListener {

    /**
    * instance of print writer to print line
    */
    protected PrintWriter writer = null;

    public PrintLineListener (PrintStream stream) {
        this.writer = new PrintWriter(stream);
    }

    public PrintLineListener (PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void afterExecute(int lineNumber, String command, List<Integer> stack, MainMemory memory, int ref, List<Integer> input, List<Integer> output) {
        String text = "| " + fillString(lineNumber + "", 5) + " | ";

        //convert integer lists to string lists
        List<String> stackStrList = this.toStringList(stack);
        List<String> memoryStrList = this.toStringList(memory.listValues());

        String dkStr = String.join(":", stackStrList);
        String lkStr = String.join(":", memoryStrList);
        String inputStr = String.join(":", this.toStringList(input));
        String outputStr = String.join(":", this.toStringList(output));

        if (dkStr.equals("") || dkStr.isEmpty()) {
            dkStr = "E";
        }

        if (lkStr.equals("") || lkStr.isEmpty()) {
            lkStr = "E";
        }

        if (inputStr.equals("") || inputStr.isEmpty()) {
            inputStr = "E";
        }

        if (outputStr.equals("") || outputStr.isEmpty()) {
            outputStr = "E";
        }

        text += fillString(dkStr, 10) + " | ";
        text += fillString(lkStr, 10) + " | ";
        text += fillString(ref + "", 5) + " | ";
        text += fillString(inputStr, 10) + " | ";
        text += fillString(outputStr, 10) + " | ";
        text += fillString(command, 10) + " |";

        //print line
        writer.println(text);

        writer.flush();
    }

    public String fillString (String str, int targetNumber) {
        while (str.toCharArray().length < targetNumber) {
            str += " ";
        }

        return str;
    }

    public void printHeader () {
        this.writer.println("| " + fillString("BZ", 5) + " | " + fillString("DK", 10) + " | " + fillString("LK", 10) + " | " + fillString("REF", 5) + " | " + fillString("Input", 10) + " | " + fillString("Output", 10) + " | " + fillString("Command", 10) + " |");

        this.writer.flush();
    }

    protected List<String> toStringList (List<Integer> list) {
        //convert integer list to string list
        List<String> strList = new ArrayList<>();

        for (Integer i : list) {
            strList.add(i + "");
        }

        return strList;
    }

}
