/*
 * Student:   Daniel Barker
 * Professor: Dr. Salimi
 * Course: 	  CSCI4200: Programming Languages
 * Date:      4 April 2023
 */
 
import java.util.*;
import java.io.*;

// Custom exception to handle errors
class CustomException extends Exception {
	public CustomException(String errorMessage) {
		 super(errorMessage);
	}
}

public class LexOriginal {

	// Global declarations of variables
	static final int MAX_LEXEME_LEN = 100;
	static Token charClass;                            // Compare to enum to identify the character's class
	static int lexLen;                                 // Current lexeme's length
	static char lexeme[] = new char[MAX_LEXEME_LEN];   // Current lexeme's character array
	static char nextChar;
	static Token nextToken;
	static int charIndex;
	static FileWriter myOutput;
	static boolean onFirstChar = true;                 // To keep track of whether currently on first char of line
	static final int MAX_LEXEME_USABLE_INDEX = 98;     // Replaces int lit 98 in addChar(); can go up to 99

	//Tokens and categories
	enum Token {
		INT_LIT,
		IDENT,
		ASSIGN_OP,
		ADD_OP,
		SUB_OP,
		MULT_OP,
		DIV_OP,
		LEFT_PAREN,
		RIGHT_PAREN,
		LETTER,
		DIGIT,
		UNKNOWN,
		END_KEYWORD,
		PRINT_KEYWORD,
		PROGRAM_KEYWORD,
		SEMICOLON,
		END_OF_FILE,
		IF_KEYWORD,
		READ_KEYWORD,
		THEN_KEYWORD
	}
 
	// Driver method
	// @throws IOException
	public static void main(String[] args) throws IOException {

		String line;
		Scanner scan;
	    myOutput = new FileWriter("lexOutput.txt");

		// Print Header Info to Terminal and Output File
		String headerInfo = "Daniel Barker, CSCI4200, Spring 2023, Lexical Analyzer";
		String headerAsterisks = "*".repeat(80);
		System.out.print(headerInfo + "\n"); 
		myOutput.write(headerInfo + "\n");
		System.out.print(headerAsterisks + "\n");
		myOutput.write(headerAsterisks + "\n");

		// Open the file, and scan each line for lexical analysis 
		try {
			scan = new Scanner(new File("lexInput.txt"));

			// For each line, get each character 
			while (scan.hasNextLine()) {

				line = scan.nextLine().trim();         // Trims leading and trailing whitespace from each line

				if(!line.trim().isEmpty()){            // Skip blank lines in input file
					System.out.println(line);
					myOutput.write(line + "\n");

					charIndex = 0;
					onFirstChar = true;                // Set true at beginning of each new line of input program

					if (getChar(line)) {
						// Perform lexical analysis within array bounds 
						while (charIndex < line.length()) {
							lex(line);
						}
	
						System.out.print("\n");
						myOutput.write("\n");
					}
				}

			}

			// If there are no more lines, it must be the end of file 
			if (!scan.hasNext()) {
				System.out.printf("Next token is: %-18s Next lexeme is %s\n", "END_OF_FILE", "EOF");
				myOutput.write(String.format("Next token is: %-18s Next lexeme is %s\n", "END_OF_FILE", "EOF"));
				System.out.print("Lexical analysis of the program is complete!\n");
				myOutput.write("Lexical analysis of the program is complete!");
			}
			
			scan.close();
		}

		// Exceptions / errors are printed to terminal and to output file
		catch (CustomException ce) {
			System.out.println(ce.getMessage() + "\n");
			myOutput.write(ce.getMessage());
		}

		catch (FileNotFoundException e) {
			System.out.println("Cannot find file OR Problem with output file creation.");
			myOutput.write("Cannot find file OR Problem with output file creation." + "\n");
			System.out.println(e.toString());
			myOutput.write(e.toString());
		}

		catch (Exception e) {
			e.printStackTrace();
			StringWriter stringWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(stringWriter));
			String eStackTrace = stringWriter.toString();
			myOutput.write(eStackTrace);
		}
		
