package oop.ex5.main;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a single method scope.
 */
public class Method extends Scope {

    /**
     * Long regex expressions.
     */
    public final static String REGEX_METHOD =  "^\\s*void(\\s*)(\\w+)\\s*(\\(.*\\))\\s*",
    REGEX_RETURN = "\\s*return\\s*;\\s*";

    /**
     * Short regex expressions.
     */
    public final static String NAME = "name", ARGUMENTS = "arguments";

    /**
     * A HashMap holding all the existing Methods.
     */
    public static HashMap<String, Method> allMethods = new HashMap<>();

    /**
     * A String which holds the first line of the method (the method's declaration line).
     */
    private final String declaration;

    /**
     * The Method Class constructor.
     *
     * @param scopeData  the scope's code lines.
     * @param outerScope a scope instance from which this constructor was called.
     * @param name The Method's name.
     * @throws ScopeError    If there is Scope error.
     * @throws MethodError   If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    public Method(List<String> scopeData, Scope outerScope, String name)
            throws ScopeError, MethodError, VariableError {
        super(scopeData, outerScope, name);
        this.declaration = this.rawData.get(0);
        // in order to avoid an infinite loop while scanning the scope's
        // data, we have to remove it's first line (the declaration of the scope)
        this.rawData.remove(0);
        checkNameValidity();
        processArguments();
        if (!this.rawData.isEmpty()) scan();
        if (allMethods.containsKey(this.name)) {
            throw new BadMethodNameAlreadyExists(this.name);
        }
        allMethods.put(name, this);
        checkReturnAtEnd();
    }

    /**
     * This method returns 2 kinds of information about the method (corresponding
     * to a given String): the method's name, or the method's arguments - both
     * in a String form.
     *
     * @param kind The kind of information needed
     * @return A String which holds the method's name or the method's arguments.
     */
    private String getInfo(String kind) {
        Pattern pattern = Pattern.compile(REGEX_METHOD);
        Matcher matcher = pattern.matcher(this.declaration.substring(ZERO,
                this.declaration.length() - ONE).trim());
        if (matcher.find()){
            switch (kind) {
                case NAME:
                    return matcher.group(TWO);
                case ARGUMENTS:
                    return matcher.group(THREE);
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Decomposes the arguments String into individual arguments.
     *
     * @throws VariableError If there is Variable error.
     */
    private void processArguments() throws VariableError, BadArgumentsNum {
        String arguments = getInfo(ARGUMENTS);
        arguments = arguments.substring(ONE, arguments.length() - ONE).trim();
        String[] splitted = arguments.split(REGEX_COMMA);
        for (String argument : splitted) {
            if (argument.isEmpty() && splitted.length != ONE) throw new BadArgumentsNum(this.name);
            else if (argument.isEmpty()) return;
            Variable variable = new Variable(argument.trim(), true, this);
            this.arguments.put(variable.getName(), variable);
        }
    }

    /**
     * Checks if the name of the current method is an s-Java valid method name.
     *
     * @throws MethodError If there is Method error.
     */
    private void checkNameValidity() throws MethodError {
        String name = getInfo(NAME);
        if (Pattern.compile(Variable.REGEX_DIGIT).matcher(name).find()) {
            throw new BadMethodNameDigit(this.name);
        } else if (Pattern.compile(Variable.REGEX_STARTS_UNDERSCORE).matcher(name).find()) {
            throw new BadMethodNameUnderscore(this.name);
        } else if (Pattern.compile(Variable.REGEX_DIGIT_WORDS).matcher(name).find()) {
            throw new BadMethodNameIllegal(this.name);
        } else if (Pattern.compile(Variable.SAVED_WORDS)
                .matcher(name).find()) {
            throw new BadMethodNameSavedKeyword(name);
        }
    }

    /**
     * Checks if there is return statement and the end of the method.
     *
     * @throws MissingReturnStatement If there is a missing return statement.
     */
    private void checkReturnAtEnd() throws MissingReturnStatement {
        String lastLine = this.rawData.get(rawData.size() - ONE);
        Pattern pattern = Pattern.compile(REGEX_RETURN);
        Matcher matcher = pattern.matcher(lastLine);
        if (!matcher.find()) {
            throw new MissingReturnStatement(this);
        }
    }

}
