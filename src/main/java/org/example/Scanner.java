package org.example;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Scanner {
    private final String sourceCode;
    private int line = 1;
    private int currentPosition = 0;

    private final static HashMap<String, Token.Type> tokenTypes;

    static {
        tokenTypes = initializeTokenTypes();
    }

    public Scanner(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Queue<Token> tokenize() {
        Queue<Token> tokens = new LinkedList<>();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < sourceCode.length(); i++) {
            char c = sourceCode.charAt(i);
            c = Character.toLowerCase(c);

            if (Character.isWhitespace(c)) {
                i = handleWhitespace(tokens, currentToken, c, i);
                continue;
            }

            if (isSymbol(c)) {
                i = handleSymbol(tokens, currentToken, c, i);
                continue;
            }

            currentToken.append(c);
            currentPosition++;
        }

        if (currentToken.length() > 0) {
            addToken(tokens, currentToken);
        }

        tokens.add(new Token(Token.Type.EOF, "", line, currentPosition));
        return tokens;
    }

    // Handles whitespace characters in the source code
    private int handleWhitespace(Queue<Token> tokens, StringBuilder currentToken, char c, int i) {
        if (currentToken.length() > 0) {
            addToken(tokens, currentToken);
            currentToken.setLength(0); // resets the currentToken
        }

        if (c == '\n') {
            line++;
            currentPosition = 0;
        }
        currentPosition++;

        return i;
    }

    // Handles symbol characters in the source code
    private int handleSymbol(Queue<Token> tokens, StringBuilder currentToken, char c, int i) {
        if (currentToken.length() > 0) {
            addToken(tokens, currentToken);
            currentToken.setLength(0);
        }

        // Check if this symbol and the next character form a valid operator
        if (i + 1 < sourceCode.length()) {
            String potentialTwoCharToken = "" + c + sourceCode.charAt(i + 1);
            if (tokenTypes.containsKey(potentialTwoCharToken)) {
                currentToken.append(potentialTwoCharToken);
                i++;
                currentPosition++;
            } else {
                currentToken.append(c);
            }
        } else {
            currentToken.append(c);
        }

        addToken(tokens, currentToken);
        currentToken.setLength(0);

        currentPosition++;
        return i;
    }

    private void addToken(Queue<Token> tokens, StringBuilder currentToken) {
        Token.Type type = identifyTokenType(currentToken.toString());
        tokens.add(new Token(type, currentToken.toString(), line, currentPosition));
    }

    private Token.Type identifyTokenType(String tokenValue) {
        if (tokenValue.matches("^\\d+$")) {
            return Token.Type.INTEGER_VALUE;
        } else if (tokenTypes.containsKey(tokenValue)) {
            return tokenTypes.get(tokenValue);
        } else {
            return Token.Type.NAME;
        }
    }

    private boolean isSymbol(char c) {
        return tokenTypes.containsKey(String.valueOf(c));
    }


    private static HashMap<String, Token.Type> initializeTokenTypes() {
        HashMap<String, Token.Type> types = new HashMap<>();
        types.put("project", Token.Type.PROJECT);
        types.put("const", Token.Type.CONST);
        types.put("var", Token.Type.VAR);
        types.put("int", Token.Type.INT);
        types.put("routine", Token.Type.ROUTINE);
        types.put("start", Token.Type.START);
        types.put("end", Token.Type.END);
        types.put("input", Token.Type.INPUT);
        types.put("output", Token.Type.OUTPUT);
        types.put("if", Token.Type.IF);
        types.put("then", Token.Type.THEN);
        types.put("endif", Token.Type.END_IF);
        types.put("else", Token.Type.ELSE);
        types.put("loop", Token.Type.LOOP);
        types.put("do", Token.Type.DO);

        types.put(":=", Token.Type.ASS);
        types.put(":", Token.Type.COLON);
        types.put(";", Token.Type.SEMICOLON);
        types.put(",", Token.Type.COMMA);
        types.put(".", Token.Type.DOT);

        types.put("(", Token.Type.LEFT_PARENTHESES);
        types.put(")", Token.Type.RIGHT_PARENTHESES);

        types.put("+", Token.Type.PLUS);
        types.put("-", Token.Type.MINUS);
        types.put("*", Token.Type.MULTIPLY);
        types.put("/", Token.Type.DIVIDE);
        types.put("%", Token.Type.REMINDER);

        types.put("=", Token.Type.EQUAL);
        types.put("<>", Token.Type.LESS_GREATER);
        types.put("<", Token.Type.LESS);
        types.put("<=", Token.Type.LESS_EQUAL);
        types.put(">", Token.Type.GREATER);
        types.put(">=", Token.Type.GREATER_EQUAL);
        return types;
    }
}
