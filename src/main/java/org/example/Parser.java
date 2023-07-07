package org.example;

import java.util.Queue;

public class Parser {

    private final Queue<Token> tokens;
    private Token currentToken;

    public Parser(Queue<Token> tokens) {
        this.tokens = tokens;
        currentToken = tokens.poll();
    }

    public void parse() {
        projectDeclaration();
        consumeExpectedToken(Token.Type.EOF); // The input should end after parsing the start symbol
    }


    private void projectDeclaration() {
        projectDef();
        consumeExpectedToken(Token.Type.DOT);
    }

    private void projectDef() {
        projectHeading();
        declarations();
        compoundStmt();
    }

    private void projectHeading() {
        consumeExpectedToken(Token.Type.PROJECT);
        consumeExpectedToken(Token.Type.NAME);
        consumeExpectedToken(Token.Type.SEMICOLON);
    }

    private void declarations() {
        constDecl();
        varDecl();
        subroutineDecl();
    }

    private void constDecl() {
        if (currentToken.type == Token.Type.CONST) {
            consumeExpectedToken(Token.Type.CONST);

            do {
                constItem();
                consumeExpectedToken(Token.Type.SEMICOLON);
            } while (currentToken.type == Token.Type.NAME);
        }
    }

    private void constItem() {
        consumeExpectedToken(Token.Type.NAME);
        consumeExpectedToken(Token.Type.EQUAL);
        consumeExpectedToken(Token.Type.INTEGER_VALUE);
    }

    private void varDecl() {
        if (currentToken.type == Token.Type.VAR) {
            consumeExpectedToken(Token.Type.VAR);

            do {
                varItem();
                consumeExpectedToken(Token.Type.SEMICOLON);
            } while (currentToken.type == Token.Type.NAME);
        }
    }

    private void varItem() {
        nameList();
        consumeExpectedToken(Token.Type.COLON);
        consumeExpectedToken(Token.Type.INT);
    }

    private void nameList() {
        consumeExpectedToken(Token.Type.NAME);

        while (currentToken.type == Token.Type.COMMA) {
            consumeExpectedToken(Token.Type.COMMA);
            consumeExpectedToken(Token.Type.NAME);
        }
    }

    private void subroutineDecl() {
        if (currentToken.type == Token.Type.ROUTINE) {
            subroutineHeading();
            declarations();
            compoundStmt();
            consumeExpectedToken(Token.Type.SEMICOLON);
        }
    }

    private void subroutineHeading() {
        consumeExpectedToken(Token.Type.ROUTINE);
        consumeExpectedToken(Token.Type.NAME);
        consumeExpectedToken(Token.Type.SEMICOLON);
    }

    private void compoundStmt() {
        consumeExpectedToken(Token.Type.START);
        stmtList();
        consumeExpectedToken(Token.Type.END);
    }

    private void stmtList() {
        while (isValidStatementStart(currentToken.type) ||
                currentToken.type == Token.Type.SEMICOLON) {    // statement is λ
            Token.Type type = currentToken.type;

            if (type == Token.Type.SEMICOLON) {         // statement is λ
                consumeExpectedToken(Token.Type.SEMICOLON);
            } else {
                statement();
            }
        }
    }

    private void statement() {
        Token.Type type = currentToken.type;

        switch (type) {
            case NAME -> assStmt();
            case INPUT, OUTPUT -> inoutStmt();
            case IF -> ifStmt();
            case LOOP -> loopStmt();
            case START -> compoundStmt();
        }
    }

    private boolean isValidStatementStart(Token.Type type) {
        // statement state:
        return type == Token.Type.NAME ||        // ass-stmt
                type == Token.Type.INPUT ||      // inout-stmt
                type == Token.Type.OUTPUT ||     // inout-stmt
                type == Token.Type.IF ||         // if-stmt
                type == Token.Type.LOOP ||       // loop-stmt
                type == Token.Type.START;      // compound-stmt
    }


    private void assStmt() {
        consumeExpectedToken(Token.Type.NAME);
        consumeExpectedToken(Token.Type.ASS);
        arithExp();
    }

    private void arithExp() {
        term();

        while (currentToken.type == Token.Type.PLUS ||
                currentToken.type == Token.Type.MINUS) {
            addSign();
            term();
        }
    }

