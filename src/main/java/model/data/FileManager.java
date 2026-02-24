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
    private static final String VEHICLE_FILE = "vehicles.txt";
    private static final String RATES_FILE = "rates.txt";
    private static final String HISTORY_FILE = "history.txt";

    public FileManager() {
        ensureFilesExist();
    }

    private void ensureFilesExist() {
        String[] essentialFiles = {
            USER_FILE, PARKING_FILE, CUSTOMER_FILE,
            VEHICLE_FILE, RATES_FILE, HISTORY_FILE
        };

        for (String fileName : essentialFiles) {
            try {
                Path path = Paths.get(fileName);
                if (!Files.exists(path)) {
                    Files.createFile(path);
                    // Inicializar tarifas por defecto si el archivo es nuevo
                    if (fileName.equals(RATES_FILE)) {
                        initDefaultRates();
                    }
                }
            } catch (IOException e) {
                System.err.println("Error inicializando: " + fileName + " -> " + e.getMessage());
            }
        }
    }

    private void initDefaultRates() throws IOException {
        List<String> defaultRates = List.of(
                "Automóvil|1000",
                "Motocicleta|500",
                "Bicicleta|200",
                "Vehículo Pesado|2000"
        );
        Files.write(Paths.get(RATES_FILE), defaultRates);
    }

    public List<String> readLinesFromFile(String fileName) {
        List<String> lines = new ArrayList<>();
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            return lines;
        }

        // Usar un BufferedReader con un FileReader garantiza que leamos el estado físico actual
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error de lectura crítica: " + e.getMessage());
        }
        return lines;
    }

    public void appendToFile(String fileName, String data) throws IOException {
        Files.write(Paths.get(fileName),
                (data + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    public void updateRates(List<String> newRates) throws IOException {
        // Esto sobreescribe el rates.txt con los nuevos valores del Admin
        Files.write(Paths.get("rates.txt"), newRates,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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

    public String getCustomerNameById(String id) {
        return readLinesFromFile("customers.txt").stream()
                .map(line -> line.split(","))
                .filter(data -> data.length > 1 && data[0].trim().equals(id.trim()))
                .map(data -> data[1].trim())
                .findFirst()
                .orElse("Desconocido (" + id + ")");
    }
}
