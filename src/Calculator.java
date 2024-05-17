import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;


public class Calculator {

    private static String ExpressionToRPN(String expression) throws IllegalArgumentException {
        StringBuilder current = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        int priority;

        for (int i = 0; i < expression.length(); i++) {
            char token = expression.charAt(i);
            priority = getPriority(token);

            if (priority == 0) {
                current.append(token);
            } else if (priority == 1) {
                stack.push(token);
            } else if (priority > 1) {
                current.append(' ');
                while (!stack.empty() && getPriority(stack.peek()) >= priority) {
                    current.append(stack.pop()).append(' ');
                }
                stack.push(token);
            } else if (priority == -1) {
                current.append(' ');
                while (!stack.empty() && getPriority(stack.peek()) != 1) {
                    current.append(stack.pop()).append(' ');
                }
                if (stack.empty() || getPriority(stack.peek()) != 1) {
                    throw new IllegalArgumentException("Invalid expression: Mismatched parentheses");
                }
                stack.pop();
            }
        }

        while (!stack.empty()) {
            char top = stack.pop();
            if (top == '(' || top == ')') {
                throw new IllegalArgumentException("Invalid expression: Mismatched parentheses");
            }
            current.append(' ').append(top);
        }

        return current.toString().trim();
    }

    private static double RPNtoAnswer(String RPN) throws IllegalArgumentException {
        StringBuilder operand = new StringBuilder();
        Stack<Double> stack = new Stack<>();

        for (int i = 0; i < RPN.length(); i++) {
            if (RPN.charAt(i) == ' ') continue;

            if (getPriority(RPN.charAt(i)) == 0) {
                while (i < RPN.length() && RPN.charAt(i) != ' ' && getPriority(RPN.charAt(i)) == 0) {
                    operand.append(RPN.charAt(i++));
                }
                double value = Double.parseDouble(operand.toString());
                stack.push(value);
                operand = new StringBuilder();
            } else if (RPN.charAt(i) == 'j') {
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Invalid RPN expression: Not enough operands for operator j");
                }
                double a = stack.pop();
                double result = j_0(a);
                stack.push(result);
            } else if (RPN.charAt(i) == '&') {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid RPN expression: Not enough operands for operator &");
                }
                double b = stack.pop();
                double a = stack.pop();
                double result = and(a, b);
                stack.push(result);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid RPN expression: Too many operands");
        }

