package utils;

import java.util.Scanner;

/**
 * Utility class for console UI operations and formatting
 */
public class ConsoleUI {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Prints welcome banner
     */
    public static void printWelcomeBanner() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  EMPLOYEE RESOURCE MANAGEMENT SYSTEM");
        System.out.println("=".repeat(60));
    }

    /**
     * Clears the console (works on most systems)
     */
    public static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback: print new lines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * Prints a formatted header
     */
    public static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }

    /**
     * Prints a section divider
     */
    public static void printDivider() {
        System.out.println("-".repeat(60));
    }

    /**
     * Prints a simple menu
     */
    public static void printMenu(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }

    /**
     * Prints a table header
     */
    public static void printTableHeader(String... columns) {
        StringBuilder sb = new StringBuilder();
        int colWidth = 60 / columns.length;

        for (String col : columns) {
            sb.append(String.format("%-" + colWidth + "s", col));
        }
        System.out.println(sb.toString());
        System.out.println("-".repeat(60));
    }

    /**
     * Prints a table row
     */
    public static void printTableRow(String... values) {
        StringBuilder sb = new StringBuilder();
        int colWidth = 60 / values.length;

        for (String val : values) {
            String truncated = val.length() > colWidth - 1 ? 
                val.substring(0, colWidth - 2) + ".." : val;
            sb.append(String.format("%-" + colWidth + "s", truncated));
        }
        System.out.println(sb.toString());
    }

    /**
     * Gets integer input with validation
     */
    public static int getIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (InputValidator.isValidInteger(input, min, max)) {
                return Integer.parseInt(input);
            }
            System.out.println("Invalid input. Please enter a number between " + min + " and " + max);
        }
    }

    /**
     * Gets string input with validation
     */
    public static String getStringInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (InputValidator.isValidString(input)) {
                return input;
            }
            System.out.println("Invalid input. Please enter a non-empty value.");
        }
    }

    /**
     * Gets email input with validation
     */
    public static String getEmailInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (InputValidator.isValidEmail(input)) {
                return input;
            }
            System.out.println("Invalid email format. Please try again.");
        }
    }

    /**
     * Gets name input with validation
     */
    public static String getNameInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (InputValidator.isValidName(input)) {
                return input;
            }
            System.out.println("Invalid name. Use letters, spaces, and hyphens only.");
        }
    }

    /**
     * Gets password input
     */
    public static String getPasswordInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (InputValidator.isValidPassword(input)) {
                return input;
            }
            System.out.println("Password must be at least 6 characters with letters and numbers.");
        }
    }

    /**
     * Gets date input with validation (dd-MM-yyyy)
     */
    public static String getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (InputValidator.isValidDate(input)) {
                return input;
            }
            System.out.println("Invalid date format. Use dd-MM-yyyy");
        }
    }

    /**
     * Gets yes/no input
     */
    public static boolean getYesNoInput(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("Invalid input. Please enter 'y' or 'n'.");
        }
    }

    /**
     * Pauses execution until user presses enter
     */
    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Prints success message
     */
    public static void printSuccess(String message) {
        System.out.println("\n✓ " + message);
    }

    /**
     * Prints error message
     */
    public static void printError(String message) {
        System.out.println("\n✗ " + message);
    }

    /**
     * Prints info message
     */
    public static void printInfo(String message) {
        System.out.println("\nℹ " + message);
    }

    /**
     * Gets scanner instance for custom input
     */
    public static Scanner getScanner() {
        return scanner;
    }
}