		myOutput.close();
	}

	// Associate UNKNOWN lexemes with appropriate tokens
	// @throws CustomException
	private static Token lookup(char ch) throws CustomException{
		switch (ch) {
			case '(':
				addChar();
				nextToken = Token.LEFT_PAREN;
				break;
			case ')':
				addChar();
				nextToken = Token.RIGHT_PAREN;
				break;
			case '+':
				addChar();
				nextToken = Token.ADD_OP;
				break;
			case '-':
				addChar();
				nextToken = Token.SUB_OP;
				break;
			case '*':
				addChar();
				nextToken = Token.MULT_OP;
				break;
			case '/':
				addChar();
				nextToken = Token.DIV_OP;
				break;
			case '=':
				addChar();
				nextToken = Token.ASSIGN_OP;
				break;
			case ';':
				addChar();
				nextToken = Token.SEMICOLON;
				break;
			default:
				/* If in default, Token must be one of
					PROGRAM_KEYWORD :: Token
					END_KEYWORD :: Token
					PRINT_KEYWORD :: Token
					IF_KEYWORD :: Token
					READ_KEYWORD :: Token
					THEN_KEYWORD :: Token */
			}
		return nextToken;
	}
	
	// Adds character (nextChar) to lexeme char array in preparation to be printed
	// @throws CustomException
	private static boolean addChar() throws CustomException{
		
		if (lexLen <= MAX_LEXEME_USABLE_INDEX) {
			lexeme[lexLen++] = nextChar;
			lexeme[lexLen] = 0;
			return true;
		}
		
		else {
			throw new CustomException("Error -- lexeme is too long.");
		}

	}
	
	// Get next char on line, determine its charClass, and increment charIndex
	private static boolean getChar(String ln) {

		// Only increment charIndex and check if out of bounds if not at first char of line due to case handling in lex()
		if(!onFirstChar){
			if (++charIndex >= ln.length()) {
				return false;
			}
		}

		nextChar = ln.charAt(charIndex);
		
		if (Character.isDigit(nextChar)) {
			charClass = Token.DIGIT;
		} 
			
		else if (Character.isAlphabetic(nextChar)) {
			charClass = Token.LETTER;
		}
			
		else {
			charClass = Token.UNKNOWN;
		}

		onFirstChar = false;                           // For each line, set false after first run of getChar()
		return true;
	}

	// Skip whitespace in line
	public static boolean getNonBlank(String ln) {
		while (Character.isSpaceChar(nextChar) || nextChar == '	') {
			if (!getChar(ln)){
				return false;
			}
		}
		return true;
	}
	
	// Lexical Analyzer Method (print token and lexeme in input line)
	// @throws IOException, Custom Exception
	public static Token lex(String ln) throws IOException, CustomException {
		
		lexLen = 0;
		getNonBlank(ln);
		
		switch (charClass) {

		// Parse identifiers 
		case LETTER:
			nextToken = Token.IDENT;
			addChar();
			
			if (getChar(ln)) {
				while (charClass == Token.LETTER || charClass == Token.DIGIT) {
					addChar();
					if (!getChar(ln)) {
						break;
					}
				}
				
				if (charClass == Token.UNKNOWN && charIndex == ln.length()) {
					charIndex--;
				}
			}
			break;
			
		// Parse integer literals 
		case DIGIT:
			nextToken = Token.INT_LIT;
			addChar();
			
			if (getChar(ln)) {
				while (charClass == Token.DIGIT) {
					addChar();
					
					if(!getChar(ln)) {
						break;
					}
				}
				
				if (charClass == Token.UNKNOWN && charIndex == ln.length()) {
					charIndex--;
				}
			}
			break;
			
		// Parentheses, operators, symbols
		case UNKNOWN:
			lookup(nextChar);
			getChar(ln);
			break;
		default:
			nextToken = Token.UNKNOWN;
			break;
		}
		
		// Handle when the Token is PROGRAM, END, PRINT, IF, READ, and THEN
		// Any combinations of letter cases is allowed in Tokens
		switch (String.valueOf(lexeme, 0, lexLen).toUpperCase()) {
			case "PROGRAM":
				nextToken = Token.PROGRAM_KEYWORD;
				break;
			case "END":
				nextToken = Token.END_KEYWORD;
				break;
			case "PRINT":
				nextToken = Token.PRINT_KEYWORD;
				break;
			case "IF":
				nextToken = Token.IF_KEYWORD;
				break;
			case "READ":
				nextToken = Token.READ_KEYWORD;
				break;
			case "THEN":
				nextToken = Token.THEN_KEYWORD;
				break;
		}
		
		// Print and output each token and its respective lexeme 
		System.out.printf("Next token is: %-18s Next lexeme is %s\n", String.valueOf(nextToken), String.valueOf(lexeme, 0, lexLen));
		myOutput.write(String.format("Next token is: %-18s Next lexeme is %s\n", String.valueOf(nextToken), String.valueOf(lexeme, 0, lexLen)));

		return nextToken;
	}
}