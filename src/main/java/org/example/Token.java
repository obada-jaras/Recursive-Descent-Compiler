package org.example;

public class Token {
    public enum Type {
        // reserved words
        PROJECT, CONST, VAR, INT, ROUTINE, START, END, INPUT, OUTPUT, IF, THEN, END_IF, ELSE, LOOP, DO,

        // identifiers
        NAME, INTEGER_VALUE,

        // Operators
        ASS, COLON, SEMICOLON, COMMA, DOT,
        LEFT_PARENTHESES, RIGHT_PARENTHESES, PLUS, MINUS, MULTIPLY, DIVIDE, REMINDER,
        EQUAL, LESS_GREATER, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,

        // End of file
        EOF
    }

    final Type type;
    final String value;
    final int lineNumber;

    final int position;

    public Token(Type type, String value, int lineNumber, int position) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
        this.position = position;
    }
}
