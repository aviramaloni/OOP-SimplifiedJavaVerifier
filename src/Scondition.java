package oop.ex5.main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A S-java condition (if/while).
 */
public class Scondition extends Scope {

    /**
     * Regex for splitting the condition.
     */
    private final static String SPLIT_REGEX = "\\|\\||\\&\\&";

    /**
     * Regex for finding condition.
     */
    private final static String REGEX_CONDITION = "^\\s*(if|while)(\\s*)*\\((.*)\\)\\s*";

    /**
     * Condition values.
     */
    public final static String TRUE_VALUE = "true", FALSE_VALUE = "false";

    /**
     * Initialing lines for testing condition.
     */
    private final static String INIT_LINE_INT = "int check_int", INIT_LINE_DOUBLE = "double check_double",
            INIT_LINE_BOOLEAN = "boolean check_boolean";

    /**
     * A String which holds the first line of the Scondition scope (the scopes's
     * declaration line).
     */
    private final String declaration;

    /**
     * A list of Strings, which holds the condition/s of the if/while scope.
     * The list's size is greater than one in case of a multiple conditions
     * String condition (when using the '||' or the '&&' operands).
     */
    private final List<String> conditions = new ArrayList<>();


    /**
     * the Scondition Class constructor.
     *
     * @param name       the scope's name.
     * @param scopeData  the scope's code lines.
     * @param outerScope a scope instance from which this constructor was called.
     * @throws ScopeError    If there is Scope error.
     * @throws MethodError   If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    public Scondition(List<String> scopeData, Scope outerScope, String name)
            throws ScopeError, MethodError, VariableError {
        super(scopeData, outerScope, name);
        this.declaration = scopeData.get(ZERO);
        // in order to avoid an infinite loop while scanning the scope's
        // data, we have to remove it's first line (the declaration of the scope)
        this.rawData.remove(ZERO);
        extractCondition();
        checkConditionValidity(this.conditions);
        if (!this.rawData.isEmpty()) scan();
        for (Variable variable : this.variables.values()) variable.delete();
    }

    /**
     * Extracts the if/while condition, in order to check its validity.
     *
     * @throws MissingCondition If the condition is missing (an empty string).
     */
    private void extractCondition() throws MissingCondition, EmptyCondition {
        Pattern pattern = Pattern.compile(REGEX_CONDITION);
        Matcher matcher = pattern.matcher(this.declaration.
                substring(ZERO, this.declaration.length() - ONE).trim());
        if (matcher.find()) {
            String condition = matcher.group(THREE).trim();
            if (condition.isEmpty()) throw new MissingCondition();
            checkMultipleCondition(condition);
        }
    }

    /**
     * This method checks if the given condition contains multiple conditions, according
     * to the s-Java's syntax.
     *
     * @param condition The condition to be checked.
     * @throws EmptyCondition In case any of the given conditions is empty.
     */
    private void checkMultipleCondition(String condition) throws EmptyCondition {
        if (condition.contains(REGEX_OR) || condition.contains(REGEX_AND)) splitCondition(condition);
        else this.conditions.add(condition);
    }


    /**
     * This method splits a String of multiple conditions into individual conditions,
     * according to a given buffer (the OR / AND s-Java operators).
     *
     * @param condition a String of multiple conditions.
     * @throws EmptyCondition In case any of the given conditions is empty.
     */
    private void splitCondition(String condition) throws EmptyCondition {
        String[] splitConditions = condition.split(SPLIT_REGEX);
        // check if all conditions are valid
        for (String splitCondition : splitConditions) {
            if (splitCondition.trim().isEmpty()) throw new EmptyCondition();
            this.conditions.add(splitCondition);
        }
    }

    /**
     * Checks if the condition is valid.
     *
     * @param conditions The conditions to be checked.
     * @throws InvalidConditionException If the condition is invalid
     * @throws UninitializedVariable     If the variable in the condition is uninitialized.
     */
    private void checkConditionValidity(List<String> conditions)
            throws InvalidConditionException, UninitializedVariable {
        for (String condition : conditions) {
            condition = condition.trim();
            if (!checkBooleanReservedWord(condition) && !checkVariableType(condition) &&
                    !checkStringCondition(condition)) {
                if (!checkVariableType(condition) && callFromMethod())
                    GlobalVariablesChecker.addCondition(condition, this);
                else throw new InvalidConditionException(condition);
            }
        }
    }

    /**
     * Checks if the condition string is "true" or "false".
     *
     * @param condition The condition string.
     * @return True if the condition is a boolean keyword, false elsewhere.
     */
    private boolean checkBooleanReservedWord(String condition) {
        return condition.equals(TRUE_VALUE) || condition.equals(FALSE_VALUE);
    }

    /**
     * Checks if the given string variable name is an existing int or double variable.
     *
     * @param variableStr The string variable name.
     * @return True if the existing variable is an int or a double
     * @throws UninitializedVariable If the variable in the condition is uninitialized.
     */
    private boolean checkVariableType(String variableStr) throws UninitializedVariable {
        if (Variable.existingVariables.containsKey(variableStr)) {
            Variable variable = Variable.existingVariables.get(variableStr);
            if (!variable.isInitialized())
                throw new UninitializedVariable(variableStr);
            else {
                return (variable.getType().equals(Variable.VARIABLE_TYPE_BOOLEAN.toUpperCase()) ||
                        variable.getType().equals(Variable.VARIABLE_TYPE_INT.toUpperCase()) ||
                        variable.getType().equals(Variable.VARIABLE_TYPE_DOUBLE.toUpperCase()));
            }
        } else if (Variable.existingArguments.containsKey(variableStr)) {
            Variable arguments = Variable.existingArguments.get(variableStr);
            return (arguments.getType().equals(Variable.VARIABLE_TYPE_BOOLEAN.toUpperCase()) ||
                    arguments.getType().equals(Variable.VARIABLE_TYPE_INT.toUpperCase()) ||
                    arguments.getType().equals(Variable.VARIABLE_TYPE_DOUBLE.toUpperCase()));
        }
        return false;
    }

    /**
     * Checks if the condition is valid.
     *
     * @param condition The string condition
     * @return true if the condition is valid, false elsewhere.
     */
    private boolean checkStringCondition(String condition) {
        Variable intVar, doubleVar, booleanVar;
        boolean[] checkArr = {true, true, true};
        // try to create the test variables
        try {
            intVar = new Variable(INIT_LINE_INT, false, this);
            doubleVar = new Variable(INIT_LINE_DOUBLE, false, this);
            booleanVar = new Variable(INIT_LINE_BOOLEAN, false, this);
        } catch (VariableError e) {
            return false;
        }
        // try to set the given condition
        try {
            intVar.setData(condition, false, this);
        } catch (VariableError e) {
            checkArr[ZERO] = false;
        }
        try {
            doubleVar.setData(condition, false, this);
        } catch (VariableError e) {
            checkArr[ONE] = false;
        }
        try {
            booleanVar.setData(condition, false, this);
        } catch (VariableError e) {
            checkArr[TWO] = false;
        }
        // remove the variables from existence
        Variable.existingVariables.remove(INIT_LINE_INT);
        Variable.existingVariables.remove(INIT_LINE_DOUBLE);
        Variable.existingVariables.remove(INIT_LINE_BOOLEAN);
        intVar.delete();
        doubleVar.delete();
        booleanVar.delete();
        // if one if the data set is valid (true) it will return true;
        return checkArr[ZERO] || checkArr[ONE] || checkArr[TWO];
    }

}
