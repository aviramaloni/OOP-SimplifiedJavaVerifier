package oop.ex5.main;

/**
 * a class of Method errors exceptions.
 */
public class MethodError extends Exception {

    /**
     * super constructor for a Method Error
     *
     * @param errorMessage The error message.
     */
    public MethodError(String errorMessage) {
        super(errorMessage);
    }
}

/**
 * Bad Method Name error, starts with a digit.
 */
class BadMethodNameDigit extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodNameDigit(String name) {
        super("'" + name + "' is an invalid Method Name, can't start with a digit.");
    }
}

/**
 * Bad Method Name error, underscore.
 */
class BadMethodNameUnderscore extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodNameUnderscore(String name) {
        super("'" + name + "' is an invalid Method Name, can't start with an underscore.");
    }
}

/**
 * Bad Method Name error, contains illegal characters .
 */
class BadMethodNameIllegal extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodNameIllegal(String name) {
        super("'" + name + "' is an invalid Method Name, contains illegal characters.");
    }
}

/**
 * Bad Method Name error, saved keyword.
 */
class BadMethodNameSavedKeyword extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodNameSavedKeyword(String name) {
        super("'" + name + "' is an invalid Method Name, This name is a saved keyword.");
    }
}

/**
 * Bad Method Name error, already exists.
 */
class BadMethodNameAlreadyExists extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodNameAlreadyExists(String name) {
        super("'Method '" + name +
                "' is already defined.");
    }
}

/**
 * Bad Method Type error.
 */
class BadMethodType extends MethodError {
    /**
     * The Error constructor.
     */
    public BadMethodType(String type) {
        super("" + type + " is not a valid Method type, Sjava supports only void methods.");
    }
}

/**
 * Method does not exist.
 */
class MethodDoesNotExist extends MethodError {
    /**
     * The Error constructor.
     */
    public MethodDoesNotExist(String name) {
        super("Cannot resolve symbol '" + name + "'.");
    }
}

/**
 * Method does not exist.
 */
class MissingReturnStatement extends MethodError {
    /**
     * The Error constructor.
     */
    public MissingReturnStatement(Method method) {
        super("Missing return statement in '" + method.getName() + "' method.");
    }
}

/**
 * when trying to call a method with a wrong number of arguments.
 */
class BadArgumentsNum extends MethodError {
    /**
     * The Error constructor.
     */
    BadArgumentsNum(String name) {
        super("Actual and formal argument lists of method '" +
                name + "' differ in length.");
    }
}

/**
 * Invalid Method creation, when trying to declare a new method inside a method.
 */
class InvalidMethodCreation extends MethodError {
    /**
     * The Error constructor.
     */
    InvalidMethodCreation(String name) {
        super("'" + name +
                "' can not be declared inside another Method.");
    }
}

/**
 * Invalid Method call, when trying to call a method from the global scope.
 */
class InvalidMethodCall extends MethodError {
    /**
     * The Error constructor.
     */
    InvalidMethodCall(String name) {
        super("'" + name +
                "' can not be called from the global scope.");
    }
}


