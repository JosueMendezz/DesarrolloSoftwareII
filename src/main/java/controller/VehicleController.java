package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import model.data.FileManager;
import model.entities.Customer;
import model.entities.*;

public class VehicleController {

    private final FileManager fileManager;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public VehicleController(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Vehicle createVehicleInstance(String plate, String type, String brand, String model,
            String color, String ownerId, List<String> additional) {

        switch (type.toLowerCase()) {
            case "automóvil":
            case "automovil":
            case "car":
                return new Car(plate, "Automóvil", brand, model, color, ownerId, additional, LocalDateTime.now());

            case "motocicleta":
            case "moto":
            case "motorcycle":
                return new Motorcycle(plate, "Motocicleta", brand, model, color, ownerId, additional, LocalDateTime.now());

            case "bicicleta":
            case "bicycle":
                return new Bicycle(plate, "Bicicleta", brand, model, color, ownerId, additional, LocalDateTime.now());

            case "vehículo pesado":
            case "heavy":
                return new HeavyVehicle(plate, "Vehículo Pesado", brand, model, color, ownerId, additional, LocalDateTime.now());

            default:
                throw new IllegalArgumentException("Tipo de vehículo no reconocido: " + type);
        }
    }

    public int findClosestAvailableSpace(String parkingName, String vehicleType, boolean isPreferential) throws Exception {
        // Cargar configuración detallada (numero|tipo|preferencial)
        List<String[]> spaceConfigs = fileManager.loadSpecificParkingConfig(parkingName);

        List<String[]> parkedVehicles = fileManager.loadAllParkedVehicles();
        Set<Integer> occupiedSpaces = new HashSet<>();
        for (String[] v : parkedVehicles) {
            if (v.length > 10 && v[9].equalsIgnoreCase(parkingName)) {
                occupiedSpaces.add(Integer.parseInt(v[10]));
            }
        }

        for (String[] config : spaceConfigs) {
            int spaceNumber = Integer.parseInt(config[0]);
            String allowedType = config[1];
            boolean spaceIsPreferential = Boolean.parseBoolean(config[2]);

            if (!occupiedSpaces.contains(spaceNumber)) {
                if (allowedType.equalsIgnoreCase(vehicleType) && spaceIsPreferential == isPreferential) {
                    return spaceNumber;
                }
            }
        }
        return -1;
    }

    public int processVehicleEntry(String parkingName, String plate, String type,
            String brand, String model, String color,
            String details, boolean isPreferential,
            List<String> extraResponsibles, String ownerId) throws Exception {

        int space = findClosestAvailableSpace(parkingName, type, isPreferential);
        if (space == -1) {
            return -1;
        }

        String entryTime = LocalDateTime.now().format(formatter);
        String extras = extraResponsibles.isEmpty() ? "None" : String.join(";", extraResponsibles);

        StringBuilder sb = new StringBuilder();
        sb.append(plate).append("|")
                .append(type).append("|")
                .append(brand).append("|")
                .append(model).append("|")
                .append(color).append("|")
                .append(details.isEmpty() ? "N/A" : details).append("|")
                .append(isPreferential).append("|")
                .append(extras).append("|")
                .append(ownerId).append("|")
                .append(parkingName).append("|")
                .append(space).append("|")
                .append(entryTime);

        fileManager.saveVehicleEntry(sb.toString());
        return space;
    }

    public List<Object[]> getParkedVehiclesStatus(String selectedParking, String searchSpace) throws Exception {
        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();
        List<Object[]> tableData = new ArrayList<>();

        for (String[] v : allVehicles) {
            if (v.length > 10 && v[9].equalsIgnoreCase(selectedParking)) {
                String space = v[10];

                if (searchSpace != null && !searchSpace.trim().isEmpty()) {
                    if (!space.equals(searchSpace.trim())) {
                        continue;
                    }
                }

                Vehicle instance = createVehicleInstance(v[0], v[1], v[2], v[3], v[4], v[8], new ArrayList<>());

                double rate = instance.getHourlyRate();

                String ownerName = getCustomerName(v[8]);

                tableData.add(new Object[]{
                    space,
                    v[0],
                    ownerName,
                    v[3],
                    v[2],
                    v[1],
                    "₡" + rate,
                    (v.length > 11 ? v[11] : "N/A")
                });
            }
        }
        return tableData;
    }

    public boolean isVehicleAlreadyParked(String plate) throws Exception {
        List<String> allRecords = fileManager.readAllOccupancyRecords();
        for (String record : allRecords) {
            String[] data = record.split("\\|");
            if (data.length > 0 && data[0].equalsIgnoreCase(plate.trim())) {
                return true;
            }
        }
        return false;
    }

    public List<String> getAvailableParkingNames() throws IOException {
        List<String> lines = fileManager.readAllParkingLines();
        List<String> names = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length > 0) {
                names.add(parts[0]);
            }
        }
        return names;
    }

    public Customer findCustomerById(String id) throws Exception {
        List<String[]> allCustomers = fileManager.loadCustomersRaw();
        for (String[] data : allCustomers) {
            if (data[0].equals(id)) {
                return new Customer(data[0], data[1], Boolean.parseBoolean(data[2]));
            }
        }
        return null;
    }

    public boolean customerExists(String id) throws Exception {
        return findCustomerById(id) != null;
    }

    public void registerNewOwner(String id, String name, boolean isPreferential) throws Exception {
        if (customerExists(id)) {

            return;
        }
        String data = id + "," + name + "," + isPreferential;
        fileManager.appendCustomer(data);
    }

    public FileManager getFileManager() {
        return this.fileManager;
    }

    private String getCustomerName(String ownerId) throws Exception {
        Customer owner = findCustomerById(ownerId);
        return (owner != null) ? owner.getName() : "Desconocido";
    }

    public double calculateAmount(String entryTimeStr, double hourlyRate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime entryTime = LocalDateTime.parse(entryTimeStr, formatter);
        LocalDateTime exitTime = LocalDateTime.now();

        long minutes = java.time.Duration.between(entryTime, exitTime).toMinutes();

        double hours = Math.ceil(minutes / 60.0);
        return hours * hourlyRate;
    }

    public void processPayment(String plate) throws IOException {
        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();
        List<String[]> updatedList = allVehicles.stream()
                .filter(v -> !v[0].equalsIgnoreCase(plate))
                .collect(java.util.stream.Collectors.toList());

        List<String> lines = updatedList.stream()
                .map(v -> String.join("|", v))
                .collect(java.util.stream.Collectors.toList());

        fileManager.saveLinesToFile("vehicles.txt", lines);
    }
}
