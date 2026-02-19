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

    public void prepareTempParking(String name, int total, int pref) {
        this.tempParking = new ParkingLot(0, name, total, pref);
        this.tempParking.setSpaces(new ParkingSpace[total]);
        this.spacesConfigured = 0;
        this.prefSpacesAssigned = 0;
    }

    public void validateParkingName(String name) throws Exception {
        List<String> existingLines = fileManager.readAllParkingLines();
        for (String line : existingLines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\|");
            if (parts.length > 0 && parts[0].equalsIgnoreCase(name.trim())) {
                throw new Exception("Error: El parqueo '" + name + "' ya existe.");
            }
        }
    }

    public void configureSpaceBlock(int qty, boolean isPref, String type) throws Exception {
        int totalRemaining = getRemainingSpaces();
        int prefRemaining = getPrefRemaining();

        if (!isPref && (totalRemaining - qty) < prefRemaining) {
            throw new Exception("Error: Debes reservar espacios para la cuota preferencial.");
        }
        if (isPref && qty > prefRemaining) {
            throw new Exception("La cantidad excede la cuota preferencial restante.");
        }

        for (int i = 0; i < qty; i++) {
            createSingleSpace(isPref, type);
        }
        if (isPref) {
            prefSpacesAssigned += qty;
        }
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

    public int getOccupancyCount(String parkingName) throws Exception {
        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();
        int count = 0;
        for (String[] v : allVehicles) {
            if (v.length > 10 && v[9].trim().equalsIgnoreCase(parkingName.trim())) {
                count++;
            }
        }
        return count;
    }

    private void updateParkingInList(String oldName) throws IOException {
        List<String> lines = fileManager.readAllParkingLines();
        List<String> updatedLines = new ArrayList<>();
        boolean exists = false;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
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
        saveDetailedSpaceConfig();
    }

    private void saveDetailedSpaceConfig() throws IOException {
        String fileName = tempParking.getName() + "_config.txt";
        List<String> configLines = new ArrayList<>();

        for (ParkingSpace space : tempParking.getSpaces()) {
            if (space != null) {
                String line = String.format("%d|%s|%b",
                        space.getSpaceNumber(),
                        space.getVehicleTypesSupported(),
                        space.isIsPreferential());
                configLines.add(line);
            }
        }
        fileManager.saveLinesToFile(fileName, configLines);
    }

    private String formatParkingLine() {
        return String.format("%s|%d|%d", tempParking.getName(),
                tempParking.getNumberOfSpaces(), tempParking.getPreferentialSpaces());
    }

    public void deleteParkingBranch(String name) throws Exception {
        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();

        long count = allVehicles.stream()
                .filter(v -> v.length > 9 && v[9].trim().equalsIgnoreCase(name.trim()))
                .count();

        if (count > 0) {
            throw new Exception("No se puede eliminar: El parqueo '" + name
                    + "' tiene " + count + " vehículo(s) dentro actualmente.");
        }

        List<String> lines = fileManager.readAllParkingLines();
        List<String> remaining = new ArrayList<>();
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            if (!line.split("\\|")[0].equalsIgnoreCase(name)) {
                remaining.add(line);
            }
        }
        fileManager.saveParkingData(remaining);

        java.io.File configFile = new java.io.File(name + "_config.txt");
        if (configFile.exists()) {
            configFile.delete();
        }
    }

    public void validateSpaceTypeChange(String parkingName, int startSpace, int endSpace) throws Exception {
        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();

        for (String[] v : allVehicles) {
            if (v.length > 10 && v[9].trim().equalsIgnoreCase(parkingName.trim())) {
                int spaceNumber = Integer.parseInt(v[10].trim());

                if (spaceNumber >= startSpace && spaceNumber <= endSpace) {
                    throw new Exception("No puede cambiar el tipo de los espacios " + startSpace + " al " + endSpace
                            + " porque el espacio #" + spaceNumber + " está ocupado por un(a) " + v[1]);
                }
            }
        }
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

    public ParkingLot getTempParking() {
        return this.tempParking;
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

    public FileManager getFileManager() {
        return this.fileManager;
    }

    public int getOccupiedPreferentialCount(String parkingName) throws IOException {
        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();
        int count = 0;
        for (String[] v : allVehicles) {
            if (v.length > 10 && v[9].trim().equalsIgnoreCase(parkingName.trim())) {
                if (v[6].trim().equalsIgnoreCase("true")) {
                    count++;
                }
            }
        }
        return count;
    }

    public List<String> getAllParkingLotNames() {
        List<String> names = new ArrayList<>();
        try {
            List<String> data = fileManager.readAllParkingLines();

            for (String line : data) {
                if (!line.trim().isEmpty()) {
                    names.add(line.split("\\|")[0]);
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return names;
    }

    public void validateSpaceBlockIsFree(String parkingName, int start, int end, String newType, boolean newIsPref) throws Exception {
        if (parkingName == null) {
            return;
        }

        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();
        for (String[] v : allVehicles) {
            if (v.length > 10 && v[9].trim().equalsIgnoreCase(parkingName.trim())) {
                try {
                    int occupiedSpace = Integer.parseInt(v[10].trim());

                    if (occupiedSpace >= start && occupiedSpace <= end) {
                        String vehicleType = v[1].trim();
                        boolean isVehiclePreferential = v[6].trim().equalsIgnoreCase("true");

                        if (!vehicleType.equalsIgnoreCase(newType.trim())) {
                            throw new Exception("El espacio #" + occupiedSpace + " está ocupado por un(a) " + vehicleType
                                    + ". No puede cambiar el tipo de vehículo de este bloque.");
                        }

                        if (isVehiclePreferential && !newIsPref) {
                            throw new Exception("¡BLOQUEO DE SEGURIDAD!\n"
                                    + "El espacio #" + occupiedSpace + " está ocupado por un cliente con DISCAPACIDAD.\n"
                                    + "No puede convertir este bloque en 'No Preferencial' mientras el vehículo esté dentro.");
                        }
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
    }

    public void validatePreferentialQuota(String parkingName, int newPrefQuota) throws Exception {
        if (parkingName == null) {
            return;
        }

        int occupiedPrefs = getOccupiedPreferentialCount(parkingName);

        if (newPrefQuota < occupiedPrefs) {
            throw new Exception("No puede reducir los espacios preferenciales a " + newPrefQuota
                    + " porque actualmente hay " + occupiedPrefs + " vehículos preferenciales parqueados.");
        }
    }
}
