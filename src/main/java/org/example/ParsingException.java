package org.example;

public class ParsingException extends RuntimeException {
    private final Token token;

    public ParsingException(String errorMessage, Token token) {
        super(errorMessage);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
