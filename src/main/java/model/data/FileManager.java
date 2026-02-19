package model.data;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.entities.*;

public class FileManager {

    private static final String USER_FILE = "users.txt";
    private static final String PARKING_FILE = "parkings.txt";
    private static final String CUSTOMER_FILE = "customers.txt";

    public FileManager() {
        ensureFilesExist();
    }

    private void ensureFilesExist() {
        try {
            if (!Files.exists(Paths.get(USER_FILE))) {
                Files.createFile(Paths.get(USER_FILE));
            }
            if (!Files.exists(Paths.get(PARKING_FILE))) {
                Files.createFile(Paths.get(PARKING_FILE));
            }
            if (!Files.exists(Paths.get(CUSTOMER_FILE))) {
                Files.createFile(Paths.get(CUSTOMER_FILE));
            }
        } catch (IOException e) {
            System.err.println("Critical Error: Could not initialize data files: " + e.getMessage());
        }
    }

    public List<User> loadUsers() throws IOException {
        return Files.readAllLines(Paths.get(USER_FILE)).stream()
                .map(this::parseUserLine)
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }

    public void saveUsers(List<User> users) throws IOException {
        List<String> lines = users.stream()
                .map(u -> u.getUsername() + "," + u.getPassword() + "," + u.getRole() + "," + u.getFullName() + "," + u.getAssignedParking())
                .collect(Collectors.toList());
        Files.write(Paths.get(USER_FILE), lines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    private User parseUserLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) {
            return null;
        }

        String username = parts[0].trim();
        String password = parts[1].trim();
        String role = parts[2].trim().toUpperCase();
        String name = parts[3].trim();
        String sede = parts[4].trim();

        return role.equals("ADMIN")
                ? new Admin(username, password, name, sede)
                : new Clerk(username, password, name, sede);
    }

    public List<String> readAllParkingLines() throws IOException {
        return Files.readAllLines(Paths.get(PARKING_FILE));
    }

    public void saveParkingData(List<String> lines) throws IOException {
        Files.write(Paths.get(PARKING_FILE), lines,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public List<String> readAllCustomerLines() throws IOException {
        return Files.readAllLines(Paths.get(CUSTOMER_FILE));
    }

    public void appendCustomer(String data) throws IOException {
        Files.write(Paths.get(CUSTOMER_FILE), (data + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public List<String> readRawLines(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        return Files.exists(path) ? Files.readAllLines(path) : new ArrayList<>();
    }

    public void saveCustomer(String id, String name, boolean isPreferential) throws IOException {
        String data = id + "," + name + "," + isPreferential;
        appendCustomer(data);
    }

    public List<String[]> loadCustomersRaw() throws IOException {
        return readAllCustomerLines().stream()
                .map(line -> line.split(","))
                .collect(java.util.stream.Collectors.toList());
    }

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
        File file = new File("users.txt");

        if (!file.exists()) {
            return lines;
        }

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

    public List<String> readAllOccupancyRecords() throws IOException {
        List<String> records = new ArrayList<>();
        File file = new File("vehicles.txt");

        if (!file.exists()) {
            return records;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                records.add(line);
            }
        }
        return records;
    }

    public Map<Integer, String> getParkingLayout(String parkingName) {
        Map<Integer, String> layout = new HashMap<>();
        return layout;
    }

    public List<Integer> getPreferentialSpaceIds(String parkingName) {
        List<Integer> ids = new ArrayList<>();
        return ids;
    }

    public List<String> readAllCustomers() throws IOException {
        List<String> lines = new ArrayList<>();
        File file = new File("customers.txt");

        if (!file.exists()) {
            return lines;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    public void saveVehicleEntry(String dataLine) throws IOException {
        Files.write(Paths.get("vehicles.txt"),
                (dataLine + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    public List<String[]> loadAllParkedVehicles() throws IOException {
        List<String> lines = readAllOccupancyRecords();
        List<String[]> data = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                data.add(line.split("\\|"));
            }
        }
        return data;
    }

    public List<String[]> loadSpecificParkingConfig(String parkingName) throws IOException {
        String fileName = parkingName + "_config.txt";
        Path path = Paths.get(fileName);

        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        return Files.readAllLines(path).stream()
                .filter(line -> !line.trim().isEmpty())
                .map(line -> line.split("\\|"))
                .collect(Collectors.toList());
    }

    public void saveLinesToFile(String fileName, List<String> lines) throws IOException {
        File file = new File(fileName);
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, false)))) {
            for (String line : lines) {
                out.println(line);
            }
        }
    }
}
