// Java Program to illustrate reading from FileReader 
// using BufferedReader 
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class SclScanner
{
    public static void main(String[] args) throws Exception {

        // We need to provide file path as the parameter:
        // double backquote is to avoid compiler interpret words
        // like \test as \t (ie. as a escape sequence)
        File file = new File("C:\\Users\\Mike\\Documents\\SclExample.txt");
        Scanner scanner = new Scanner(Files.readString(file.toPath(), StandardCharsets.US_ASCII));

        List<Token> tokens = scanner.scanTokens();
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println("Token Type: " + tokens.get(i).type + " Token Lexeme: " + tokens.get(i).lexeme + "\n");
        }
    }



static class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String s = source.substring(start, current);

        switch (s) {
            case "IF": addToken(TokenType.IF); break;
            case "WHILE": addToken(TokenType.WHILE); break;
            case "ELSE": addToken(TokenType.ELSE); break;
            case "END_WHILE": addToken(TokenType.END_WHILE); break;
            case "true": addToken(TokenType.TRUE); break;
            case "false": addToken(TokenType.FALSE); break;
            case "NOT": addToken(TokenType.NOT); break;
            case "THEN": addToken(TokenType.THEN); break;
            case "ENDIF": addToken(TokenType.ENDIF); break;
            default: addToken(TokenType.IDENTIFIER); break;
        }
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            case '>': addToken(TokenType.GREATER); break;
            case '<': addToken(TokenType.LESS); break;
            case ':':
                if (peek() == '=') {
                    addToken(TokenType.EQUAL);
                } else {
                    System.out.println(" Unexpected character: "+ c +" ,on line: " + line + " should be followed by '=' for assignment");
                }
                break;
            case '=':
                System.out.println(" Unexpected character: "+ c +" ,on line: " + line + " should be preceded by ':' for assignment");
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    System.out.println(" Unexpected character: "+ c +" ,on line: " + line);
                }
        }
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        if (type == TokenType.EQUAL) {
            text = ":=";
            advance();
        }
        tokens.add(new Token(type, text, literal, line));
    }

    Scanner(String source) {
        this.source = source;
    }
    private boolean isAtEnd() {
        return current >= source.length();
    }
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    private List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }
}

static class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}

enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens.
    NOT, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR, THEN,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE, END_WHILE, ENDIF,

    EOF
}
}

