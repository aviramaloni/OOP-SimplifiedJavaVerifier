package oop.ex5.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This class represents a scope in the s-Java program. each instance
 * of this class is an individual scope (starts with a valid s-Java
 * code line with an opening bracket at its end, followed by a
 * single closing bracket). In this program, an instance of this class
 * may be one of the three: a general scope, a method defined scope or a
 * condition (if/while) defined scope.
 */
public class Scope {

    /**
     * The value used in the regex group operation.
     */
    protected final static int ZERO = 0, ONE = 1, TWO = 2, THREE = 3;

    /**
     * The Global Scope name.
     */
    private final static String GLOBAL_SCOPE_NAME = "Global Scope";

    /**
     * Short regex expressions.
     */
    public final static String REGEX_SEMICOLON = ";", REGEX_OPEN_BRACKET = "{",
            REGEX_CLOSED_BRACKET = "}", REGEX_COMMENT = "//", REGEX_COMMA = ",",
            REGEX_SINGLE_SPACE = " ", REGEX_OR = "||", REGEX_AND = "&&", REGEX_EMPTY = "";

    /**
     * Long regex expressions.
     */
    public final static String REGEX_CONDITION = "^\\s*(if|while)\\s*\\(.*\\)\\s*",
            REGEX_METHOD = "^\\s*(\\w+)(\\s+)(\\w+)\\s*\\(.*\\)\\s*",
            REGEX_POSSIBLE_METHOD = "^\\s*([a-zA-Z0-9_]+)\\s*(\\(.*\\))\\s*$",
            REGEX_POSSIBLE_ASSIGN = "^(\\S+)\\s*=\\s*(\\S+)$",
            REGEX_RETURN = "^\\s*return\\s*;\\s*$";

    /**
     * Scope types names.
     */
    private final static String TYPE_IF_OR_WHILE = "ifWhile", TYPE_METHOD = "method";

    /**
     * Regex method return type.
     */
    private final static String METHOD_TYPE_VOID = "void";


    /**
     * A static variable which hold a reference to the program's global scope.
     */
    public static Scope globalScope;

    /**
     * A HashMap of all the scope variables.
     */
    protected HashMap<String, Variable> variables = new HashMap<>();

    /**
     * A HashMap of all the scope arguments (used by the method).
     */
    protected LinkedHashMap<String, Variable> arguments = new LinkedHashMap<>();

    /**
     * The scope's data. Each line is preserved in a single list cell.
     */
    protected List<String> rawData;

    /**
     * A list of the scope's inner scopes.
     */
    protected List<Scope> innerScopes = new ArrayList<>();

    /**
     * The outer scope of this scope (if this scope is the global scope it will be null).
     */
    protected Scope outerScope;

    /**
     * The scope length (number of lines).
     */
    protected int length;

    /**
     * The scope name.
     */
    protected String name;


    /**
     * The Scope Class constructor.
     *
     * @param scopeData  The scope code lines.
     * @param outerScope The scope which wraps this scope.
     * @param name       The scope name.
     */
    public Scope(List<String> scopeData, Scope outerScope, String name) {
        this.name = name;
        if (this.name.equals(GLOBAL_SCOPE_NAME)) Scope.globalScope = this;
        this.length = scopeData.size();
        this.rawData = scopeData;
        this.outerScope = outerScope;
    }

