// Author: David Adams
// Class: CSCI 4200, Fall 2023
// Program: Parser
// Did you use your own lexical analyzer? Yes. I also fully rewrote the parser itself.
//
// As we discussed, the output is not exactly the same formatting as the original parser. This
// corrects an issue with the original parser and I believe improves readability.


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


public class Parse {
    // Default path for input text file
    private static final String DEFAULT_INPUT_PATH = "sourceProgram.txt";
    // Default path for output text file
    private static final String DEFAULT_OUTPUT_PATH = "parseOut.txt";
    // Delimiters constant string
    private static final String DELIMITERS = " \t+-*/=;()";
    // Path for input text file
    private static String inputPath;
    // Path for output text file
    private static String outputPath;


    // Command line arguments:
    // args[0]=inputPath- String representing path to text file to be read as input
    // args[1]=outputPath- String representing path to text file for output to be printed to
    public static void main(String[] args) {
        switch (args.length) {
            // No command line arguments passed => use all defaults
            case 0:
                inputPath = DEFAULT_INPUT_PATH;
                outputPath = DEFAULT_OUTPUT_PATH;
                break;
            // Command line argument provided for inputPath
            case 1:
                inputPath = args[0];
                outputPath = DEFAULT_OUTPUT_PATH;
                break;
            // Command line argument provided for inputPath and outputPath
            case 2:
                inputPath = args[0];
                outputPath = args[1];
                break;
            default:
                inputPath = DEFAULT_INPUT_PATH;
                outputPath = DEFAULT_OUTPUT_PATH;
                break;
        }

        // try-with PrintWriter for printing to output file
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(outputPath), false))) {
            // Print header to terminal and output file
            writer.println("David Adams, CSCI4200, Fall 2023, Parser");
            System.out.println("David Adams, CSCI4200, Fall 2023, Parser");
            writer.println("*".repeat(80));
            System.out.println("*".repeat(80));

            // Initialize LinkedList to store tokens from lexical analysis
            LinkedList<Token> tokenList = new LinkedList<Token>();

            // Read input text file line by line
            try (Scanner scanner = new Scanner(new File(inputPath))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    // Print source program to terminal and output file
                    writer.println(line);
                    System.out.println(line);

                    // Perform lexical analysis on current line and add results to tokenList
                    tokenList.addAll(lex(line));
                }
            } catch (FileNotFoundException e) {
                System.out.println(String.format("\nNo file was found at the specified path: %s\n\nExiting.", inputPath));
                System.exit(1);
            } catch (Exception e) {
                throw e;
            }

            writer.println("*".repeat(20));
            System.out.println("*".repeat(20));

            // Begin parsing of tokens from lexical analysis
            parseHandler(writer, tokenList);

            writer.println("*".repeat(80));
            System.out.println("*".repeat(80));

        } catch (FileNotFoundException e) {
            System.out.println(String.format("\nA problem occurred while attempting to create output file at path: %s\n\nExiting.", outputPath));
            System.exit(1);
        } catch (Exception e) {
            throw(e);
        }
    }


    // enum for different token types
    private static enum TokenType {
        ADD_OP,
        ASSIGN_OP,
        DIV_OP,
        ELSE_KEYWORD,
        END_KEYWORD,
        END_OF_FILE,
        IDENT,
        IF_KEYWORD,
        INT_LIT,
        LEFT_PAREN,
        MULT_OP,
        PRINT_KEYWORD,
        PROGRAM_KEYWORD,
        READ_KEYWORD,
        RIGHT_PAREN,
        SEMICOLON,
        SUB_OP,
        THEN_KEYWORD,
        UNKNOWN,
        NULL
    }


    // Class representing a token
    // A token is composed of 2 elements: a String (lexeme) and a TokenType (type)
    private static class Token {
        private String lexeme;
        private TokenType type;

        // Constructor
        public Token (String lexeme, TokenType type) {
            this.lexeme = lexeme;
            this.type = type;
        }

        // Returns a String representation of the data contained in a Token object
        // Useful for printing
        public String toString() {
            return String.format("{\"%s\", %s}", this.lexeme, this.type);
        }
    }


    // My implementation of a lexical analyzer from previous assignment modified into a class method
    // and to go line by line instead of taking the entire input text file at once
    // Takes as input a String line representing a single line from the input source program
    // Returns a LinkedList of Token elements representing the resulting tokens (in order)
    private static LinkedList<Token> lex(String line) {
        // List representing all Token elements to be returned
        LinkedList<Token> tokenList = new LinkedList<Token>();

        // Tokenize the current line using DELIMITERS
        StringTokenizer tokenizer = new StringTokenizer(line, DELIMITERS, true);

        // Loop through all tokens
        while (tokenizer.hasMoreTokens()) {
            String currentLexeme = tokenizer.nextToken();

            // Evaluate type of current token
            TokenType currentTokenType;
            // Switch on upper case of currentLexeme as keywords can be any case
            switch (currentLexeme.toUpperCase()) {
                // " " is not treated as an actual token (type of NULL)
                case " ":
                    currentTokenType = TokenType.NULL;
                    break;
                // "\t" (TAB) is not treated as an actual token (type of NULL)
                case "\t":
                    currentTokenType = TokenType.NULL;
                    break;
                case "+":
                    currentTokenType = TokenType.ADD_OP;
                    break;
                case "-":
                    currentTokenType = TokenType.SUB_OP;
                    break;
                case "*":
                    currentTokenType = TokenType.MULT_OP;
                    break;
                case "/":
                    currentTokenType = TokenType.DIV_OP;
                    break;
                case "=":
                    currentTokenType = TokenType.ASSIGN_OP;
                    break;
                case ";":
                    currentTokenType = TokenType.SEMICOLON;
                    break;
                case "(":
                    currentTokenType = TokenType.LEFT_PAREN;
                    break;
                case ")":
                    currentTokenType = TokenType.RIGHT_PAREN;
                    break;
                case "ELSE":
                    currentTokenType = TokenType.ELSE_KEYWORD;
                    break;
                case "END":
                    currentTokenType = TokenType.END_KEYWORD;
                    break;
                case "EOF":
                    currentTokenType = TokenType.END_OF_FILE;
                    break;
                case "IF":
                    currentTokenType = TokenType.IF_KEYWORD;
                    break;
                case "PRINT":
                    currentTokenType = TokenType.PRINT_KEYWORD;
                    break;
                case "PROGRAM":
                    currentTokenType = TokenType.PROGRAM_KEYWORD;
                    break;
                case "READ":
                    currentTokenType = TokenType.READ_KEYWORD;
                    break;
                case "THEN":
                    currentTokenType = TokenType.THEN_KEYWORD;
                    break;
                // Not an operator or keyword
                default:
                    // Identifier
                    if (Pattern.matches("[a-zA-Z][a-zA-Z\\d]*", currentLexeme)) {
                        currentTokenType = TokenType.IDENT;
                    // Integer literal
                    } else if (Pattern.matches("[\\d]+", currentLexeme)) {
                        currentTokenType = TokenType.INT_LIT;
                    // Unknown token
                    } else {
                        currentTokenType = TokenType.UNKNOWN;
                    }
                    break;
            }

            // Check currentTokenType is not NULL (" " or "\t")
            if (currentTokenType != TokenType.NULL) {
                // Add new Token into list
                tokenList.add(new Token(currentLexeme, currentTokenType));
            }
        }

        return tokenList;
    }


    // Handler method to begin parsing of resulting list of tokens from lexical analysis (lex)
    // Takes a LinkedList of Token elements representing the tokens from lex (ordered) as input
    // PrintWriter input is just to allow directly printing to output file from this and subsequent
    // methods (Java is a stupid language)
    // See Assign-Parser-DESCR-Fa23.pdf for complete description of grammar and parser
    private static void parseHandler(PrintWriter writer, LinkedList<Token> tokenList) {
        // Check that tokenList is not empty
        if (!tokenList.isEmpty()) {
            // Get next token from list (peek returns the token without removing it)
            Token nextToken = tokenList.peek();
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));

            // Check first token is PROGRAM_KEYWORD, printing an error if not
            if (nextToken.type == TokenType.PROGRAM_KEYWORD) {
                tokenList.remove();
            } else {
                writer.println("**Error** - missing PROGRAM keyword");
                System.out.println("**Error** - missing PROGRAM keyword");
            }

            // Continue parsing regardless of error and enter program
            writer.println("Enter <program>");
            System.out.println("Enter <program>");
            program(writer, tokenList);
        }

        // Parsing done
        writer.println("Parsing of the program is complete!");
        System.out.println("Parsing of the program is complete!");
    }


    // program method (see Assign-Parser-DESCR-Fa23.pdf for complete description of grammar)
    private static void program(PrintWriter writer, LinkedList<Token> tokenList) {
        // Used to determine is END keyword is present or missing at last line of program
        boolean endIsPresent = false;

        // Continue with parsing while tokens remain in list
        while (!tokenList.isEmpty()) {
            // Get next token from list
            Token nextToken = tokenList.peek();
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));

            switch (nextToken.type) {
                // END keyword
                case TokenType.END_KEYWORD:
                    tokenList.remove();
                    endIsPresent = true;
                    end(writer, tokenList);
                    break;

                // Statement cases
                case TokenType.PRINT_KEYWORD, TokenType.IDENT, TokenType.READ_KEYWORD, TokenType.IF_KEYWORD:
                    // Enter statement
                    writer.println("Enter <statement>");
                    System.out.println("Enter <statement>");
                    statement(writer, tokenList);

                    // Complicated handling for missing semicolons (gross but works)
                    if (!tokenList.isEmpty()) {
                        // Get next token from list
                        nextToken = tokenList.peek();
                        switch (nextToken.type) {
                            // Semicolon present
                            case TokenType.SEMICOLON:
                                tokenList.remove();
                                writer.println(String.format("Next token is: %s", nextToken.toString()));
                                System.out.println(String.format("Next token is: %s", nextToken.toString()));
                                break;
                            // END keyword
                            case TokenType.END_KEYWORD:
                                break;
                            // Missing semicolon
                            default:
                                writer.println("**Error** - missing semicolon");
                                System.out.println("**Error** - missing semicolon");
                                break;
                        }
                    }
                    // Exit statement
                    writer.println("Exit <statement>");
                    System.out.println("Exit <statement>");
                    break;

                // Handle unexpected tokens
                default:
                    tokenList.remove();
                    writer.println(String.format("**Error** - unexpected token: %s", nextToken.type));
                    System.out.println(String.format("**Error** - unexpected token: %s", nextToken.type));
                    break;
            }
        }

        // END keyword missing
        if (!endIsPresent) {
            writer.println("**Error** - missing END keyword");
            System.out.println("**Error** - missing END keyword");
        }
    }


    // end method called when an END keyword is seen, may not necessarily be the actual end of the
    // program
    private static void end(PrintWriter writer, LinkedList<Token> tokenList) {
        if (!tokenList.isEmpty()) {
            writer.println("**Error** - unexpected END keyword");
            System.out.println("**Error** - unexpected END keyword");
            program(writer, tokenList);
        } else {
            // Exit program
            writer.println("Exit <program>");
            System.out.println("Exit <program>");
        }
    }


    // statement method (see Assign-Parser-DESCR-Fa23.pdf for complete description of grammar)
    private static void statement(PrintWriter writer, LinkedList<Token> tokenList) {
        // Get next token from list
        Token nextToken = tokenList.remove();

        switch (nextToken.type) {
            case TokenType.PRINT_KEYWORD:
                // Enter output
                writer.println("Enter <output>");
                System.out.println("Enter <output>");
                output(writer, tokenList);
                // Exit output
                writer.println("Exit <output>");
                System.out.println("Exit <output>");
                break;
            case TokenType.IDENT:
                // Enter assign
                writer.println("Enter <assign>");
                System.out.println("Enter <assign>");
                assign(writer, tokenList);
                // Exit assign
                writer.println("Exit <assign>");
                System.out.println("Exit <assign>");
                break;
            case TokenType.READ_KEYWORD:
                // Enter input
                writer.println("Enter <input>");
                System.out.println("Enter <input>");
                input(writer, tokenList);
                // Exit input
                writer.println("Exit <input>");
                System.out.println("Exit <input>");
                break;
            case TokenType.IF_KEYWORD:
                // Enter selection
                writer.println("Enter <selection>");
                System.out.println("Enter <selection>");
                selection(writer, tokenList);
                // Exit selection
                writer.println("Exit <selection>");
                System.out.println("Exit <selection>");
                break;
            // Handle unexpected tokens
            default:
                tokenList.remove();
                writer.println(String.format("**Error** - unexpected token: %s", nextToken.type));
                System.out.println(String.format("**Error** - unexpected token: %s", nextToken.type));
                break;
        }
    }


    // output method (see Assign-Parser-DESCR-Fa23.pdf for complete description of grammar)
    private static void output(PrintWriter writer, LinkedList<Token> tokenList) {
        // Get next token from list, should be LEFT_PAREN
        Token nextToken = tokenList.peek();
        // Only print "next token" message if token present
        if (!tokenList.isEmpty()) {
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));
        }

        // Check next token is LEFT_PAREN
        if (nextToken.type == TokenType.LEFT_PAREN) {
            tokenList.remove();
        } else {
            writer.println("**Error** - missing left parenthesis");
            System.out.println("**Error** - missing left parenthesis");
        }

        // If a next token is present, enter expr regardless of error
        if (!tokenList.isEmpty()) {
            nextToken = tokenList.peek();
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));
            // Enter expr
            writer.println("Enter <expr>");
            System.out.println("Enter <expr>");
            expr(writer, tokenList);
            // Exit expr
            writer.println("Exit <expr>");
            System.out.println("Exit <expr>");
        }

        // Get next token from list, should be RIGHT_PAREN
        nextToken = tokenList.peek();
        // Only print "next token" message if token present
        if (!tokenList.isEmpty()) {
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));
        }

        // Check next token is RIGHT_PAREN
        if (nextToken.type == TokenType.RIGHT_PAREN) {
            tokenList.remove();
        } else {
            writer.println("**Error** - missing right parenthesis");
            System.out.println("**Error** - missing right parenthesis");
        }
    }


    // assign method (see Assign-Parser-DESCR-Fa23.pdf for complete description of grammar)
    private static void assign(PrintWriter writer, LinkedList<Token> tokenList) {
        // Get next token from list, should be ASSIGN_OP
        Token nextToken = tokenList.peek();
        // Only print "next token" message if token present
        if (!tokenList.isEmpty()) {
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));
        }

        // Check next token is ASSIGN_OP
        if (nextToken.type == TokenType.ASSIGN_OP) {
            tokenList.remove();

            // If a next token is present, enter expr
            if (!tokenList.isEmpty()) {
                nextToken = tokenList.peek();
                writer.println(String.format("Next token is: %s", nextToken.toString()));
                System.out.println(String.format("Next token is: %s", nextToken.toString()));
                // Enter expr
                writer.println("Enter <expr>");
                System.out.println("Enter <expr>");
                expr(writer, tokenList);
                // Exit expr
                writer.println("Exit <expr>");
                System.out.println("Exit <expr>");
            }

        // Do not enter expr on missing ASSIGN_OP
        } else {
            writer.println("**Error** - expected ASSIGN_OP");
            System.out.println("**Error** - expected ASSIGN_OP");
        }
    }


    // input method (see Assign-Parser-DESCR-Fa23.pdf for complete description of grammar)
    private static void input(PrintWriter writer, LinkedList<Token> tokenList) {
        // Get next token from list, should be LEFT_PAREN
        Token nextToken = tokenList.peek();
        // Only print "next token" message if token present
        if (!tokenList.isEmpty()) {
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));
        }

        // Check next token is LEFT_PAREN
        if (nextToken.type == TokenType.LEFT_PAREN) {
            tokenList.remove();
        } else {
            writer.println("**Error** - missing left parenthesis");
            System.out.println("**Error** - missing left parenthesis");
        }

        // Get next token from list, should be IDENT
        nextToken = tokenList.peek();
        // Only print "next token" message if token present
        if (!tokenList.isEmpty()) {
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));
        }

        // Check next token is IDENT
        if (nextToken.type == TokenType.IDENT) {
            tokenList.remove();

            // Get next token from list, should be RIGHT_PAREN
            nextToken = tokenList.peek();
            // Only print "next token" message if token present
            if (!tokenList.isEmpty()) {
                writer.println(String.format("Next token is: %s", nextToken.toString()));
                System.out.println(String.format("Next token is: %s", nextToken.toString()));
            }

            // Check next token is RIGHT_PAREN
            if (nextToken.type == TokenType.RIGHT_PAREN) {
                tokenList.remove();
            } else {
                writer.println("**Error** - missing right parenthesis");
                System.out.println("**Error** - missing right parenthesis");
            }

        // Next token is not IDENT
        } else {
            writer.println("**Error** - expected IDENT");
            System.out.println("**Error** - expected IDENT");
        }
    }


    // selection method (see Assign-Parser-DESCR-Fa23.pdf for complete description of grammar)
    private static void selection(PrintWriter writer, LinkedList<Token> tokenList) {
         // Get next token from list, should be LEFT_PAREN
        Token nextToken = tokenList.peek();
        // Only print "next token" message if token present
        if (!tokenList.isEmpty()) {
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));
        }

        // Check next token is LEFT_PAREN
        if (nextToken.type == TokenType.LEFT_PAREN) {
            tokenList.remove();
        } else {
            writer.println("**Error** - missing left parenthesis");
            System.out.println("**Error** - missing left parenthesis");
        }

        // If a next token is present, enter expr regardless of error
        if (!tokenList.isEmpty()) {
            nextToken = tokenList.peek();
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));
            // Enter expr
            writer.println("Enter <expr>");
            System.out.println("Enter <expr>");
            expr(writer, tokenList);
            // Exit expr
            writer.println("Exit <expr>");
            System.out.println("Exit <expr>");
        }

        // Get next token from list, should be RIGHT_PAREN
        nextToken = tokenList.peek();
        // Only print "next token" message if token present
        if (!tokenList.isEmpty()) {
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));
        }

        // Check next token is RIGHT_PAREN
        if (nextToken.type == TokenType.RIGHT_PAREN) {
            tokenList.remove();
        } else {
            writer.println("**Error** - missing right parenthesis");
            System.out.println("**Error** - missing right parenthesis");
        }

        // Get next token from list, should be THEN_KEYWORD
        nextToken = tokenList.peek();
        // Only print "next token" message if token present
        if (!tokenList.isEmpty()) {
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));
        }

        // Check next token is THEN_KEYWORD
        if (nextToken.type == TokenType.THEN_KEYWORD) {
            tokenList.remove();
        } else {
            writer.println("**Error** - missing THEN_KEYWORD");
            System.out.println("**Error** - missing THEN_KEYWORD");
        }

        // Should be <statement>
        if (!tokenList.isEmpty()) {
            // Get next token from list
            nextToken = tokenList.peek();
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));

            // Enter statement
            writer.println("Enter <statement>");
            System.out.println("Enter <statement>");
            statement(writer, tokenList);
            // Exit statement
            writer.println("Exit <statement>");
            System.out.println("Exit <statement>");
        }

        // Get next token from list, want to check if it is ELSE_KEYWORD
        nextToken = tokenList.peek();

        // Check if nextToken is ELSE_KEYWORD
        if (nextToken.type == TokenType.ELSE_KEYWORD) {
            tokenList.remove();
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));

            // Should be <statement>
            if (!tokenList.isEmpty()) {
                // Get next token from list
                nextToken = tokenList.peek();
                writer.println(String.format("Next token is: %s", nextToken.toString()));
                System.out.println(String.format("Next token is: %s", nextToken.toString()));

                // Enter statement
                writer.println("Enter <statement>");
                System.out.println("Enter <statement>");
                statement(writer, tokenList);
                // Exit statement
                writer.println("Exit <statement>");
                System.out.println("Exit <statement>");
            }
        }
    }


    // expr method (see Assign-Parser-DESCR-Fa23.pdf for complete description of grammar)
    private static void expr(PrintWriter writer, LinkedList<Token> tokenList) {
        // expr always initially enters term
        writer.println("Enter <term>");
        System.out.println("Enter <term>");
        term(writer, tokenList);
        // Exit term
        writer.println("Exit <term>");
        System.out.println("Exit <term>");

        Token nextToken = tokenList.peek();
        // Check if nextToken is a ADD_OP or SUB_OP
        if (nextToken.type == TokenType.ADD_OP || nextToken.type == TokenType.SUB_OP) {
            tokenList.remove();
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));

            // Should be <term>
            if (!tokenList.isEmpty()) {
                // Get nextToken
                nextToken = tokenList.peek();
                writer.println(String.format("Next token is: %s", nextToken.toString()));
                System.out.println(String.format("Next token is: %s", nextToken.toString()));
                // Enter term
                writer.println("Enter <term>");
                System.out.println("Enter <term>");
                term(writer, tokenList);
                // Exit term
                writer.println("Exit <term>");
                System.out.println("Exit <term>");
            }
        }
    }


    // term method (see Assign-Parser-DESCR-Fa23.pdf for complete description of grammar)
    private static void term(PrintWriter writer, LinkedList<Token> tokenList) {
        // term always initially enters factor
        writer.println("Enter <factor>");
        System.out.println("Enter <factor>");
        factor(writer, tokenList);
        // Exit factor
        writer.println("Exit <factor>");
        System.out.println("Exit <factor>");

        Token nextToken = tokenList.peek();
        // Check if nextToken is a MULT_OP or DIV_OP
        if (nextToken.type == TokenType.MULT_OP || nextToken.type == TokenType.DIV_OP) {
            tokenList.remove();
            writer.println(String.format("Next token is: %s", nextToken.toString()));
            System.out.println(String.format("Next token is: %s", nextToken.toString()));

            // Should be <factor>
            if (!tokenList.isEmpty()) {
                // Get nextToken
                nextToken = tokenList.peek();
                writer.println(String.format("Next token is: %s", nextToken.toString()));
                System.out.println(String.format("Next token is: %s", nextToken.toString()));
                // Enter factor
                writer.println("Enter <factor>");
                System.out.println("Enter <factor>");
                factor(writer, tokenList);
                // Exit factor
                writer.println("Exit <factor>");
                System.out.println("Exit <factor>");
            }
        }
    }


    // factor method (see Assign-Parser-DESCR-Fa23.pdf for complete description of grammar)
    private static void factor(PrintWriter writer,  LinkedList<Token> tokenList) {
        // Get next token from list (tokenList cannot be empty here)
        Token nextToken = tokenList.remove();

        switch (nextToken.type) {
            // IDENT
            case TokenType.IDENT:
                break;

            // INT_LIT
            case TokenType.INT_LIT:
                break;

            // ( <expr> )
            case TokenType.LEFT_PAREN:
                // If a next token is present, enter expr
                if (!tokenList.isEmpty()) {
                    nextToken = tokenList.peek();
                    writer.println(String.format("Next token is: %s", nextToken.toString()));
                    System.out.println(String.format("Next token is: %s", nextToken.toString()));
                    // Enter expr
                    writer.println("Enter <expr>");
                    System.out.println("Enter <expr>");
                    expr(writer, tokenList);
                    // Exit expr
                    writer.println("Exit <expr>");
                    System.out.println("Exit <expr>");
                }

                // Get next token from list, should be RIGHT_PAREN
                nextToken = tokenList.peek();
                // Only print "next token" message if token present
                if (!tokenList.isEmpty()) {
                    writer.println(String.format("Next token is: %s", nextToken.toString()));
                    System.out.println(String.format("Next token is: %s", nextToken.toString()));
                }

                // Check next token is RIGHT_PAREN
                if (nextToken.type == TokenType.RIGHT_PAREN) {
                    tokenList.remove();
                } else {
                    writer.println("**Error** - missing right parenthesis");
                    System.out.println("**Error** - missing right parenthesis");
                }
                break;

            // Handle unexpected tokens
            default:
                writer.println(String.format("**Error** - unexpected token: %s", nextToken.type));
                System.out.println(String.format("**Error** - unexpected token: %s", nextToken.type));
                break;
        }
    }
}