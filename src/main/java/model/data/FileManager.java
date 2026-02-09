package model.data;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.entities.*;

/**
 * Persistence Layer Manager.
 * Responsibilities: Low-level File I/O operations and data parsing.
 * SRP: This class only handles how data is written to and read from physical storage.
 */
public class FileManager {

    private static final String USER_FILE = "users.txt";
    private static final String PARKING_FILE = "parkings.txt";
    private static final String CUSTOMER_FILE = "customers.txt";

    public FileManager() {
        ensureFilesExist();
    }

    private void ensureFilesExist() {
        try {
            if (!Files.exists(Paths.get(USER_FILE))) Files.createFile(Paths.get(USER_FILE));
            if (!Files.exists(Paths.get(PARKING_FILE))) Files.createFile(Paths.get(PARKING_FILE));
            if (!Files.exists(Paths.get(CUSTOMER_FILE))) Files.createFile(Paths.get(CUSTOMER_FILE));
        } catch (IOException e) {
            System.err.println("Critical Error: Could not initialize data files: " + e.getMessage());
        }
    }

    // --- USER REPOSITORY ---

    public List<User> loadUsers() throws IOException {
        return Files.readAllLines(Paths.get(USER_FILE)).stream()
                .map(this::parseUserLine)
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }

    public void saveUsers(List<User> users) throws IOException {
        List<String> lines = users.stream()
                .map(u -> u.getUsername() + "," + u.getPassword() + "," + u.getRole())
                .collect(Collectors.toList());
        Files.write(Paths.get(USER_FILE), lines, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private User parseUserLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 3) return null;

        String username = parts[0].trim();
        String password = parts[1].trim();
        String role = parts[2].trim().toUpperCase();

        return role.equals("ADMIN") ? new Admin(username, password) : new Clerk(username, password);
    }

    // --- PARKING REPOSITORY ---

    public List<String> readAllParkingLines() throws IOException {
        return Files.readAllLines(Paths.get(PARKING_FILE));
    }

    public void saveParkingData(List<String> lines) throws IOException {
        Files.write(Paths.get(PARKING_FILE), lines, 
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // --- CUSTOMER REPOSITORY ---

    public List<String> readAllCustomerLines() throws IOException {
        return Files.readAllLines(Paths.get(CUSTOMER_FILE));
    }

    public void appendCustomer(String data) throws IOException {
        Files.write(Paths.get(CUSTOMER_FILE), (data + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    
    /**
     * Helper to read raw lines from any file within the model.data context.
     */
    public List<String> readRawLines(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        return Files.exists(path) ? Files.readAllLines(path) : new ArrayList<>();
    }
    
    public void saveCustomer(String id, String name, boolean isPreferential) throws IOException {
    String data = id + "," + name + "," + isPreferential;
    appendCustomer(data);
}

/**
 * Loads all customers as a list of String arrays for table compatibility.
 */
public List<String[]> loadCustomersRaw() throws IOException {
    return readAllCustomerLines().stream()
            .map(line -> line.split(","))
            .collect(java.util.stream.Collectors.toList());
}

/**
 * Overwrites the customer file with updated data (used for delete/update).
 */
public void overwriteCustomers(List<String[]> data) throws IOException {
    List<String> lines = data.stream()
            .map(row -> String.join(",", row))
            .collect(java.util.stream.Collectors.toList());
    Files.write(Paths.get("customers.txt"), lines, 
                java.nio.file.StandardOpenOption.CREATE, 
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
}

public List<String> readAllUserLines() {
    List<String> lines = new ArrayList<>();
    // Ajusta la ruta si tu archivo tiene otro nombre
    File file = new File("users.txt"); 
    
    if (!file.exists()) return lines;

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
    } catch (IOException e) {
        System.err.println("Error reading users file: " + e.getMessage());
    }
    return lines;
}
}