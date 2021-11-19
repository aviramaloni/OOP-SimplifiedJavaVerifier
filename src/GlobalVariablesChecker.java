package oop.ex5.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A singleton class with the responsibility of checking variables declarations,
 * assignments and conditions, when appeared before their actual declaration or
 * initialization in the global scope.
 */
public class GlobalVariablesChecker {

    /**
     * The value used in the regex group operation.
     */
    private final static int REGEX_VARIABLE = 1, REGEX_VALUE = 2;

    /**
     * A list of Strings which holds all possible variables assignments from Method scopes.
     */
    public static List<String> globalVariablesAssignments = new ArrayList<>();

    /**
     * A list of Strings which holds all possible variables declarations from Method scopes.
     */
    public static List<String> globalVariablesDeclaration = new ArrayList<>();

    /**
     * A map which holds all possible condition Strings, with their Sconditions scopes from
     * which they created.
     */
    public static HashMap<String, Scope> globalVariablesCondition = new HashMap<>();

    /**
     * the Class constructor.
     */
    private GlobalVariablesChecker() {
    }

    /**
     * This method adds a possible global scope assignment to the relevant list.
     *
     * @param assignment A string contains the relevant assignment.
     */
    public static void addAssignment(String assignment) {
        globalVariablesAssignments.add(assignment);
    }

    /**
     * This method adds a possible global scope declaration to the relevant
     * list.
     *
     * @param declaration A string contains the relevant declaration.
     */
    public static void addDeclaration(String declaration) {
        globalVariablesDeclaration.add(declaration);
    }

    /**
     * This method adds a possible global scope condition to the relevant map.
     *
     * @param condition A string contains the relevant condition.
     * @param scope     The scope from which the condition was created.
     */
    public static void addCondition(String condition, Scope scope) {
        globalVariablesCondition.put(condition, scope);
    }

    /**
     * This method iterates over all possible variables assignments (from Method scopes),
     * and checks if the variable was declared in the global scope after the assignments.
     *
     * @throws VariableError In case the assignment is invalid.
     */
    public static void checkGlobalAssignments() throws VariableError {
        Scope curScope = Scope.globalScope;
        Pattern pattern = Pattern.compile(Scope.REGEX_POSSIBLE_ASSIGN);
        for (String possibleAssignment : globalVariablesAssignments) {
            Matcher matcher = pattern.matcher(possibleAssignment);
            if (matcher.find()) {
                if (curScope.variables.containsKey(matcher.group(REGEX_VARIABLE))) {
                    curScope.variables.get(matcher.group(REGEX_VARIABLE)).setData(matcher.group(REGEX_VALUE),
                            false, Scope.globalScope);
                } else throw new VariableDoesNotExist(matcher.group(REGEX_VARIABLE));
            }
        }
    }

    /**
     * This method iterates over all possible variables declarations (from Method scopes),
     * and checks if the variable was declared and initialized in the global scope after the declaration.
     *
     * @throws InvalidCommand    In case the declaration command in wrong.
     * @throws InvalidMethodCall In case the call was done not from a method scope.
     * @throws VariableError     In case the assignment is invalid.
     * @throws InvalidSyntax     In case there is a problem with the declaration syntax.
     */
    public static void checkGlobalDeclaration()
            throws InvalidCommand, InvalidMethodCall, VariableError, InvalidSyntax {
        for (String declaration : globalVariablesDeclaration) {
            Scope.globalScope.singleLineCommand(declaration + Scope.REGEX_SEMICOLON);
        }
    }

    /**
     * This method checks if all possible condition (with the use of global scope variables) are valid.
     *
     * @throws VariableError In case the condition is of a bad s-Java condition type.
     * @throws ScopeError    In case of an invalid s-Java condition.
     */
    public static void checkGlobalCondition() throws VariableError, ScopeError {
        for (String variableStr : globalVariablesCondition.keySet()) {
            Variable variable = Variable.existingVariables.get(variableStr);
            Variable argument = Variable.existingArguments.get(variableStr);
            if (variable != null) {
                if (variable.initializedScope != Scope.globalScope)
                    throw new InvalidConditionException(variableStr);
                if (!variable.isInitialized()) throw new UninitializedVariable(variableStr);
                else if (!variable.getType().equals(Variable.VARIABLE_TYPE_BOOLEAN.toUpperCase()) &&
                        !variable.getType().equals(Variable.VARIABLE_TYPE_INT.toUpperCase()) &&
                        !variable.getType().equals(Variable.VARIABLE_TYPE_DOUBLE.toUpperCase()))
                    throw new InvalidConditionException(variableStr);
                else return;
            }
            if (argument != null) {
                if (!argument.getType().equals(Variable.VARIABLE_TYPE_BOOLEAN.toUpperCase()) &&
                        !argument.getType().equals(Variable.VARIABLE_TYPE_INT.toUpperCase()) &&
                        !argument.getType().equals(Variable.VARIABLE_TYPE_DOUBLE.toUpperCase()))
                    throw new InvalidConditionException(variableStr);
                else return;
            }
            throw new InvalidConditionException(variableStr);
        }
    }
}