    private void term() {
        factor();

        while (currentToken.type == Token.Type.MULTIPLY ||
                currentToken.type == Token.Type.DIVIDE ||
                currentToken.type == Token.Type.REMINDER) {
            mulSign();
            factor();
        }
    }

    private void factor() {
        if (currentToken.type == Token.Type.LEFT_PARENTHESES) {
            consumeExpectedToken(Token.Type.LEFT_PARENTHESES);
            arithExp();
            consumeExpectedToken(Token.Type.RIGHT_PARENTHESES);
        } else if (currentToken.type == Token.Type.NAME || currentToken.type == Token.Type.INTEGER_VALUE) {
            nameValue();
        } else {
            error("Expected '(', variable name, or integer value, but found " + currentToken.type);
        }
    }

    private void nameValue() {
        Token.Type type = currentToken.type;

        if (type == Token.Type.NAME || type == Token.Type.INTEGER_VALUE) {
            consumeExpectedToken(type);
        } else {
            error("Expected variable name or integer value, but found " + type);
        }
    }

    private void addSign() {
        Token.Type type = currentToken.type;

        if (type == Token.Type.PLUS || type == Token.Type.MINUS) {
            consumeExpectedToken(type);
        } else {
            error("Expected '+' or '-', but found " + type);
        }
    }

    private void mulSign() {
        Token.Type type = currentToken.type;

        if (type == Token.Type.MULTIPLY || type == Token.Type.DIVIDE || type == Token.Type.REMINDER) {
            consumeExpectedToken(type);
        } else {
            error("Expected '*', '/' or '%', but found " + type);
        }
    }

    private void inoutStmt() {
        if (currentToken.type == Token.Type.INPUT) {
            consumeExpectedToken(Token.Type.INPUT);
            consumeExpectedToken(Token.Type.LEFT_PARENTHESES);
            consumeExpectedToken(Token.Type.NAME);
            consumeExpectedToken(Token.Type.RIGHT_PARENTHESES);
        } else if (currentToken.type == Token.Type.OUTPUT) {
            consumeExpectedToken(Token.Type.OUTPUT);
            consumeExpectedToken(Token.Type.LEFT_PARENTHESES);
            nameValue();
            consumeExpectedToken(Token.Type.RIGHT_PARENTHESES);
        } else {
            error("Expected 'input' or 'output', but found " + currentToken.type);
        }
    }

    private void ifStmt() {
        consumeExpectedToken(Token.Type.IF);
        consumeExpectedToken(Token.Type.LEFT_PARENTHESES);
        boolExp();
        consumeExpectedToken(Token.Type.RIGHT_PARENTHESES);
        consumeExpectedToken(Token.Type.THEN);
        statement();
        elsePart();
        consumeExpectedToken(Token.Type.END_IF);
    }

    private void elsePart() {
        if (currentToken.type == Token.Type.ELSE) {
            consumeExpectedToken(Token.Type.ELSE);
            statement();
        }
    }

    private void loopStmt() {
        consumeExpectedToken(Token.Type.LOOP);
        consumeExpectedToken(Token.Type.LEFT_PARENTHESES);
        boolExp();
        consumeExpectedToken(Token.Type.RIGHT_PARENTHESES);
        consumeExpectedToken(Token.Type.DO);
        statement();
    }

    private void boolExp() {
        nameValue();
        relationalOper();
        nameValue();
    }

    private void relationalOper() {
        if (isValidCompoundStmtStart(currentToken.type)) {
            consumeExpectedToken(currentToken.type);
        } else {
            error("Expected relational operator ('=', '<>', '<', '<=', '>', '>='), but found " + currentToken.type);
        }
    }

    private boolean isValidCompoundStmtStart(Token.Type type) {
        return type == Token.Type.EQUAL ||
                type == Token.Type.LESS_GREATER ||
                type == Token.Type.LESS ||
                type == Token.Type.LESS_EQUAL ||
                type == Token.Type.GREATER ||
                type == Token.Type.GREATER_EQUAL;
    }


    private void consumeExpectedToken(Token.Type expectedType) {
        if (currentToken.type == expectedType) {
            currentToken = tokens.poll(); // consume the token
        } else {
            error("Expected " + expectedType + " but found " + currentToken.type);
        }
    }

    private void error(String errorMessage) {
        throw new ParsingException("Error parsing tokens:\n" + errorMessage, currentToken);
    }
}

