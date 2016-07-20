# AM0 Interpreter for Java

An (also Just-In-Time) interpreter for AM0, written in Java 8.
You can insert your commands in interactive console mode or write an file with your AM0 commands and execute them.
Its also possible to use only the core functionality to integrate this interpreter in your own application.

Version: 0.0.2 (Pre-Alpha)

Download:
https://github.com/JuKu/java-am0-interpreter/releases

## Execution
Java 8 is required. You can download only the .jar file and execute them in console with "java -jar <filename>.jar".

## Supported AM0 commands:
 - LIT <Integer>
 - ADD
 - SUB
 - MULT / MUL
 - DIV
 - MOD
 - EQ (equals)
 - NE (not equals)
 - LT (less than)
 - GT (greater than)
 - LE (less or equal)
 - GT (greater or equal)

experimental:
 - LOAD <Integer>
 - STORE <Integer>
 - JMP <BZ> (jump to command number BZ)
 - JMC <BZ> (jump on condition, if 0 is on top of stack)
 - WRITE <Index in main memory LK>
 - READ <Index in main memory LK>

## Supported AM1 coommands:
 - PUSH