    /**
     * The scope's data decoder.
     * This method iterates over the scope's data (each iteration scans an
     * individual code line). according to the exercise's instructions, and the
     * s-Java coding specifications, this method matched each line to it's
     * appropriate cipher.
     *
     * @throws ScopeError    If there is Scope error.
     * @throws MethodError   If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    protected void scan() throws ScopeError, MethodError, VariableError {
        int maxLineNum = this.rawData.size();
        String line;
        for (int lineNum = 0; lineNum < maxLineNum; lineNum++) {
            line = this.rawData.get(lineNum);
            // in case the current is a comment line or an empty line.
            if (line.startsWith(REGEX_COMMENT) || line.trim().isEmpty()) {
            }
            // in case of a declaration or assignment
            else if (line.trim().endsWith(REGEX_SEMICOLON)) singleLineCommand(line);
                // in case of a new scope creation
            else if (line.trim().endsWith(REGEX_OPEN_BRACKET)) lineNum += scopeCreation(line, lineNum) - ONE;
                // in case of invalid line syntax
            else {
                throw new InvalidSyntax(line);
            }
        }
    }

    /**
     * This method helps to determine whether the scanned line creates a new
     * method scope or a new if/while scope.
     *
     * @param line    the scope's declaration line.
     * @param lineNum the number of the first line in the new declared scope
     *                (with respect to the original scope line counter).
     * @return the new declared scope's size (the number of lines in it).
     * @throws ScopeError    If there is Scope error.
     * @throws MethodError   If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    private int scopeCreation(String line, int lineNum) throws ScopeError, MethodError, VariableError {
        Pattern pattern1 = Pattern.compile(REGEX_CONDITION);
        Pattern pattern2 = Pattern.compile(REGEX_METHOD);

        // without the "{"
        String substringLine = line.substring(ZERO, line.length() - ONE);

        Matcher matcher1 = pattern1.matcher(substringLine);
        Matcher matcher2 = pattern2.matcher(substringLine);

        // if/while statement
        if (matcher1.find()) {
            return scopeCreationAUX(lineNum, TYPE_IF_OR_WHILE, line);
        }
        // a method declaration statement
        else if (matcher2.find()) {
            if (matcher2.group(ONE).equals(METHOD_TYPE_VOID)) {
                if (this.outerScope != null) throw new InvalidMethodCreation(matcher2.group(THREE));
                else return scopeCreationAUX(lineNum, TYPE_METHOD, matcher2.group(THREE));
            } else throw new BadMethodType(matcher2.group(ONE));
        }
        // in case the line ends with "{" but no void/if/while with a valid s-java declaration
        else throw new InvalidScopeDeclaration();
    }

    /**
     * this method 'creates' a new Method or Scondition Class, corresponding
     * to the given type.
     *
     * @param lineNum the number of the first line in this new declared scope
     *                (with respect to the original scope line counter).
     * @param type    "method" or "ifWhile" - to determine the new Class's identity.
     * @param name    the scope's name.
     * @return the new scope's size (the number of lines in it).
     * @throws ScopeError    If there is Scope error.
     * @throws MethodError   If there is Method error.
     * @throws VariableError If there is Variable error.
     */
    private int scopeCreationAUX(int lineNum, String type, String name)
            throws ScopeError, MethodError, VariableError {
        int innerScopeSize = findInnerScopeSize(lineNum);
        List<String> innerScopeData = new ArrayList<>();
        // -1 for not including the closing bracket
        for (int i = lineNum; i < innerScopeSize + lineNum - 1; i++) {
            innerScopeData.add(this.rawData.get(i));
        }
        if (type.equals(TYPE_METHOD)) {
            Method method = new Method(innerScopeData, this, name);
            this.innerScopes.add(method);
        } else {
            if (callFromMethod()) {
                Scondition scondition = new Scondition(innerScopeData, this, name);
                this.innerScopes.add(scondition);
            } else throw new ConditionDeclarationNotFromMethod();
        }
        return innerScopeSize;
    }


    /**
     * Processes all the single line command:
     * 1. new Variable declarations.
     * 2. a Method call.
     * 3. a return statement.
     * 4. existing Variable assignments.
     *
     * @param line The single line command.
     * @throws VariableError     Possible when trying to declare or assign a variable.
     * @throws InvalidCommand    If there is an invalid command in one of the scope lines.
     * @throws InvalidMethodCall If a method is called not from the global scope.
     * @throws InvalidSyntax     In case of an invalid s-Java syntax.
     */
    public void singleLineCommand(String line)
            throws VariableError, InvalidCommand, InvalidMethodCall, InvalidSyntax {
        String trimmedLine = line.trim();
        trimmedLine = trimmedLine.substring(ZERO, trimmedLine.length() - ONE);
        // New Variable declarations
        if (possibleVariableDeclaration(trimmedLine)) declareNewVariables(trimmedLine);
            // A Method call
        else if (possibleMethodCall(line)) {
            if (!callFromMethod()) throw new InvalidMethodCall(line);
            MethodCallsChecker.addCall(line);
        }
        // A return statement
        else if (isReturnLine(line)) {
        }
        // A Variable assignments
        else assignExistingVariable(trimmedLine);
    }

    /**
     * Check if this scope or any of his outer scope is a method.
     *
     * @return True if this scope is a method or wrapped by one, false elsewhere.
     */
    protected boolean callFromMethod() {
        Scope outerScope = this;
        while (outerScope != null) {
            if (outerScope instanceof Method) return true;
            outerScope = outerScope.outerScope;
        }
        return false;
    }

    /**
     * Checks if the given line is a valid s-Java return statement line.
     *
     * @param line The line to be checked.
     * @return True in case the given line is a valid s-Java return statement inside a method,
     * false otherwise.
     */
    private boolean isReturnLine(String line) {
        Pattern pattern = Pattern.compile(REGEX_RETURN);
        Matcher matcher = pattern.matcher(line);
        return matcher.find() && callFromMethod();
    }

    /**
     * Checks if the method call is valid.
     *
     * @param line A String in which a method is to be called.
     * @return True in case the line holds a valid s-Java method call, false otherwise.
     */
    private boolean possibleMethodCall(String line) {
        Pattern pattern = Pattern.compile(REGEX_POSSIBLE_METHOD);
        Matcher matcher = pattern.matcher(line.substring(ZERO, line.length() - ONE)); // removes the '}'
        return matcher.find();
    }

