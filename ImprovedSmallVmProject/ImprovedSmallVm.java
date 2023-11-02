// Author: David Adams
// CSCI 4200


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;


public class ImprovedSmallVm {
    // Scanner for taking user input from terminal
    private Scanner stdin = new Scanner(System.in);
    // Default path for input text file
    private static final String DEFAULT_INPUT_PATH = "mySmallVm_Prog.txt";
    // Default path for output text file
    private static final String DEFAULT_OUTPUT_PATH = "mySmallVm_Output.txt";
    // Path for input text file
    private String inputPath;
    // Path for output text file
    private String outputPath;
    // Hash map to contain variables stored during execution of program
    private HashMap<String, Integer> variables;


    // Constructor
    public ImprovedSmallVm(String inputPath, String outputPath) {
        // Set inputPath
        try {
            this.inputPath = inputPath;
        } catch (Exception e) {
            System.out.println("The passed value for the path to the input file is not valid.\nSetting path to input file to default value of " + DEFAULT_INPUT_PATH + " and continuing.\n");
            this.inputPath = DEFAULT_INPUT_PATH;
        }

        // Set outputPath
        try {
            this.outputPath = outputPath;
        } catch (Exception e) {
            System.out.println("The passed value for the path for the file to print output to is not valid.\nSetting path to output file to default value of " + DEFAULT_OUTPUT_PATH + " and continuing.\n");
            this.outputPath = DEFAULT_OUTPUT_PATH;
        }

        // Initialize variables dictionary
        this.variables = new HashMap<String, Integer>();
    }


    // Get inputPath
    public String getInputPath() {
        return this.inputPath;
    }


    // Set inputPath
    public void setInputPath(String inputPath) {
        try {
            this.inputPath = inputPath;
        } catch (Exception e) {
            System.out.println("The passed value for the path to the input file is not valid.\nResetting input path to last value.\n");
        }
    }


    // Get outputPath
    public String getOutputPath() {
        return this.outputPath;
    }


    // Set outputPath
    public void setOutputPath(String outputPath) {
        try {
            this.outputPath = outputPath;
        } catch (Exception e) {
            System.out.println("The passed value for the path for the file to print output to is not valid.\nResetting output path to last value.\n");
        }
    }


    // Get variables
    public HashMap<String, Integer> getVariables() {
        return this.variables;
    }


    // Print variables to terminal
    public void printVariables() {
        System.out.println(this.variables);
    }


