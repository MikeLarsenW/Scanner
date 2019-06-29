/*
University Name: Kennesaw State University
College: College of Computing and Software Engineering
Department: Department of Computer Science
Course: CS 4308
Course Title: Concepts of Programming Languages
Section: Section W01
Term: Summer 2019
Instructor: Dr. Jose Garrido
Student Name: Michael Wessels, Woohyung Song, Russ Grant
Student Email: mwessel1@students.kennesaw.edu,  wsong3@students.kennesaw.edu, jgrant64@students.kennesaw.edu
Assignment: Term Project 1st Deliverable
*/
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

        // scan the tokens and store them in a list
        List<Token> tokens = scanner.scanTokens();

        // reiterate the list of tokens to display in the console
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println("Token Type: " + tokens.get(i).type + " Token Lexeme: " + tokens.get(i).lexeme + "\n");
        }
    }


// Scanner class that can be given a string of scl code from a file
static class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    // checks to see if the character is a alphabetical symbol and if so returns true
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    // if the token is either a letter or digit return true
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    // If given a string of words this method will decide what the Identifier is
    private void identifier() {
        // continue until the next character is not a letter
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

    // Method for scanning each token and deciding what TokenType to give the Token
    private void scanToken() {
        char c = advance();
        switch (c) {
            // look at single characters first since they are the easiest case to handle
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
                // The next 3 cases are so that white space is ignored
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
                // if there is a break then increment the line number
            case '\n':
                line++;
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    // if no case is selected then display a helpful message
                    System.out.println(" Unexpected character: "+ c +" ,on line: " + line);
                }
        }
    }

    // retrieves the next char and increments the current index of the scanned string
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    // adds the token if only given a type
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // adds a token if both the type and literal are given
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        if (type == TokenType.EQUAL) {
            text = ":=";
            advance();
        }
        tokens.add(new Token(type, text, literal, line));
    }

    // constructor for the scanner takes in a string of scl code
    Scanner(String source) {
        this.source = source;
    }

    // Helper method to check if the line is the last one
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // Helper method for checking if the character is a digit
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // Helper method to get a long number such as 123
    private void number() {
        // Continue until the next character is not a number
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

    // Look ahead and see what the current character is
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    // Look ahead and see what the next character is
    private char peekNext() {
        // If the next char is the end of the file return  /0 to display that
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    // scans all tokens and returns a list of tokens
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

// Token class to give each scanned character an assigned type, lexeme, literal, and line.
static class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    // constructor for the Token
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

// Enum for the different Token Types
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

