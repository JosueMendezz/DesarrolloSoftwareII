package model.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.entities.*;

public class FileManager {

    private static final String USER_FILE_PATH = "users.txt";

    public List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(USER_FILE_PATH);

        // Existe ?
        if (!file.exists()) {
            return users;
        }

        //  Lectura del archivo
        //Se usa try-with-resources para asegurar que los flujos se cierren siempre
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 3) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String role = parts[2].trim().toUpperCase();

                    //  Creación de objetos según el rol -CLERK y OPERATO-
                    if (role.equals("ADMIN")) {
                        users.add(new Admin(username, password));
                    } else if (role.equals("CLERK") || role.equals("OPERATOR")) {
                        users.add(new Clerk(username, password));
                    }
                }
            }
        }

        return users;
    }
}