        return stack.pop();
    }

    private static double and(double x, double y) {
        return Math.min(x, y);
    }

    private static double j_0(double x) {
        return (x == 0) ? 1 : 0;
    }

    private static int getPriority(char token) {
        if (token == 'j') return 3;
        if (token == '&') return 2;
        if (token == '(') return 1;
        if (token == ')') return -1;
        if (Character.isDigit(token) || token == '.') return 0;
        return -2;
    }

    public static void printTruthTableTwoVariables(String example, int k) {
        System.out.println("x | y | f");
        System.out.println("----------------");
        for (int x = 0; x < k; x++) {
            for (int y = 0; y < k; y++) {
                String expr = example.replace('x', (char) (x + '0')).replace('y', (char) (y + '0'));
                try {
                    String rpn = ExpressionToRPN(expr);
                    double result = RPNtoAnswer(rpn);
                    System.out.printf("%d | %d | %-6.0f%n", x, y, result);
                } catch (IllegalArgumentException e) {
                    System.out.printf("%d | %d | Error: %s%n", x, y, e.getMessage());
                }
                System.out.println("----------------");
            }
        }
    }

    public static void printTruthTableOneVariable(String example, int k) {
        System.out.println("x | f");
        System.out.println("----------------");
        for (int x = 0; x < k; x++) {
            String expr = example.replace('x', (char) (x + '0'));
            try {
                String rpn = ExpressionToRPN(expr);
                double result = RPNtoAnswer(rpn);
                System.out.printf("%d | %-6.0f%n", x, result);
            } catch (IllegalArgumentException e) {
                System.out.printf("%d | Error: %s%n", x, e.getMessage());
            }
            System.out.println("----------------");
        }
    }

    public static void printSDNF(String example, int k, int n) {
        StringBuilder sdnf = new StringBuilder();
        boolean firstTerm = true;

        if (n == 1) {
            for (int x = 0; x < k; x++) {
                String expr = example.replace('x', (char) (x + '0'));
                String rpn = ExpressionToRPN(expr);
                double result = RPNtoAnswer(rpn);
                if (result != 0) {
                    if (!firstTerm) {
                        sdnf.append(" v ");
                    }
                    if (result != k - 1) {
                        sdnf.append((char) (result + '0'));
                        sdnf.append(" & ");
                    }
                    sdnf.append("(");
                    sdnf.append("J_").append(x).append("(x)");
                    sdnf.append(")");
                    firstTerm = false;
                }
            }
        } else if (n == 2) {
            for (int x = 0; x < k; x++) {
                for (int y = 0; y < k; y++) {
                    String expr = example.replace('x', (char) (x + '0')).replace('y', (char) (y + '0'));
                    String rpn = ExpressionToRPN(expr);
                    double result = RPNtoAnswer(rpn);
                    if (result != 0) {
                        if (!firstTerm) {
                            sdnf.append(" v ");
                        }
                        if (result != k - 1) {
                            sdnf.append((char) (result + '0'));
                            sdnf.append(" & ");
                        }
                        if (x == 0) {
                            sdnf.append("(J_").append(x).append("(x)").append(" & ");
                        } else {
                            sdnf.append("(J_").append(x).append("(x)").append(" & ");
                        }
                        if (y == 0) {
                            sdnf.append("J_").append(y).append("(y))");
                        } else {
                            sdnf.append("J_").append(y).append("(y))");
                        }
                        firstTerm = false;
                    }
                }
            }
        }

        if (sdnf.isEmpty()) {
            System.out.println("SDNF: false");
        } else {
            System.out.println("SDNF: " + sdnf);
        }
    }

    public static void checkT(String example, int k, int n) {
        int[] results;

        if (n == 1) {
            results = new int[k];
            for (int x = 0; x < k; x++) {
                String expr = example.replace('x', (char) (x + '0'));
                String rpn = ExpressionToRPN(expr);
                results[x] = (int) RPNtoAnswer(rpn);
            }
        } else if (n == 2) {
            results = new int[k * k];
            int counter = 0;
            for (int x = 0; x < k; x++) {
                for (int y = 0; y < k; y++) {
                    String expr = example.replace('x', (char) (x + '0')).replace('y', (char) (y + '0'));
                    String rpn = ExpressionToRPN(expr);
                    results[counter++] = (int) RPNtoAnswer(rpn);
                }
            }
        } else {
            System.out.println("Error: Unsupported value of n");
            return;
        }

        Scanner scanner = new Scanner(System.in);


        System.out.print("Enter the set E separated by a space: ");
        String[] inputArray = scanner.nextLine().split("\\s+");


        int[] E = new int[inputArray.length];


        for (int i = 0; i < inputArray.length; i++) {
            try {
                E[i] = Integer.parseInt(inputArray[i]);
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format \"" + inputArray[i] + "\"");
                return;
            }
        }

        for (int i = 0; i < inputArray.length; i++) {
            if (E[i] > k - 1 || E[i] < 0) {
                System.out.println("Error: Invalid number \"" + inputArray[i] + "\"");
                return;
            }
        }

        boolean flag = true;
        if (n == 1) {
            for (int i = 0; i < k; i++) {
                if (contains(E, i) && !contains(results, i)) {
                    flag = false;
                    break;
                }
            }
        } else {
            int it = 0;
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < k; j++) {
                    if (contains(E, i) && contains(E, j)) {
                        assert false;
                        if (!contains(results, it)) {
                            flag = false;
                            break;
                        }
                    }
                    it++;
                }
            }
        }


        if (flag) {
            System.out.println("The function saves the set " + Arrays.toString(E));
        } else {
            System.out.println("The function not saves the set " + Arrays.toString(E));
        }
    }

    public static boolean contains(int[] array, int target) {
        for (int element : array) {
            if (element == target) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the value of k: ");
        int k = scanner.nextInt();

        System.out.print("Enter the number of variables (1 or 2): ");
        int n = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter the expression: ");
        String example = scanner.nextLine();

        try {
            if (n == 1) {
                printTruthTableOneVariable(example, k);
            } else if (n == 2) {
                printTruthTableTwoVariables(example, k);
            } else {
                System.out.println("Error: Number of variables must be 1 or 2.");
            }

            printSDNF(example, k, n);
            checkT(example, k, n);
        } catch (IllegalArgumentException e) {
            System.out.println("Error in expression: " + e.getMessage());
        }

        scanner.close();
    }
}
