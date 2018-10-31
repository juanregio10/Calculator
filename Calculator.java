package calculator;

import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author Juan Marin
 */
public class Calculator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner keyboard = new Scanner(System.in);
        System.out.println("Hello, I am your calculator. Please enter an infix expression: ");

        boolean isValid = false;
        while (!isValid) {
            //input read without spaces
            String input = keyboard.nextLine().replaceAll("\\s+", "");
            isValid = isInputValid(input);
            if (isValid) {
                evaluation(input);
            } else {
                System.out.println("Invalid input, please try again");
            }
        }
    }

    //function uses shunting yard algorithm to convert infix to postfix expression
    //and evaluates
    public static int evaluation(String input) {
        //string expression stored char array
        char[] tokens = input.toCharArray();
        Stack<Double> result = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            //if first char is '-' change its symbol
            if (tokens[0] == '-') {
                tokens[0] = '#';
            }
            //if tokens are digits
            if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.') {
                StringBuilder num = new StringBuilder();

                //while loop apends tokens if a number has more than one digit
                while ((i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9')
                        || (i < tokens.length && tokens[i] == '.')) {
                    num.append(tokens[i++]);
                }
                //push number into result stack
                result.push(Double.parseDouble(num.toString()));

            }
            //If statement to prevent empty stack error
            if (i < tokens.length) {
                //check for opening parenthesis and push into stack
                if (tokens[i] == '(') {
                    operators.push(tokens[i]);
                    //check for closing parenthesis and push into stack                   
                } else if (tokens[i] == ')') {
                    while (operators.peek() != '(') {
                        //result.push(operation(operators.pop(), result.pop(), result.pop()));
                        if (operators.peek() == '#') {
                            operators.pop();
                            result.push(operation(operators.pop(), result.pop() * (-1), result.pop()));
                        } else {
                            result.push(operation(operators.pop(), result.pop(), result.pop()));
                        }

                    }
                    operators.pop();

                    //if token is an operator
                } else if (tokens[i] == '*' || tokens[i] == '/' || tokens[i] == '+'
                        || tokens[i] == '-' || tokens[i] == '^' || tokens[i] == '#') {

                    //if a '-' is unary
                    if (tokens[i] == '-' && (tokens[i - 1] == '*' || tokens[i - 1] == '/' || tokens[i - 1] == '+'
                            || tokens[i - 1] == '-' || tokens[i - 1] == '^' || tokens[i - 1] == '(' || tokens[i - 1] == ')')) {

                        tokens[i] = '#';
                    }
                    //calculating result
                    while (!operators.empty() && hasHighPrecedence(tokens[i], operators.peek())) {
                        if (operators.peek() == '#') {
                            operators.pop();
                            result.push(operation(operators.pop(), result.pop() * (-1), result.pop()));
                        } else {
                            result.push(operation(operators.pop(), result.pop(), result.pop()));
                        }
                    }
                    operators.push(tokens[i]);
                }
            }
        }
        //calculating result
        while (!operators.empty()) {
            if (operators.peek() == '#') {
                operators.pop();
                result.push(operation(operators.pop(), result.pop() * (-1), result.pop()));
            } else {
                result.push(operation(operators.pop(), result.pop(), result.pop()));
            }
        }
        System.out.println(result.pop());
        return 0;
    }

    //Does operations of operators and operands poped from stacks
    public static double operation(char op, double num2, double num1) {
        switch (op) {
            case '#':
                return (-1) * num2;
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                if (num2 != 0) {
                    return num1 / num2;
                } else {
                    throw new IllegalArgumentException("Error: Can't divide by zero!");
                }
            case '^':
                return Math.pow(num1, num2);
        }
        return 0;
    }

    //checks precedence of operators
    public static boolean hasHighPrecedence(char operator1, char operator2) {
        if (operator2 == '(' || operator2 == ')') {
            return false;
        } else if ((operator2 == '+' || operator2 == '-') && (operator1 == '/' || operator1 == '*' || operator1 == '^')) {
            return false;
        } //if operator1 is # then it has higher precedence and must be poped 
        else if (operator1 == '#') {
            return false;
        } else {
            return true;
        }
    }
    //This function checks for all possible errors that can cause program to crash
    public static boolean isInputValid(String expression) {

        if(expression.isEmpty() || expression == null) {
            return false;
        }
        String allowedOperators = "+*^/";
        allowedOperators.toCharArray();

        int parenCount = 0;

        //loop though the string array to check for other conditions
        for (int i = 0; i < expression.length(); i++) {

            //checks that input is not null or empty
            if (expression != null && !expression.isEmpty()) {

                //this checks if the first and last index contains any invalid operators including the period for floating point
                if ((i == 0 || i == expression.length() - 1) && (expression.charAt(i) == '+'
                        || expression.charAt(i) == '/' || expression.charAt(i) == '*'
                        || expression.charAt(i) == '^')) {
                    return false;

                    //checks if the first and last index contains wrong parenthesis   
                } else if ((i == 0 && expression.charAt(i) == ')')
                        || (i == expression.length() - 1 && expression.charAt(i) == '(')) {
                    return false;
                }

                //checks if the number of parenthesis is equal
                if (expression.charAt(i) == '(') {
                    parenCount += 1;
                } else if (expression.charAt(i) == ')') {
                    parenCount -= 1;
                }
                if (i == expression.length() - 1 && parenCount != 0) {
                    return false;
                }

                //if a period is the last character on the expression    
                if (i == expression.length() - 1 && expression.charAt(i) == '.') {
                    return false;
                }

                //if there is not a number to the right of the period
                if (expression.charAt(i) == '.') {
                    if (!(expression.charAt(i + 1) >= '0' && expression.charAt(i + 1) <= '9')) {
                        return false;
                    }
                }

                //if there are two operators next to each other unless it involves a minus sign
                for (int j = 0; j < allowedOperators.length(); j++) {
                    if (expression.charAt(i) == allowedOperators.charAt(j) && (expression.charAt(i + 1) == '*' || expression.charAt(i + 1) == '+'
                            || expression.charAt(i + 1) == '/' || expression.charAt(i + 1) == '^')) {
                        return false;
                    }
                }

                //checks if it does not contains digits
                if (!(expression.charAt(i) >= '0' && expression.charAt(i) <= '9')) {
                    //checks if it contains anything but the accepted operators including the period for floating points
                    if (expression.charAt(i) != '+' && expression.charAt(i) != '-' && expression.charAt(i) != '*'
                            && expression.charAt(i) != '^' && expression.charAt(i) != '/'
                            && expression.charAt(i) != '(' && expression.charAt(i) != ')' && expression.charAt(i) != '.') {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
