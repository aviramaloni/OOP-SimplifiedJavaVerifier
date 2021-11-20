 # Simplified Java Verifier


![Jenkins](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fwso2.org%2Fjenkins%2Fview%2FAll%2520Builds%2Fjob%2Farchetypes)

This program was designed and created by Aviram Aloni and Orr Matzkin, as part of 
The Hebrew University of Jerusalem's *'Introduction to Object-Oriented Programming'* course.

> s-Java is a simplified version of Java. All its features are described below.

## Table of Contents
- About
  - [Project's Essence](#projects-essence)
  - [Goals](#goals)
  - [Requirements](#requirements)

- s-Java specifications
  - [General Description](#general-description)
  - [Variables](#variables)
    - [Variable declaration](#variable-declaration)
    - [Assigning values to variables](#assigning-values-to-variables)
    - [Referring to variables](#referring-to-variables)
  - [Methods](#methods)
  - [if and while Blocks](#if-and-while-blocks)

- How to use
  - [Installation](#installation)
  - [Running Through Command Line](#running-through-command-line)
  - [Running Through IntelliJ IDEA](#running-through-intellij-idea)


## About
### Project's Essence
This program implements a *s-Java* verifier - a tool able to verify the validity of s-Java
code.
It knows how to read s-Java code and determine its validity, but not to translate it to bytecode.
s-Java is a programing language, which only supports a very limited set of Java features (s stands for simplified).

### Goals
- Implementing with the concepts of Regular Expressions.
- Designing & implementing a complex system.
- Working with the Exceptions mechanism.

### Requirements

![PowerShell Gallery](https://img.shields.io/powershellgallery/p/DNS.1.1.1.1)

- openjdk-16 version 16.0.1+

## s-Java specifications
### General Description
- An s-Java file does not interact with other files. Each file is stand-alone.
- Each s-Java file is composed of two kinds of components:
  - *Global variables*, or *members*, are variables shared between methods.
  - *Methods* are general functions. Each method is composed of a list of code lines: defining
      local variables, giving new values to variables (local or global), calling methods, defining
      *if/while* blocks and returning.
- s-Java has no classes. As such, it is appropriate to think of s-Java members/global variables
  as comparable to Java static members, and of s-Java methods as comparable to Java static
  methods.
- Each s-Java *line* is either:
  – An empty line, containing only white spaces (spaces, tabs, carriage return, etc. as \s
    is defined to include).
  – A comment line, in which the first characters are //. No characters, including white
    spaces, may appear before the //, and any number and kind of characters may appear
    after the //.
- A code line, which must end with one of the following suffixes:
    - ‘;’ - for defining variables, changing variable values, calling methods and returning.
    - ‘{’ - for opening method declarations or if/while blocks.
    - ‘}’ - for closing ‘{’ blocks (has to be in its own line).
- White spaces may appear before and after any of these suffixes.
- Other comment styles like multi-line comments (/* . . . */)*, JavaDoc comments (/** . . . */)*
  and single-line comments appearing in the middle of a line are not supported by s-Java.
- Operators are not supported (`int a = 3 - 5;`, `String b = "OO" + "P";` are illegal).
- Similarly, arrays are not supported in s-Java .

### Variables
s-Java comes with a strict set of variable types. It does not support the creation of new types
(e.g., classes, interfaces, enums). The language supports the definition of two kinds of variables:
global variables and local variables. 

Both types of variables are defined in the same way as in java:

`type name = value;`

- *types* shows the legal s-Java types.
- *name* is any sequence (length > 0) of letters (uppercase or lowercase), digits and the underscore character (_). 
**name may not start with a digit. name may start with an underscore,
but in such a case it must contain at least one more character (i.e., ’ ’ is not a legal name).**
- *value* can be one of the following:
  - a legal value for type.
  - another existing and initialized variable of the same type.

#### Variable declaration
- A variable may be declared with or without an initialization. 
  That is, both `int a;` and `int b2 = 5;` are legal s-Java lines.
- A variable declaration must be encapsulated in a single line.

| Type | Description | Value Format |
| --- | --- | --- |
| int | an integer number | a number (positive, 0 or negative) |
| double | a real number | an integer or a floating-point number (positive, 0 or negative) |
| String | a string of characters | a string of characters |
| boolean | a boolean variable | **true**, **false** or any number (int or double) |
| char | a single character | any character (inside single quotation marks) |


- Multiple variables of the same type may be declared in a single line, separated by a comma.
For example, the following lines are legal:
```
double a, b;
int i1, i2 = 6;
char c='Z', f;
boolean a, b ,c , d = true, e, f = 5;
String a = "hello" , b = "goodbye";
```
- There may not be two global variables with the same name (regardless of their types). For
example, in the following, given the first line, the second line is illegal:
```
int a = 5;
String a = "hello";
```
However, a local variable can be defined with the same name as a global one (regardless
of their types). That is, in the previous example, had the first line been a declaration of
a global, the second would have been legal if it was a declaration of a local variable. This is true even if the global variable of the same name was initialized
inside the method where the local variable is later declared.
- Methods parameters are considered local variables of the method they belong to.
- Two local variables with the same name (regardless of their type) cannot be defined inside
the same block. This also applies to variables with the same name
as a method parameter.
- However, two local variable with the same name can be defined inside different blocks, even
if one is nested in the other.
```
void foo(int param1) {
  int a = 5;
  if (param1) {
    int a = 20;
    boolean param1 = true;
    while (param1) {
      double a = 2.5;
    }
  }
return;
}
```
In case of multiple variables with the same name, a reference to that name refers to the
variable in the most specific scope.
- A variable may have the same name as a method.

#### Assigning values to variables
- Any *variable* may be declared using the modifier final, which makes it a constant. Such
variables must be initialized with some value at declaration time: `final int a = 5;`. The
modifier should appear before the type of the variable, and not after: `int final a = 5;`.
- A final *variable* may not be assigned a value in subsequent lines (i.e., lines other than it’s
declaration line).
- Other java modifiers (e.g., static, public/private) are not allowed (they are not part of
the language specification).
- A (non final) *variable* can be assigned with a value after it is created:
```
int a = 521;
...
a = 832;
```
This applies both to global and local variables. Local variables can only be assigned inside
the method they were declared in (including any scopes nested in it). Global variables may
be assigned multiple times both inside and outside a method.
- A *local variable* cannot be used in any way (in an assignment, in a call to a method) before
it is assigned a value. In other words, if a local variable is not initialized in
its declaration, the code is **only legal if**:
  - The next line in which it appears is an assignment of a value to that variable.
  - It is never used.
- A *variable* a (global or local) can be assigned with another (global or local) variable b of
the same type. Additionally, a double can also be assigned with an int, and a boolean
can also be assigned with an int and a double.
For example:
```
int a = 5;
double b = a;
```

#### Referring to variables
- Unlike a local variable, a global variable can be referred to by methods appearing before the
line in which it was assigned a value, or even declared. Other global
variables, may only be assigned with a global variable if it was already declared and assigned.
- In a case of an un-initialized global variable (meaning it is not assigned a value anywhere
outside a method), all methods may refer to it (regardless of their location in relation to its
declaration), but every method using it (in an assignment, as an argument to a method call)
must first assign a value to the global variable itself (even if it was assigned a value in some
other method).
- Accessing variables declared in a more inner scope is illegal.

### Methods
s-Java methods are a simple version of Java’s methods definition:

`void method name ( type1 parameter1, type2 parameter2, . . . , typen parametern) {`

where:
- method name is defined similarly to variable names (i.e., a sequence of length > 0, containing
letters (uppercase or lowercase), digits and underscore), with the exception that method
names must start with a letter (i.e., they may not start with a digit or an underscore).
- parameters is a comma-separated list of parameters. Each parameter is a pair of a valid type
and a valid variable name, without a value.
- Only void methods are supported.
- After the method declaration, comes the method’s code. It may contain the following lines:
  - Local variable declaration lines (as defined in variables)
  - Variable assignment lines (as defined in variables).
  - A call to another existing method. Any method foo() may be called, regardless of its
location in the file (i.e., before or after the definition of the current method). The syntax of
calling is the java syntax:
`method name (param1, param2, . . . , paramn);`
where method name is an existing method and param1, param2, . . . , paramn are variables
or constants (3, "hello") of types agreeing with the method definition (though a double
parameter can accept an int argument and a boolean can int and double). Calling a
method with an incompatible number of arguments, wrong types or uninitialized variables
is illegal.
- An if/while block.
- A return statement. Return statements may appear anywhere inside a method, but must
also appear as the last line in the method’s code; this is different from Java where a method can
also simply end without a return statement. The return statement should not return any
value, in accordance with the method’s declaration. The format is:
`return;`

### if and while Blocks
if and while blocks may only appear in methods, and are defined in the following way:
```
if (condition) {
  ...
}
while (condition) {
  ...
}
```
where condition is a boolean value, so it is either:
- One of the reserved words true or false.
- An initialized boolean, double or int variable.
- A double or int constant/value (e.g. 5, -3, -21.5).

Also, multiple conditions separated by AND/OR may appear (e.g., `if ( a || b || c) {`)



## How to use
### Installation

1. Clone this repository into a desired folder on your computer
```
git clone https://github.com/aviramaloni/OOP-SimplifiedJavaVerifier.git
```

### Running Through Command Line
1. Go to the cloned project's directory
2. run the *Sjavac.java* file
```
java Sjavac source file name
```
where *source file name* contains your s-Java code lines

### Running Through IntelliJ IDEA
1. Open a new IDEA project with all cloned files
2. Run the *Sjavac.java* file by pushing the green **Run** button

