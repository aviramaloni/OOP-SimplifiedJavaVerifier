package oop.ex5.main;

/**
 * a class of Scope errors exceptions.
 */
public class ScopeError extends Exception {

    /**
     * super constructor for a Scope Error
     *
     * @param errorMessage The error message.
     */
    public ScopeError(String errorMessage) {
        super(errorMessage);
    }
}

/**
 * in case of a wrong use of the s-Java code syntax.
 */
class InvalidSyntax extends ScopeError {
    /**
     * The Error constructor.
     */
    public InvalidSyntax(String line) {
        super("'" + line + "' has a s-Java syntax problem (missing ';' or '{').");
    }
}

/**
 * in case of a wrong use of the s-Java code syntax.
 */
class InvalidCommand extends ScopeError {
    /**
     * The Error constructor.
     */
    public InvalidCommand(String line) {
        super("'" + line + "' is an invalid s-Java command.");
    }
}

/**
 * in case the user tries to create a method/if/while s-Java
 * scope, with a bad scope declaration structure.
 */
class InvalidScopeDeclaration extends ScopeError {
    /**
     * The Error constructor.
     */
    public InvalidScopeDeclaration() {
        super("Invalid s-Java scope declaration.");
    }
}

/**
 * in case a scope is missing a closing bracket.
 */
class BadBracketsStructure extends ScopeError {
    /**
     * The Error constructor.
     */
    public BadBracketsStructure(String scopeName) {
        super("Invalid brackets structure in scope " +
                "'" + scopeName + "'.");
    }
}

/**
 * in case of an invalid condition.
 */
class InvalidConditionException extends ScopeError {
    /**
     * The Error constructor.
     */
    public InvalidConditionException(String condition) {
        super("'" + condition + "' is an invalid If/While s-Java condition.");
    }
}

/**
 * in case of a missing condition.
 */
class MissingCondition extends ScopeError {
    /**
     * The Error constructor.
     */
    public MissingCondition() {
        super("The If/While s-Java condition is missing.");
    }
}

/**
 * in case of a missing condition.
 */
class EmptyCondition extends ScopeError {
    /**
     * The Error constructor.
     */
    public EmptyCondition() {
        super("There is an empty condition in the code.");
    }
}

/**
 * in case of a missing condition.
 */
class ConditionDeclarationNotFromMethod extends ScopeError {
    /**
     * The Error constructor.
     */
    public ConditionDeclarationNotFromMethod() {
        super("Condition Scope cannot be declared from the global Scope.");
    }
}