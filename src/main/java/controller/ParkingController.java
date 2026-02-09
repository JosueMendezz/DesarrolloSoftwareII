package controller;

import model.entities.ParkingLot;
import model.entities.ParkingSpace;
import model.data.FileManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParkingController {

    private ParkingLot tempParking;
    private int spacesConfigured;
    private int prefSpacesAssigned = 0;
    private final FileManager fileManager;

    public ParkingController(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    // ESTE ES EL MÉTODO QUE LLAMA TU VISTA EN LA LÍNEA 88
    // Lo unificamos para que coincida con el nombre que pusiste en el Frame
    public void prepareTempParking(String name, int total, int pref) {
        this.tempParking = new ParkingLot(0, name, total, pref);
        // Inicializamos el arreglo de espacios para evitar NullPointerException
        this.tempParking.setSpaces(new ParkingSpace[total]);
        this.spacesConfigured = 0;
        this.prefSpacesAssigned = 0;
    }

    // Validación de nombre duplicado corregida (usando el separador '|')
    public void validateParkingName(String name) throws Exception {
        List<String> existingLines = fileManager.readAllParkingLines(); 
        for (String line : existingLines) {
            if (line.trim().isEmpty()) continue;
            // IMPORTANTE: Tu archivo usa | según el método formatParkingLine
            String[] parts = line.split("\\|"); 
            if (parts.length > 0 && parts[0].equalsIgnoreCase(name.trim())) {
                throw new Exception("Error: El parqueo '" + name + "' ya existe. Usa otro nombre.");
            }
        }
    }

    public void configureSpaceBlock(int qty, boolean isPref, String type) throws Exception {
        int totalRemaining = getRemainingSpaces();
        int prefRemaining = getPrefRemaining();

        if (!isPref && (totalRemaining - qty) < prefRemaining) {
            throw new Exception("Error: Debes reservar al menos " + prefRemaining + 
                                " espacios para la cuota preferencial pendiente.");
        }

        if (isPref && qty > prefRemaining) {
            throw new Exception("La cantidad excede la cuota preferencial restante.");
        }

        for (int i = 0; i < qty; i++) {
            createSingleSpace(isPref, type);
        }
        
        if (isPref) prefSpacesAssigned += qty;
    }

    private void createSingleSpace(boolean isPref, String type) {
        ParkingSpace space = new ParkingSpace();
        space.setSpaceNumber(spacesConfigured + 1);
        space.setVehicleTypesSupported(type);
        space.setIsPreferential(isPref);
        space.setIsOccupied(false);

        tempParking.getSpaces()[spacesConfigured] = space;
        spacesConfigured++;
    }

    public void saveParkingConfiguration(String oldName) throws IOException {
        updateParkingInList(oldName);
        spacesConfigured = 0;
        prefSpacesAssigned = 0;
    }

    private void updateParkingInList(String oldName) throws IOException {
        List<String> lines = fileManager.readAllParkingLines();
        List<String> updatedLines = new ArrayList<>();
        boolean exists = false;

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] data = line.split("\\|");
            if (data[0].equalsIgnoreCase(oldName)) {
                updatedLines.add(formatParkingLine());
                exists = true;
            } else {
                updatedLines.add(line);
            }
        }
        if (!exists) {
            updatedLines.add(formatParkingLine());
        }
        fileManager.saveParkingData(updatedLines);
    }

    private String formatParkingLine() {
        return String.format("%s|%d|%d", tempParking.getName(),
                tempParking.getNumberOfSpaces(), tempParking.getPreferentialSpaces());
    }

    public int getRemainingSpaces() {
        return tempParking.getNumberOfSpaces() - spacesConfigured;
    }

    public int getPrefRemaining() {
        return tempParking.getPreferentialSpaces() - prefSpacesAssigned;
    }

    public boolean isConfigFinished() {
        return tempParking != null && getRemainingSpaces() == 0 && getPrefRemaining() <= 0;
    }

    public List<String[]> loadAllParkings() throws IOException {
        List<String> lines = fileManager.readAllParkingLines();
        List<String[]> data = new ArrayList<>();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                data.add(line.split("\\|"));
            }
        }
        return data;
    }
    
    public void deleteParkingBranch(String name) throws IOException {
    List<String> lines = fileManager.readAllParkingLines();
    List<String> remaining = new ArrayList<>();
    for (String line : lines) {
        if (line.trim().isEmpty()) continue; // Seguridad para líneas vacías
        // Usamos el separador | que definimos antes
        if (!line.split("\\|")[0].equalsIgnoreCase(name)) {
            remaining.add(line);
        }
    }
    fileManager.saveParkingData(remaining);
}
    public ParkingLot getTempParking() { return this.tempParking; }
    public FileManager getFileManager() { return this.fileManager; }
}