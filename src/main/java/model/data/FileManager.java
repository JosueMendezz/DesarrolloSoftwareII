package model.data;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import model.entities.Admin;
import model.entities.Clerk;
import model.entities.User;

public class FileManager {

    private static final String USER_FILE_PATH = "users.txt";
    private static final String CUSTOMER_FILE_PATH = "customers.txt";

    // Load users from text file into a polymorphic list
    public List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(USER_FILE_PATH);

        if (!file.exists()) {
            file.createNewFile();
            return users;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String role = parts[2].trim().toUpperCase();

                    // Instantiate specific subclass based on role
                    if (role.equals("ADMIN")) {
                        users.add(new Admin(username, password));
                    } else {
                        users.add(new Clerk(username, password));
                    }
                }
            }
        }
        return users;
    }

    // Append a single new user to the file
    public void saveNewUser(String username, String password, String role) throws IOException {
        String line = username + "," + password + "," + role;
        Files.write(Paths.get(USER_FILE_PATH),
                (line + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    // Rewrite the entire users file (for updates or deletions)
    public void overwriteUsers(List<User> users) throws IOException {
        List<String> lines = new ArrayList<>();
        for (User user : users) {
            lines.add(user.getUsername() + "," + user.getPassword() + "," + user.getRole());
        }
        Files.write(Paths.get(USER_FILE_PATH), lines,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    // Append a single new customer
    public void saveCustomer(String id, String name, boolean isPreferential) throws IOException {
        String line = id + "," + name + "," + isPreferential;
        Files.write(Paths.get(CUSTOMER_FILE_PATH),
                (line + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    // Load raw customer data for the JTable
    public List<String[]> loadCustomersRaw() throws IOException {
        List<String[]> dataList = new ArrayList<>();
        Path path = Paths.get(CUSTOMER_FILE_PATH);

        if (!Files.exists(path)) {
            return dataList;
        }

        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                dataList.add(parts);
            }
        }
        return dataList;
    }

    // Rewrite the entire customers file (for updates or deletions)
    public void overwriteCustomers(List<String[]> dataList) throws IOException {
        List<String> lines = new ArrayList<>();
        for (String[] row : dataList) {
            lines.add(row[0] + "," + row[1] + "," + row[2]);
        }
        Files.write(Paths.get(CUSTOMER_FILE_PATH), lines,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}
