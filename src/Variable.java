package oop.ex5.main;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Variable class.
 */
public class Variable {

    /**
     * Regex variable types.
     */
    public final static String VARIABLE_FINAL = "final", VARIABLE_TYPE_INT = "int",
            VARIABLE_TYPE_DOUBLE = "double", VARIABLE_TYPE_STRING = "String", VARIABLE_TYPE_CHAR = "char",
            VARIABLE_TYPE_BOOLEAN = "boolean", VARIABLE_INIT_CONFIG = "";

    /**
     * The value used in the regex group operation.
     */
    private final static int ONE = 1, TWO = 2, THREE = 3;

    /**
     * Regex for finding full Initialized variable or just declared variable.
     */
    private final static String REGEX_FULL_INIT = "^(\\S+)\\s+(\\S+)\\s*=\\s*(.*)$",
            REGEX_HALF_INIT = "^(\\S+)\\s+(\\S+)$";

    /**
     * Regex of all possible saved keywords.
     */
    public final static String SAVED_WORDS =
            "^(int|double|String|char|boolean|final|if|while|true|false|void|return)$";

    /**
     * Short regex expression.
     */
    public final static String REGEX_DIGIT_WORDS = "(?=\\D)(?=\\W)", REGEX_DIGIT = "^\\d",
            REGEX_UNDERSCORE = "^_$", REGEX_STARTS_UNDERSCORE = "^_";

    /**
     * All the current existing variables n the program sorted in a HashMap (name, Variable objects).
     */
    public static HashMap<String, Variable> existingVariables = new HashMap<>();

    /**
     * All the current existing arguments in the program sorted in a HashMap (name, Variable objects).
     */
    public static HashMap<String, Variable> existingArguments = new HashMap<>();

    /**
     * A Type Enum.
     */
    private enum Type {
        /**
         * An int type.
         */
        INT("^int$", "^(-\\d+)$|^(\\d+)$"),

        /**
         * A double type.
         */
        DOUBLE("^double$", "^-(\\d*\\.\\d+)$|^-(\\d+\\.\\d*)$|^(\\d*\\.\\d+)$|" +
                "^(\\d+\\.\\d*)$|^(-\\d+)$|^(\\d+)$"),

        /**
         * a String type.
         */
        STRING("^String$", "^\"(.*)\"$"),

        /**
         * A char type.
         */
        CHAR("^char$", "^'(.)'$"),

        /**
         * A boolean type.
         */
        BOOLEAN("^boolean$", "^true$|^false$|^-(\\d*\\.\\d+)$|^-(\\d+\\.\\d*)$|" +
                "^(\\d*\\.\\d+)$|^(\\d+\\.\\d*)$|^(-\\d+)$|^(\\d+)$");

        /**
         * Type regular expression for finding the Variable type,
         * and a value regular expression for finding the value.
         */
        Pattern typePattern, valuePattern;

        /**
         * the Constructor of the Type.
         *
         * @param regexType  Regular expression for finding the Variable type.
         * @param regexValue Regular expression for finding the value of the variable.
         */
        Type(String regexType, String regexValue) {
            this.typePattern = Pattern.compile(regexType);
            this.valuePattern = Pattern.compile(regexValue);
        }
    }


    /**
     * A generic Data class for Variable.
     *
     * @param <T> The data type (int/double/String/char/boolean).
     */
    private static class Data<T> {

        /**
         * The value of the data.
         */
        private final T value;

        /**
         * The constructor of the Data.
         *
         * @param value The value of the Data.
         */
        Data(T value) {
            this.value = value;
        }

        /**
         * Converts and returns the value of the data to String.
         *
         * @return The value of the Data as a String.
         */
        @Override
        public String toString() {
            return this.value.toString();
        }
    }

    /**
     * The name of the Variable.
     */
    private String name;

    /**
     * The data of the variable.
     */
    private Data<?> data;

    /**
     * The Type of the Variable.
     */
    private Type type;

    /**
     * The scope from which the variable was created.
     */
    public final Scope declaredScope;

    /**
     * The scope from which the variable was initialized.
     */
    public Scope initializedScope = null;

    /**
     * A boolean representing if the variable is initialized.
     */
    private boolean isInitialized;

    /**
     * A boolean representing if the variable is final.
     */
    private final boolean isFinal;

    /**
     * A boolean representing if the variable is an argument of a method.
     */
    private final boolean isArgument;

