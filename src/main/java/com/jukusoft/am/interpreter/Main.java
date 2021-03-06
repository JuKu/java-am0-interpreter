package com.jukusoft.am.interpreter;

import com.jukusoft.am.interpreter.exception.InterpreterRuntimeException;
import com.jukusoft.am.interpreter.exception.ScriptEndReachedException;
import com.jukusoft.am.interpreter.impl.AMInterpreter;
import com.jukusoft.am.interpreter.listener.PrintLineListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Justin on 19.07.2016.
 */
public class Main {

    public static void main (String[] args) {
        System.out.println("======== AM0 Interpreter ========\n\nYou can type an filename or AM0 commands directly.\nTo set Input, execute: INPUT: <array with numbers>\nfor example: \"INPUT: 1 2 3\".\n\nTo set ref pointer, use \"REF: <number>\".\nInsert \"reset\" to reset all data, \"history\" to list all inserted commands before or \"quit\" to close application.\n");

        //create new instance of AM0 interpreter
        Interpreter amInterpreter = new AMInterpreter();

        PrintLineListener printLineListener = new PrintLineListener(System.out);

        //register PrintLineListener to print data after every command
        amInterpreter.registerCommandListener(printLineListener);

        //create new buffered reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String line = "";

        while (true) {
            //get line number
            int bz = amInterpreter.getLastBZ();

            //print prompt
            System.out.print("\n#" + bz + " > ");

            try {
                //read line
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            if (line.isEmpty()) {
                System.out.print("Empty string isnt allowed here! Please insert an file name or an AM0 command!");
                continue;
            }

            //check, if line is input
            if (line.startsWith("INPUT: ")) {
                line = line.replace("INPUT: ", "");

                String[] inputArray = line.split(" ");

                if (inputArray.length == 0) {
                    System.out.println("Invalid input format, no input numbers set. Usage: \"INPUT: <numbers>\", for example:\nINPUT: 1 2 3");
                    continue;
                }

                //reset input first
                amInterpreter.resetInput();

                for (String inputStr : inputArray) {
                    if (inputStr.equals(" ")) {
                        continue;
                    }

                    try {
                        //convert input string to integer
                        int value = Integer.parseInt(inputStr);

                        //add input to interpreter
                        amInterpreter.addInputNumber(value);
                    } catch (NumberFormatException e) {
                        System.err.println("input \"" + inputStr + "\" isnt an number.");
                    }
                }

                System.out.println("" + inputArray.length + " input numbers set.");

                continue;
            }

            if (line.startsWith("REF: ")) {
                line = line.replace("REF: ", "");

                try {
                    //convert line to integer
                    int ref = Integer.parseInt(line);

                    //set ref
                    amInterpreter.setRef(ref);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Syntax! No number set. Usage: \"REF: <integer>\"");
                }

                continue;
            }

            if (line.toLowerCase().equals("quit") || line.toLowerCase().equals("exit")) {
                System.exit(0);
            }

            if (line.toLowerCase().equals("reset")) {
                //reset interpreter
                amInterpreter.reset();

                //print data
                printLineListener.printHeader();
                amInterpreter.print();

                continue;
            }

            if (line.toLowerCase().equals("history")) {
                //print command history
                amInterpreter.printHistory();

                continue;
            }

            //create new instance of file
            File f = new File(line);

            //check, if line is an file name
            if (f.exists()) {
                //check, if file is readable
                if (f.canRead()) {
                    //interprete file
                    System.out.println("interprete file " + f.getName() + " now.");

                    try {
                        //read all lines in file to list
                        List<String> lines = Files.readAllLines(f.toPath());

                        //print header
                        printLineListener.printHeader();

                        //first, add all code lines to code history
                        int i = 1;

                        for (String line1 : lines) {
                            if (line1.contains("START")) {
                                //remove whitespaces at start and end of command
                                line1 = AMInterpreter.trimEnd(AMInterpreter.trimStart(line1));

                                line1 = line1.replace("START: ", "");
                                line1 = line1.replace("START ", "");
                                line1 = line1.replace("START", "");
                                line1 = line1.replace(";", "");

                                int i1 = Integer.parseInt(line1);

                                //set new start BZ
                                i = i1;
                                amInterpreter.setBZ(i);

                                continue;
                            } else {
                                i = amInterpreter.setCommandLine(i, line1);
                            }
                        }

                        for (String line1 : lines) {
                            //execute line
                            amInterpreter.executeLine(line1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println();

                        continue;
                    } catch (ScriptEndReachedException e) {
                        System.out.println(e.getMessage());
                        continue;
                    } catch (InterpreterRuntimeException e) {
                        e.printStackTrace();
                        continue;
                    }
                } else {
                    System.out.println("Cannot read file " + f.getName() + ", please set correct file permissions!");
                    continue;
                }
            } else {
                //print header
                printLineListener.printHeader();

                try {
                    //execute AM0 command
                    amInterpreter.executeLine(line);
                } catch (NumberFormatException e) {
                    System.out.println("Error! Please insert an number as parameter!");
                } catch (ScriptEndReachedException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("An runtime error oncurred: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
    }

}
