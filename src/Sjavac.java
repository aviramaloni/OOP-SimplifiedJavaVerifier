package oop.ex5.main;

import java.io.IOException;
import java.util.List;

/**
 * This class operates the entire Simplified Java Verifier program.
 * It contains the main method of the program, so by using the command
 * 'java oop.ex5.main.Sjavac source file name' (in the CMD), the
 * verifier will start running.
 */
public class Sjavac {

    /**
     * Enum for the output type, no value field needed because it's redundant.
     * (we will use the 'ordinal()' method to get the numeric value.
     */
    private enum OutputType {
        LEGAL(), ILLEGAL(), IO_ERROR();
    }

    /**
     * Number of arguments needed.
     */
    final static int ARGUMENTS_NUMBER = 1, ZERO = 0;

    /**
     * The main method of the program.
     *
     * @param args The arguments given in the command line in order to run the program.
     */
    public static void main(String[] args) {
        try {
            if (args.length < ARGUMENTS_NUMBER)
                throw new IllegalArgumentException("Missing s-Java file name.");
            else if (args.length > ARGUMENTS_NUMBER)
                throw new IllegalArgumentException("Too many arguments.");
            List<String> fileContent = getSjavaLines(args[ZERO]);
            Scope scope = new Scope(fileContent, null, "Global Scope");
            scope.scan();
            finalChecks();
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(OutputType.IO_ERROR.ordinal());
            System.err.println(e);
            return;
        } catch (VariableError | ScopeError | MethodError e) {
            System.out.println(OutputType.ILLEGAL.ordinal());
            System.err.println(e);
            return;

        } finally {
            clearStaticCollections();
        }
        System.out.println(OutputType.LEGAL.ordinal());
    }

    /**
     * Gets the entire file lines in order.
     *
     * @param filePath The source Sjava file path.
     * @return List of all the lines of the file.
     * @throws IOException If the file does not exist.
     */
    private static List<String> getSjavaLines(String filePath)
            throws IOException {
        SjavaFileReader sjavaFileReader = new SjavaFileReader(filePath);
        sjavaFileReader.readFile();
        return sjavaFileReader.getFileContent();
    }

    /**
     * Clears the static collection members from all class.
     */
    private static void clearStaticCollections() {
        Variable.existingVariables.clear();
        Variable.existingArguments.clear();
        Method.allMethods.clear();
        MethodCallsChecker.calls.clear();
        GlobalVariablesChecker.globalVariablesAssignments.clear();
        GlobalVariablesChecker.globalVariablesDeclaration.clear();
        GlobalVariablesChecker.globalVariablesCondition.clear();
        Scope.globalScope = null;
    }

    /**
     * Runs the final check for method call, and global assignments, declarations and conditions.
     * Some methods and variables might be declared after their usage.
     *
     * @throws ScopeError    If there is Scope error.
     * @throws MethodError   If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    private static void finalChecks() throws ScopeError, MethodError, VariableError {
        MethodCallsChecker.CheckCalls();
        GlobalVariablesChecker.checkGlobalAssignments();
        GlobalVariablesChecker.checkGlobalDeclaration();
        GlobalVariablesChecker.checkGlobalCondition();
    }

}