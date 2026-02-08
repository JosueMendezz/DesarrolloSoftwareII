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

   public static void updateParkingInFile(ParkingLot updatedParking, String oldName) throws IOException {
    List<String> lines = readAllLines("parkings.txt");
    List<String> updatedLines = new java.util.ArrayList<>();
    boolean found = false;

    for (String line : lines) {
        String[] data = line.split("\\|");
        // Comparamos contra el nombre ORIGINAL (oldName)
        if (data[0].equalsIgnoreCase(oldName)) {
            updatedLines.add(updatedParking.getName() + "|" + 
                             updatedParking.getNumberOfSpaces() + "|" + 
                             updatedParking.getPreferentialSpaces());
            found = true;
        } else {
            updatedLines.add(line);
        }
    }

    // Si no existía (era un parqueo totalmente nuevo), se añade
    if (!found) {
        updatedLines.add(updatedParking.getName() + "|" + 
                         updatedParking.getNumberOfSpaces() + "|" + 
                         updatedParking.getPreferentialSpaces());
    }

    java.nio.file.Files.write(java.nio.file.Paths.get("parkings.txt"), updatedLines);
}

    public static void overwriteParking(ParkingLot parking) throws IOException {
    // Este método debería llamar internamente a updateParkingFull 
    // usando los datos del objeto ParkingLot.
    updateParkingFull(parking.getName(), parking.getName(), 
                      parking.getNumberOfSpaces(), parking.getPreferentialSpaces());
}
    
    public static List<String> readAllLines(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        // El try-with-resources asegura que el archivo se cierre aunque haya error
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines; // Lanza la IOException si el archivo no existe o está bloqueado
    }
    
    // En FileManager.java
public static void deleteParking(String name) throws IOException {
    List<String> lines = readAllLines("parkings.txt");
    // Filtramos: dejamos todas las líneas EXCEPTO la que coincide con el nombre
    List<String> updatedLines = lines.stream()
            .filter(line -> !line.split("\\|")[0].equals(name))
            .toList();
            
    java.nio.file.Files.write(java.nio.file.Paths.get("parkings.txt"), updatedLines);
}

public static void updateParkingFull(String oldName, String newName, int newTotal, int newPref) throws IOException {
    List<String> lines = readAllLines("parkings.txt");
    List<String> updatedLines = new java.util.ArrayList<>();
    
    for (String line : lines) {
        String[] data = line.split("\\|");
        // Si el nombre coincide, armamos la nueva línea con el orden: Nombre|Total|Pref
        if (data[0].equals(oldName)) {
            updatedLines.add(newName + "|" + newTotal + "|" + newPref);
        } else {
            updatedLines.add(line);
        }
    }
    // Sobrescribimos el archivo con la lista actualizada
    java.nio.file.Files.write(java.nio.file.Paths.get("parkings.txt"), updatedLines);
}

public static model.entities.ParkingLot getParkingByName(String name) throws IOException {
    List<String> lines = readAllLines("parkings.txt");
    for (String line : lines) {
        String[] data = line.split("\\|");
        // Si el nombre (data[0]) coincide, creamos el objeto
        if (data[0].equalsIgnoreCase(name)) {
            model.entities.ParkingLot p = new model.entities.ParkingLot();
            p.setName(data[0]);
            p.setNumberOfSpaces(Integer.parseInt(data[1]));
            p.setPreferentialSpaces(Integer.parseInt(data[2]));
            
            // Inicializamos el arreglo de espacios vacío para la nueva configuración
            p.setSpaces(new model.entities.ParkingSpace[p.getNumberOfSpaces()]);
            return p;
        }
    }
    throw new IOException("No se encontró el parqueo con el nombre: " + name);
}

public static boolean exists(String name) throws IOException {
    List<String> lines = readAllLines("parkings.txt");
    return lines.stream()
                .map(line -> line.split("\\|")[0].trim())
                .anyMatch(n -> n.equalsIgnoreCase(name.trim()));
}
}
    