package utils;

import java.io.*;
import java.util.*;

/**
 * Utility class for CSV file operations with safety checks
 */
public class CSVManager {

    /**
     * Reads CSV file and returns list of string arrays
     */
    public static List<String[]> readCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        File file = new File(filePath);

        // Create file if it doesn't exist
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
                return data;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    data.add(parseCSVLine(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }

        return data;
    }

    /**
     * Writes list of string arrays to CSV file
     */
    public static boolean writeCSV(String filePath, List<String[]> data) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String[] row : data) {
                    writer.write(escapeAndJoinCSV(row));
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error writing CSV file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Parses a CSV line handling commas within quotes
     */
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        result.add(current.toString().trim());
        return result.toArray(new String[0]);
    }

    /**
     * Escapes quotes and joins array into CSV line
     */
    private static String escapeAndJoinCSV(String[] row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            if (i > 0) sb.append(",");
            
            String value = row[i] != null ? row[i] : "";
            
            // Escape quotes and wrap in quotes if contains comma
            if (value.contains(",") || value.contains("\"")) {
                value = "\"" + value.replace("\"", "\"\"") + "\"";
            }
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * Finds row by column value
     */
    public static String[] findRow(List<String[]> data, int columnIndex, String value) {
        if (data.isEmpty()) return null;

        for (String[] row : data) {
            if (columnIndex < row.length && row[columnIndex].equalsIgnoreCase(value)) {
                return row;
            }
        }
        return null;
    }

    /**
     * Updates row by ID
     */
    public static boolean updateRow(List<String[]> data, int idColumnIndex, String id, String[] newRow) {
        for (int i = 0; i < data.size(); i++) {
            if (idColumnIndex < data.get(i).length && data.get(i)[idColumnIndex].equals(id)) {
                data.set(i, newRow);
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes row by ID
     */
    public static boolean deleteRow(List<String[]> data, int idColumnIndex, String id) {
        return data.removeIf(row -> idColumnIndex < row.length && row[idColumnIndex].equals(id));
    }

    /**
     * Gets all values from specific column
     */
    public static List<String> getColumnValues(List<String[]> data, int columnIndex) {
        List<String> values = new ArrayList<>();
        for (String[] row : data) {
            if (columnIndex < row.length && !row[columnIndex].isEmpty()) {
                values.add(row[columnIndex]);
            }
        }
        return values;
    }

    /**
     * Checks if value exists in column
     */
    public static boolean valueExists(List<String[]> data, int columnIndex, String value) {
        for (String[] row : data) {
            if (columnIndex < row.length && row[columnIndex].equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
