package thecout.lox;

public class Token {

    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    public final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return "Type: " + type + " Lexeme: \"" + lexeme + "\" Literal: \"" + literal + "\"";
    }

    //Create Getters
    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Object getLiteral() {
        return literal;
    }

    public int getLine() {
        return line;
    }
}