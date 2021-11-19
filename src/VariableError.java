package oop.ex5.main;

/**
 * Variable Error class.
 */
public class VariableError extends Exception {

    /**
     * super constructor for a  Variable Error
     *
     * @param errorMessage The error message.
     */
    public VariableError(String errorMessage) {
        super(errorMessage);
    }
}

/**
 * Bad Variable declaration, used only when there is a general fault.
 */
class BadVariableDeclaration extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableDeclaration(String initializeLine, boolean isArguments) {
        super("'" + initializeLine + "' is not a valid " +
                ((isArguments) ? "argument" : "variable") + " declaration.");
    }
}

/**
 * Bad Variable Name error, starts with a digit.
 */
class BadVariableNameDigit extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableNameDigit(String name) {
        super("'" + name + "' is an invalid Variable Name, Can't start with a digit.");
    }
}

/**
 * Bad Variable Name error, underscore.
 */
class BadVariableNameUnderscore extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableNameUnderscore(String name) {
        super("'" + name + "' is an invalid Variable Name, can't be only an underscore.");
    }
}

/**
 * Bad Variable Name error, contains illegal characters .
 */
class BadVariableNameIllegal extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableNameIllegal(String name) {
        super("'" + name + "' is an invalid Variable Name, contains illegal characters.");
    }
}

/**
 * Bad Variable Name error, saved keyword.
 */
class BadVariableNameSavedKeyword extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableNameSavedKeyword(String name) {
        super("'" + name + "' is an invalid Variable Name, This name is a saved keyword.");
    }
}

/**
 * Bad Variable Name error, already exists.
 */
class BadVariableNameAlreadyExists extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableNameAlreadyExists(String name) {
        super("'Variable '" + name + "' is already defined in the scope.");
    }
}

/**
 * Bad Variable Type error.
 */
class BadVariableType extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableType(String type) {
        super("" + type + " is an invalid Variable type.");
    }
}

/**
 * Bad Variable data error.
 */
class BadVariableData extends VariableError {
    /**
     * The Error constructor.
     */
    public BadVariableData(Variable variable, String data) {
        super(data + " is an invalid value for a " +
                variable.getType().toLowerCase() + " variable.");
    }
}

/**
 * Trying to change a final Variable.
 */
class IllegalFinalDataChange extends VariableError {
    /**
     * The Error constructor.
     */
    public IllegalFinalDataChange(Variable variable) {
        super("Cannot assign a value to final variable '" + variable.getName() + "'.");
    }
}

/**
 * Trying to initialize a variable with a different type variable
 */
class IllegalVariableCasting extends VariableError {
    /**
     * The Error constructor.
     */
    public IllegalVariableCasting(Variable thisVariable, Variable otherVariable) {
        super("Cannot assign a " + otherVariable.getType() + " member to a " +
                thisVariable.getType() + " variable.");
    }
}

/**
 * Variable does not exist.
 */
class VariableDoesNotExist extends VariableError {
    /**
     * The Error constructor.
     */
    public VariableDoesNotExist(String name) {
        super("Cannot resolve symbol '" + name + "'.");
    }
}

/**
 * Invalid variable assignment
 */
class InvalidVariableAssignment extends VariableError {
    /**
     * The Error constructor.
     */
    public InvalidVariableAssignment(String possibleAssignment) {
        super(possibleAssignment + " is not a valid assignment.");
    }
}

/**
 * when trying to call a method with a wrong number of arguments.
 */
class UninitializedParameter extends VariableError {
    /**
     * The Error constructor.
     */
    UninitializedParameter(String param) {
        super(param + " is not initialized.");
    }
}

/**
 * when trying to call a method with a wrong number of arguments.
 */
class VariableInitInMethodDeclaration extends VariableError {
    /**
     * The Error constructor.
     */
    VariableInitInMethodDeclaration(String variable) {
        super(variable + " can not be initialized in a method declaration.");
    }
}

/**
 * Uninitialized Variable.
 */
class UninitializedVariable extends VariableError {
    /**
     * The Error constructor.
     */
    UninitializedVariable(String variable) {
        super(variable + " is uninitialized.");
    }
}

/**
 * Uninitialized final Variable.
 */
class UninitializedFinalVariable extends VariableError {
    /**
     * The Error constructor.
     */
    UninitializedFinalVariable(String variable) {
        super("Final " + variable +
                " is uninitialized.");
    }
}

/**
 * self assigning a variable.
 */
class SelfAssign extends VariableError {
    /**
     * The Error constructor.
     */
    SelfAssign(String variable) {
        super("Variable '" + variable + "'" +
                " might not have been initialized.");
    }
}