    /**
     * Checks if the given line starts with one of the s-Java's reserved keywords,
     * which indicates a new variable declaration.
     *
     * @param line The line to be checked.
     * @return True in case the line decodes for a variable declaration, false otherwise.
     */
    private boolean possibleVariableDeclaration(String line) {
        return (line.trim().startsWith(Variable.VARIABLE_FINAL) ||
                line.trim().startsWith(Variable.VARIABLE_TYPE_INT) ||
                line.trim().startsWith(Variable.VARIABLE_TYPE_DOUBLE) ||
                line.trim().startsWith(Variable.VARIABLE_TYPE_STRING) ||
                line.trim().startsWith(Variable.VARIABLE_TYPE_BOOLEAN) ||
                line.trim().startsWith(Variable.VARIABLE_TYPE_CHAR));
    }

    /**
     * Tries to assign an existing argument or variable a new value.
     *
     * @param line The line to be checked.
     * @throws VariableError  If one of the possible assignments fails.
     * @throws InvalidCommand If there is an invalid command (not an assignment).
     */
    private void assignExistingVariable(String line) throws VariableError, InvalidCommand {
        Pattern pattern = Pattern.compile(REGEX_POSSIBLE_ASSIGN);
        String[] assignmentsStr = line.split(REGEX_COMMA);
        for (String possibleAssignment : assignmentsStr) {
            Matcher matcher = pattern.matcher(possibleAssignment);
            if (matcher.find()) {
                Scope curScope = this;
                while (curScope != null) {
                    if (curScope.arguments.containsKey(matcher.group(ONE))) {
                        curScope.arguments.get(matcher.group(ONE)).
                                setData(matcher.group(TWO), false, this);
                        return;
                    }
                    if (curScope.variables.containsKey(matcher.group(ONE))) {
                        curScope.variables.get(matcher.group(ONE)).
                                setData(matcher.group(TWO), false, this);
                        return;
                    }
                    curScope = curScope.outerScope;
                }
                if (callFromMethod()) {
                    GlobalVariablesChecker.addAssignment(possibleAssignment);
                } else throw new VariableDoesNotExist(matcher.group(ONE));
            } else throw new InvalidCommand(line);
        }

    }

    /**
     * Tries to create new variables.
     *
     * @param line The line to be checked.
     * @throws VariableError If one of the possible deceleration fails.
     */
    private void declareNewVariables(String line) throws VariableError, InvalidSyntax {
        String configStr = Variable.VARIABLE_INIT_CONFIG;
        String[] separatedWords = line.split(REGEX_SINGLE_SPACE);
        if (separatedWords.length >= TWO) {
            configStr += separatedWords[ZERO] + REGEX_SINGLE_SPACE;
            if (separatedWords[ZERO].equals(Variable.VARIABLE_FINAL)) configStr += separatedWords[ONE] +
                    REGEX_SINGLE_SPACE;
        }
        try {
            line = line.replaceFirst(configStr, Variable.VARIABLE_INIT_CONFIG);
        } catch (PatternSyntaxException e) {
            throw new BadVariableDeclaration(line, false);
        }
        if (line.endsWith(REGEX_COMMA)) throw new BadVariableDeclaration(line, false);
        String[] variablesStr = line.split(REGEX_COMMA);
        for (String variableStr : variablesStr) {
            if (variableStr.isEmpty()) throw new InvalidSyntax(line);
            Variable variable = new Variable(configStr +
                    variableStr.trim(), false, this);
            this.variables.put(variable.getName(), variable);
        }
    }

    /**
     * Finds the size of a scope (according to it's starting line number).
     * by iteration, it scans for a new closing bracket to appear, to signify the end
     * of the scope.
     *
     * @param lineNum the number of the first line in this new declared scope
     *                (with respect to the original scope line counter).
     * @return the size of the scope (the number of lines in it).
     * @throws BadBracketsStructure If there is a bad brackets structure.
     */
    private int findInnerScopeSize(int lineNum) throws BadBracketsStructure {
        int scopeSize = ONE;
        int openBracketsNum = ONE;
        int closedBracketsNum = ZERO;
        while (lineNum < this.rawData.size() && ((openBracketsNum - closedBracketsNum) != 0)) {
            lineNum++;
            try {
                if (this.rawData.get(lineNum).trim().endsWith(REGEX_OPEN_BRACKET)) openBracketsNum++;
                if (this.rawData.get(lineNum).trim().equals(REGEX_CLOSED_BRACKET)) closedBracketsNum++;
            } catch (IndexOutOfBoundsException e) {
                throw new BadBracketsStructure(this.name);
            }
            scopeSize++;
        }
        if ((openBracketsNum - closedBracketsNum) != ZERO) throw new BadBracketsStructure(this.name);
        return scopeSize;
    }

    /**
     * Getter for the scope name.
     *
     * @return This scope name.
     */
    public String getName() {
        return this.name;
    }
}

