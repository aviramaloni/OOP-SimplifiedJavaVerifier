package oop.ex5.main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A singleton Class which holds references to the existing methods
 * in the program, alongside their names.
 */
public class MethodCallsChecker {


    /**
     * A list of String, which holds all the code lines in which
     * a method is to be called.
     */
    public static List<String> calls = new ArrayList<>();

    /**
     * The Class constructor.
     */
    private MethodCallsChecker() {
    }

    /**
     * This method add a call to the list of calls.
     *
     * @param call The call to be addded.
     */
    public static void addCall(String call) {
        calls.add(call);
    }

    /**
     * This method checks if the calls (in the list of calls) are valid.
     * A valid s-Java call must refer to an existing method, with an appropriate
     * number of arguments, with a matching type.
     *
     * @throws BadArgumentsNum    In case the number of arguments given in the call
     *                            don't match the actual method number of arguments.
     * @throws VariableError      In case the given arguments type don't match the actual
     *                            method arguments type.
     * @throws MethodDoesNotExist In case of a call to a non-existing method.
     */
    public static void CheckCalls() throws BadArgumentsNum, VariableError, MethodDoesNotExist {
        Pattern pattern = Pattern.compile(Scope.REGEX_POSSIBLE_METHOD);
        Matcher matcher;
        String methodName;
        String arguments;
        for (String call : calls) {
            matcher = pattern.matcher(call.substring(Scope.ZERO, call.length() - Scope.ONE));
            if (matcher.find()) {
                methodName = matcher.group(Scope.ONE);
                arguments = matcher.group(Scope.TWO).substring(Scope.ONE,
                        matcher.group(Scope.TWO).length() - Scope.ONE);
                if (Method.allMethods.containsKey(methodName))
                    checkPossibleArguments(Method.allMethods.get(methodName), arguments);
                else throw new MethodDoesNotExist(methodName);
            } else throw new MethodDoesNotExist(call);
        }
    }

    /**
     * This method checks if the arguments given in the method call line, matches the
     * actual arguments of the method (by amount and by type).
     *
     * @param scope     The relevant method.
     * @param arguments A String which holds the arguments given in the call line.
     * @throws BadArgumentsNum In case of an inappropriate number of arguments.
     * @throws VariableError   In case of an inappropriate arguments type.
     */
    private static void checkPossibleArguments(Method scope, String arguments)
            throws BadArgumentsNum, VariableError {
        String[] splitArguments = arguments.split(Scope.REGEX_COMMA);
        // in case no arguments needed
        if (splitArguments[Scope.ZERO].equals(Scope.REGEX_EMPTY) && scope.arguments.size() == Scope.ZERO)
            return;
        // in case of a wrong number of arguments
        if (splitArguments.length != scope.arguments.size()) throw new BadArgumentsNum(scope.getName());
        List<Variable> orderedArguments = new ArrayList<>(scope.arguments.values());
        for (int i = 0; i < splitArguments.length; i++) {
            orderedArguments.get(i).setData(splitArguments[i].trim(),
                    true, Scope.globalScope);
        }
    }
}