    // Execute program currently loaded in input array
    public void executeProg() {
        // try-with PrintWriter for printing to output file and Scanner for reading from input file
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(this.outputPath), false)); Scanner scanner = new Scanner(new File(this.inputPath))) {
            writer.println("David Adams, CSCI 4200, Fall 2023");
            writer.println("****************************************");
            // Counter to track line number for error messages
            int i = 0;
            // Loop through input text file
            while (scanner.hasNextLine()) {
                // Current statement
                String statement = scanner.nextLine();
                // Return code for statement helper functions
                int returnCode = 0;
                // Get the instruction part of the statement (the first part)
                String instr = statement.split(" ")[0];

                // Need to add extra check for comments (";") as comment does not necessarily have to be followed by " " char
                try {
                    if (instr.substring(0, 1).equals(";")) {
                        instr = ";";
                    }

                // Treat blank lines as comments
                } catch (StringIndexOutOfBoundsException e) {
                    instr = ";";
                }

                // Switch on instruction type
                switch (instr) {
                    case ";":
                        break;
                    case "ADD":
                        returnCode = addInstr(statement);
                        break;
                    case "SUB":
                        returnCode = subInstr(statement);
                        break;
                    case "MUL":
                        returnCode = mulInstr(statement);
                        break;
                    case "DIV":
                        returnCode = divInstr(statement);
                        break;
                    case "IN":
                        returnCode = inInstr(statement, i, writer);
                        break;
                    case "OUT":
                        returnCode = outInstr(statement, i, writer);
                        break;
                    case "STO":
                        returnCode = stoInstr(statement);
                        break;
                    case "HALT":
                        returnCode = -1;
                        break;
                    // No match so syntax error
                    default:
                        // Default error code
                        returnCode = 1;
                        break;
                }

                // Break on HALT(returnCode = -1) or error
                if (returnCode != 0) {
                    // Exit on error
                    if (returnCode != -1) {
                        System.out.println(getErrorMessage(returnCode, i));
                        System.out.println("\nExiting.");
                        System.exit(1);
                    }
                    break;
                }

                i++;
            }
            writer.println("****************************************");
        } catch (FileNotFoundException e) {
            System.out.println(e + "\n\nNo file was found at the specified path: " + this.inputPath + "\n\nExiting.");
            System.exit(1);
        } catch (Exception e) {
            System.out.println(e + "\n\nSomething went wrong.\n\nExiting.");
            System.exit(1);
        }
    }


    // Return the proper error message based on the return code provided
    private String getErrorMessage(int returnCode, int line) {
        String errorMessage = "The statement at line " + Integer.toString(line + 1) + " is not valid SmallVm syntax.";
        switch (returnCode) {
            // General syntax error
            case 1:
                errorMessage += " Please fix your program.";
                break;
            // Error in ADD statement
            case 2:
                errorMessage += " ADD statements should be of the format \"ADD Destination Source1 Source2\", where Destination is a variable name and Source1 and Source2 are either variables names or integer literals.";
                break;
            // Error in SUB statement
            case 3:
                errorMessage += " SUB statements should be of the format \"SUB Destination Source1 Source2\", where Destination is a variable name and Source1 and Source2 are either variables names or integer literals.";
                break;
            // Error in MUL statement
            case 4:
                errorMessage += " MUL statements should be of the format \"MUL Destination Source1 Source2\", where Destination is a variable name and Source1 and Source2 are either variables names or integer literals.";
                break;
            // Error in DIV statement
            case 5:
                errorMessage += " DIV statements should be of the format \"DIV Destination Source1 Source2\", where Destination is a variable name and Source1 and Source2 are either variables names or integer literals.";
                break;
            // Error in IN statement
            case 6:
                errorMessage += " IN statements should be of the format \"IN Destination\", where Destination is a variable name.";
                break;
            // Error in OUT statement
            case 7:
                errorMessage += " OUT statements should be of the format \"OUT Value\", where Value is a variable name or a String enclosed in \"\".";
                break;
            // Error in STO statement
            case 8:
                errorMessage += " STO statements should be of the format \"STO Destination Source\", where Destination is a variable name and Source is a variable name or an integer literal.";
                break;
            // General error
            default:
                errorMessage += " Please fix your program.";
                break;
        }
        return errorMessage;
    }   


    // Helper to identify and get variable values
    private int getVariableValue(String source) {
        int value = 0;
        // Check if source matches variable format
        if (Pattern.matches("[a-zA-Z][a-zA-Z\\d]*", source)) {
            // Check if variable exists
            if (this.variables.containsKey(source)) {
                value = this.variables.get(source);
            } else {
                System.out.println(source + " appears to be a variable, but the variable " + source + " has not been initialized.\n\nExiting.");
                System.exit(1);
            }
        } else {
            value = Integer.parseInt(source);
        }
        return value;
    }


    // ADD instruction; returns 0 on success; returns 2 on failure
    // ADD Destination Source1 Source2 
    // Accepts two integer values, Source1 and Source2, and stores their sum in the Destination variable, Destination
    private int addInstr(String statement) {
        try {
            // Split statement into destination, source1, and source2
            String[] splitStatement = statement.split(" ");
            if (splitStatement.length != 4) {
                return 2;
            }
            String destination = splitStatement[1];
            String source1 = splitStatement[2];
            String source2 = splitStatement[3];
            
            // Get values for source1 and source2
            int value1 = getVariableValue(source1);
            int value2 = getVariableValue(source2);

            // Store <destination, source1 + source2> in variables, or overwrite it if destination key already exists in variables
            this.variables.put(destination, value1 + value2);
        } catch (Exception e) {
            System.out.println(e + "\n");
            return 2;
        }
        return 0;
    }


    // SUB instruction; returns 0 on success; returns 3 on failure
    // SUB Destination Source1 Source2
    // SUB accepts two integer values, Source1 and Source2, and stores the result of (Source1 – Source2) in the Destination variable, Destination
    private int subInstr(String statement) {
        try {
            // Split statement into destination, source1, and source2
            String[] splitStatement = statement.split(" ");
            if (splitStatement.length != 4) {
                return 3;
            }
            String destination = splitStatement[1];
            String source1 = splitStatement[2];
            String source2 = splitStatement[3];
            
            // Get values for source1 and source2
            int value1 = getVariableValue(source1);
            int value2 = getVariableValue(source2);

            // Store <destination, source1 - source2> in variables, or overwrite it if destination key already exists in variables
            this.variables.put(destination, value1 - value2);
        } catch (Exception e) {
            System.out.println(e + "\n");
            return 3;
        }
        return 0;
    }


    // MUL instruction; returns 0 on success; returns 4 on failure
    // MUL Destination Source1 Source2
    // MUL accepts two integer values, Source1 and Source2, and stores the result of (Source1 * Source2) in the Destination variable, Destination
    private int mulInstr(String statement) {
        try {
            // Split statement into destination, source1, and source2
            String[] splitStatement = statement.split(" ");
            if (splitStatement.length != 4) {
                return 4;
            }
            String destination = splitStatement[1];
            String source1 = splitStatement[2];
            String source2 = splitStatement[3];
            
            // Get values for source1 and source2
            int value1 = getVariableValue(source1);
            int value2 = getVariableValue(source2);

            // Store <destination, source1 * source2> in variables, or overwrite it if destination key already exists in variables
            this.variables.put(destination, value1 * value2);
        } catch (Exception e) {
            System.out.println(e + "\n");
            return 4;
        }
        return 0;
    }


    // DIV instruction; returns 0 on success; returns 5 on failure
    // DIV Destination Source1 Source2
    // DIV accepts two integer values, Source1 and Source2, and stores the result of (Source1 / Source2) in the Destination variable, Destination
    private int divInstr(String statement) {
        try {
            // Split statement into destination, source1, and source2
            String[] splitStatement = statement.split(" ");
            if (splitStatement.length != 4) {
                return 5;
            }
            String destination = splitStatement[1];
            String source1 = splitStatement[2];
            String source2 = splitStatement[3];
            
            // Get values for source1 and source2
            int value1 = getVariableValue(source1);
            int value2 = getVariableValue(source2);

            // Store <destination, source1 / source2> in variables, or overwrite it if destination key already exists in variables
            this.variables.put(destination, value1 / value2);
        } catch (Exception e) {
            System.out.println(e + "\n");
            return 5;
        }
        return 0;
    }


    // IN instruction; returns 0 on success; returns 6 on failure
    // IN Destination
    // Prompts the user for an integer input and stores the input integer in the variable Destination
    private int inInstr(String statement, int i, PrintWriter writer) {
        int num = 0;
        try {
            // Get destination from statement
            String[] splitStatement = statement.split(" ");
            if (splitStatement.length != 2) {
                return 6;
            }
            String destination = statement.split(" ")[1];

            // Read input from terminal and parse to an integer
            num = Integer.parseInt(stdin.nextLine());
            this.variables.put(destination, num);
        // Input was not an integer
        } catch (NumberFormatException e) {
            System.out.println("The previous input was not an integer. Please enter an integer value:");
            inInstr(statement, i, writer);
        } catch (Exception e) {
            System.out.println(e + "\n");
            return 6;
        }
        // Add num to output array
        writer.println(Integer.toString(num));
        return 0;
    }


    // OUT instruction; returns 0 on success; returns 7 on failure
    // OUT Value
    // Displays Value to terminal on a new line. Value can be either a variable or a String enclosed in ""
    private int outInstr(String statement, int i, PrintWriter writer) {
        try {
            // Remove instruction portion from statement
            String str = statement.substring(4);

            // Check if str matches proper format for String literal
            // Which includes Strings enclosed both in "" and “” apparently according to the example program but not the documentation
            if ((str.substring(0, 1).equals("\"") || str.substring(0, 1).equals("“")) && (str.substring(str.length() - 1).equals("\"") || str.substring(str.length() - 1).equals("”"))) {
                // Print to terminal
                System.out.println(str.substring(1, str.length() - 1));
                // Add printed string to output array
                writer.println(str.substring(1, str.length() - 1));

            // Check if str matches variable format
            } else if (Pattern.matches("[a-zA-Z][a-zA-Z\\d]*", str)) {
                // Check if variable exists
                if (this.variables.containsKey(str)) {
                    // Replace variable with integer value
                    str = Integer.toString(this.variables.get(str));
                } else {
                    System.out.println(str + " appears to be a variable, but the variable " + str + " has not been initialized.\n\nExiting.");
                    System.exit(1);
                }
                // Print to terminal
                System.out.println(str);
                // Add printed stringto output array
                writer.println(str);
            } else {
                System.out.println(str);
                System.out.println(str.substring(0, 1));
                System.out.println(str.substring(str.length() - 1));
                return 7;
            }
        } catch (Exception e) {
            System.out.println(e + "\n");
            return 7;
        }
        return 0;
    }


    // STO instruction; returns 0 on success; returns 8 on failure
    // STO Destination Source
    // STO stores the value of Source in Destination variable. Source can be either a variable or an integer constant
    private int stoInstr(String statement) {
        try {
            // Split statement into destination and source
            String[] splitStatement = statement.split(" ");
            if (splitStatement.length != 3) {
                return 8;
            }
            String destination = splitStatement[1];
            String source = splitStatement[2];
            
            // Get value for source
            int value = getVariableValue(source);

            // Store <destination, source> in variables, or overwrite it if destination key already exists in variables
            this.variables.put(destination, value);
        } catch (Exception e) {
            System.out.println(e + "\n");
            return 8;
        }
        return 0;
    }




    // Create SmallVm virtual machine, load SmallVm program from external file, and execute loaded program with output going to external file
    //
    // Command line arguments:
    // args[0]=inputPath- String representing path to text file to be used as program for input
    // args[1]=outputPath- String representing path to text file for output to be printed to
    public static void main(String[] args) {
        ImprovedSmallVm vm;
        switch (args.length) {
            // No command line arguments passed => use all defaults
            case 0:
                vm = new ImprovedSmallVm(DEFAULT_INPUT_PATH, DEFAULT_OUTPUT_PATH);
                break;
            // Command line argument provided for inputPath
            case 1:
                vm = new ImprovedSmallVm(args[0], DEFAULT_OUTPUT_PATH);
                break;
            // Command line argument provided for inputPath and outputPath
            case 2:
                vm = new ImprovedSmallVm(args[0], args[1]);
                break;
            default:
                vm = new ImprovedSmallVm(DEFAULT_INPUT_PATH, DEFAULT_OUTPUT_PATH);
                break;
        }

        // Execute program loaded into input array, printing output to text file at outputPath
        vm.executeProg();
    }
}