    /**
     * The Contractor of the Variable.
     *
     * @param initializeLine The initializing line (trimmed!)
     * @param isArgument     True if this variable should is, else false.
     * @param declaredScope  The scope where the variable was declared.
     * @throws VariableError When the Variable declaration goes wrong.
     */
    public Variable(String initializeLine, boolean isArgument, Scope declaredScope) throws VariableError {
        this.declaredScope = declaredScope;
        this.isArgument = isArgument;
        this.isFinal = initializeLine.startsWith(VARIABLE_FINAL);
        updateParameters(isFinal ? initializeLine.replaceFirst(VARIABLE_FINAL, Scope.REGEX_EMPTY)
                : initializeLine);
        if (this.isArgument) existingArguments.put(this.name, this);
        else existingVariables.put(this.name, this);
    }

    /**
     * extracts and updates the variable parameters (type, name and data) for the initialize line.
     *
     * @param initializeLine The initialize line without the final keyword.
     * @throws VariableError If updating the parameter is unsuccessful it throws a VariableError.
     */
    private void updateParameters(String initializeLine) throws VariableError {
        Matcher fullMatcher = Pattern.compile(REGEX_FULL_INIT).
                matcher(initializeLine.trim());
        Matcher partMatcher = Pattern.compile(REGEX_HALF_INIT).matcher(initializeLine.trim());
        // with initialization (<Type> <Name> <=> <Data>)
        if (fullMatcher.find()) {
            if (this.isArgument) throw new VariableInitInMethodDeclaration(fullMatcher.group(TWO));
            this.type = extractType(fullMatcher.group(ONE));
            this.name = extractName(fullMatcher.group(TWO));
            this.data = extractData(fullMatcher.group(THREE),
                    false, initializeLine.trim(), this.declaredScope);
            this.isInitialized = true;
            this.initializedScope = declaredScope;
        }
        // if this is variable is not going to be initialized yet (<Type> <Name>)
        else if (partMatcher.find()) {
            if (this.isFinal && !this.isArgument)
                throw new UninitializedFinalVariable(partMatcher.group(TWO));
            this.type = extractType(partMatcher.group(ONE));
            this.name = extractName(partMatcher.group(TWO));
            this.isInitialized = false;
        } else throw new BadVariableDeclaration(initializeLine, this.isArgument);
    }

    /**
     * Finds the Variable Type.
     *
     * @param typeStr The String that should hold the variable type.
     * @return the Type of the Variable.
     * @throws VariableError If the given Type is invalid throws a VariableError.
     */
    private Type extractType(String typeStr) throws VariableError {
        Matcher matcher;
        for (Type type : Type.values()) {
            matcher = type.typePattern.matcher(typeStr);
            if (matcher.find()) return type;
        }
        throw new BadVariableType(typeStr);
    }

    /**
     * Finds the Variable name.
     *
     * @param nameStr The name String (from the initializing line).
     * @return The name of the Variable.
     * @throws VariableError If the given name is invalid throws a VariableError.
     */
    private String extractName(String nameStr) throws VariableError {
        // if the name is already taken in this scope
        if (this.declaredScope.variables.containsKey(nameStr) ||
                this.declaredScope.arguments.containsKey(nameStr))
            throw new BadVariableNameAlreadyExists(nameStr);
        // if the name starts with a digit
        if (Pattern.compile(REGEX_DIGIT).matcher(nameStr).find()) {
            throw new BadVariableNameDigit(nameStr);
            // if the name is a only a single underscore
        } else if (Pattern.compile(REGEX_UNDERSCORE).matcher(nameStr).find()) {
            throw new BadVariableNameUnderscore(nameStr);
            // if the name contains illegal characters (not letters or digits)
        } else if (Pattern.compile(REGEX_DIGIT_WORDS).matcher(nameStr).find()) {
            throw new BadVariableNameIllegal(nameStr);
            // if the name is one of the reserved keyword
        } else if (Pattern.compile(SAVED_WORDS)
                .matcher(nameStr).find()) {
            throw new BadVariableNameSavedKeyword(nameStr);
        } else return nameStr;
    }

