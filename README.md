# AM0 Interpreter for Java

An interpreter for AM0, written in Java 8.

Version: 0.0.1 (Pre-Alpha)

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