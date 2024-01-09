// Author: David Adams
// Course: CSCI 4200, Fall 2023
// Credit to Daniel Barker, for starter code


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


// Lexical analyzer that takes a text file representing a program as input and prints to terminal
// and an output text file the resulting lexemes and tokens in a textual format
public class Lex {
    // Default path for input text file
    private static final String DEFAULT_INPUT_PATH = "lexInput.txt";
    // Default path for output text file
    private static final String DEFAULT_OUTPUT_PATH = "lexOutput.txt";
    // Delimiters constant string
    private static final String DELIMITERS = " \t+-*/=;()";
    // Path for input text file
    private static String inputPath;
    // Path for output text file
    private static String outputPath;


    // enum for different token types
    private static enum Token {
		ADD_OP,
        ASSIGN_OP,
        DIV_OP,
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

        // try-with PrintWriter for printing to output file and Scanner for reading from input file
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(outputPath), false)); Scanner scanner = new Scanner(new File(inputPath))) {

            // Print header to terminal and output file
            writer.println("David Adams, CSCI4200, Fall 2023, Lexical Analyzer");
            System.out.println("David Adams, CSCI4200, Fall 2023, Lexical Analyzer");
            writer.println("*".repeat(80));
            System.out.println("*".repeat(80));

            // Read input text file line by line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // Print current line to terminal and output file
                writer.println(line);
                System.out.println(line);

                // Tokenize the current line using DELIMITERS
                StringTokenizer tokenizer = new StringTokenizer(line, DELIMITERS, true);

                // Loop through all tokens
                while (tokenizer.hasMoreTokens()) {
                    String currentToken = tokenizer.nextToken();

                    // Evaluate type of current token
                    Token currentTokenType;
                    // Switch on upper case of currentToken as keywords can be any case
                    switch (currentToken.toUpperCase()) {
                        // " " is not treated as an actual token (type of NULL)
                        case " ":
                            currentTokenType = Token.NULL;
                            break;
                        // "\t" (TAB) is not treated as an actual token (type of NULL)
                        case "\t":
                            currentTokenType = Token.NULL;
                            break;
                        case "+":
                            currentTokenType = Token.ADD_OP;
                            break;
                        case "-":
                            currentTokenType = Token.SUB_OP;
                            break;
                        case "*":
                            currentTokenType = Token.MULT_OP;
                            break;
                        case "/":
                            currentTokenType = Token.DIV_OP;
                            break;
                        case "=":
                            currentTokenType = Token.ASSIGN_OP;
                            break;
                        case ";":
                            currentTokenType = Token.SEMICOLON;
                            break;
                        case "(":
                            currentTokenType = Token.LEFT_PAREN;
                            break;
                        case ")":
                            currentTokenType = Token.RIGHT_PAREN;
                            break;
                        case "END":
                            currentTokenType = Token.END_KEYWORD;
                            break;
                        case "EOF":
                            currentTokenType = Token.END_OF_FILE;
                            break;
                        case "IF":
                            currentTokenType = Token.IF_KEYWORD;
                            break;
                        case "PRINT":
                            currentTokenType = Token.PRINT_KEYWORD;
                            break;
                        case "PROGRAM":
                            currentTokenType = Token.PROGRAM_KEYWORD;
                            break;
                        case "READ":
                            currentTokenType = Token.READ_KEYWORD;
                            break;
                        case "THEN":
                            currentTokenType = Token.THEN_KEYWORD;
                            break;
                        // Not an operator or keyword
                        default:
                            // Identifier
                            if (Pattern.matches("[a-zA-Z][a-zA-Z\\d]*", currentToken)) {
                                currentTokenType = Token.IDENT;
                            // Integer literal
                            } else if (Pattern.matches("[\\d]+", currentToken)) {
                                currentTokenType = Token.INT_LIT;
                            // Unknown token
                            } else {
                                currentTokenType = Token.UNKNOWN;
                            }
                            break;
                    }

                    // Check currentTokenType is not NULL (" " or "\t")
                    if (currentTokenType != Token.NULL) {
                        // Print formatted for current token and lexeme to terminal and output file
                        writer.println(String.format("Next token is: %-18s Next lexeme is %s", String.valueOf(currentTokenType), currentToken));
                        System.out.println(String.format("Next token is: %-18s Next lexeme is %s", String.valueOf(currentTokenType), currentToken));
                    }
                }

                // Print blank line to terminal and output file
                writer.println();
                System.out.println();
            }

            // Reached EOF
            writer.println(String.format("Next token is: %-18s Next lexeme is %s", String.valueOf(Token.END_OF_FILE), "EOF"));
            System.out.println(String.format("Next token is: %-18s Next lexeme is %s", String.valueOf(Token.END_OF_FILE), "EOF"));
            // Analysis complete
            writer.print("Lexical analysis of the program is complete!");
            System.out.println("Lexical analysis of the program is complete!");

        } catch (FileNotFoundException e) {
            System.out.println(String.format("\nNo file was found at the specified path: %s\nOR problem with creating output file at path: %s\n\nExiting.", inputPath, outputPath));
            System.exit(1);
        } catch (Exception e) {
            throw(e);
        }
    }
}