    /**
     * Finds the data of the Variable
     *
     * @param dataStr            the data String.
     * @param isFromCallsHandler If the method was called from the callsHandler class.
     * @param initializeLine     The initialization line of the variable.
     * @param scope              The scope from which the variable was created.
     * @return The matching Data class with the a data value.
     * @throws VariableError If the Variable data is invalid.
     */
    private Data<?> extractData(String dataStr, boolean isFromCallsHandler, String initializeLine,
                                Scope scope) throws VariableError {
        // checks for an already existing variable or argument
        Variable existingVariable = getExistsInVariablesOrArguments(dataStr);
        if (existingVariable != null) {
            if (!existingVariable.isArgument &&
                    (!existingVariable.isInitialized ||
                            !initializedInOuterScope(existingVariable, scope, isFromCallsHandler)))
                throw new UninitializedParameter(existingVariable.getName());
                // checks if this is a valid casting
            else if (this.getType().equals(existingVariable.getType()) ||
                    (this.type == Type.DOUBLE && existingVariable.getType().
                            equals(VARIABLE_TYPE_INT.toUpperCase())) ||
                    (this.type == Type.BOOLEAN && (existingVariable.getType().
                            equals(VARIABLE_TYPE_INT.toUpperCase())
                            || existingVariable.getType().
                            equals(VARIABLE_TYPE_DOUBLE.toUpperCase())))) {
                return existingVariable.getDataObject();
            } else throw new IllegalVariableCasting(this, existingVariable);
        }

        // creates a new data value
        Matcher matcher = this.type.valuePattern.matcher(dataStr);
        if (!matcher.find()) {
            // if the variable is self assigned
            if (this.name.equals(dataStr)) throw new SelfAssign(this.name);
            // if there is no existing variable or argument
            if (this.declaredScope.callFromMethod() && initializeLine != null) {
                GlobalVariablesChecker.addDeclaration(initializeLine);
                return null;
            } else throw new BadVariableData(this, dataStr);
        }
        switch (this.type) {
            case INT:
                return new Data<>(Integer.parseInt(dataStr));
            case DOUBLE:
                return new Data<>(Double.parseDouble(dataStr));
            case STRING:
                return new Data<>(matcher.group(ONE));
            case CHAR:
                return new Data<>(matcher.group(ONE).charAt(Scope.ZERO));
            case BOOLEAN:
                if (dataStr.equals(Scondition.TRUE_VALUE) || dataStr.equals(Scondition.FALSE_VALUE))
                    return new Data<>(Boolean.parseBoolean(dataStr));
                else
                    return new Data<>(!(Double.parseDouble(dataStr) == Scope.ZERO));
        }
        return null;
    }

    /**
     * This method checks if the given variable was initialized in the given scope, or
     * an ancient scope of his.
     *
     * @param variable           The variable to be checked.
     * @param scope              The scope from which the variable was assigned.
     * @param isFromCallsHandler A boolean arguments which indicates whether the call to this method
     *                           was form the CallHandler class.
     * @return True in case the variable was initialized in an outer scope of the scope given, false
     * otherwise.
     */
    private boolean initializedInOuterScope(Variable variable, Scope scope, boolean isFromCallsHandler) {
        if (isFromCallsHandler) return true;
        while (scope != null) {
            if (variable.initializedScope == scope) return true;
            else scope = scope.outerScope;
        }
        return false;
    }

    /**
     * This method returns a reference to a Variable with a key equals to the
     * given String. The Variable returned may be a variable, an argument, or a null
     * pointer in case no variable or argument with a key equals to the given String was found.
     *
     * @param dataStr A String of the desired Variable key.
     * @return A reference to the desired variable, or a null pointer in case no
     * matching varible was found.
     */
    private Variable getExistsInVariablesOrArguments(String dataStr) {
        if (existingVariables.containsKey(dataStr)) {
            return existingVariables.get(dataStr);
        } else return existingArguments.getOrDefault(dataStr, null);
    }


    /**
     * Sets the Variable data to the given data.
     *
     * @param dataStr            The new Variable Data as a String.
     * @param isFromCallsHandler True if the setData method is called from the CallsHandler, else false.
     * @param scope              The scope from which the variable was created.
     * @throws VariableError If the Variable data is invalid.
     */
    public void setData(String dataStr, boolean isFromCallsHandler, Scope scope) throws VariableError {
        if (this.isFinal && !isFromCallsHandler) throw new IllegalFinalDataChange(this);
        else this.data = extractData(dataStr, isFromCallsHandler, null, scope);
        this.isInitialized = true;
        if (this.initializedScope == null) this.initializedScope = scope;
    }

    /**
     * Gets the Variable name.
     *
     * @return The Variable name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the Variable type.
     *
     * @return the Variable Type.
     */
    public String getType() {
        return this.type.toString();
    }

    /**
     * Gets the Variable data.
     *
     * @return the Variable Data.
     */
    public String getData() {
        return this.data.toString();
    }

    /**
     * Gets the Variable Data object.
     *
     * @return the Variable Data object.
     */
    private Data<?> getDataObject() {
        return this.data;
    }

    /**
     * Gets the initialized status.
     *
     * @return True if the Variable is initialized, else false.
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Gets the final status.
     *
     * @return True if the Variable is final, else false.
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Removes the Variable object from the existing variables hash set.
     */
    public void delete() {
        existingVariables.remove(this.name);
    }
}

