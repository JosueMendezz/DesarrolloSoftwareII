package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.Set;
import model.data.FileManager;
import model.entities.Customer;
import model.entities.*;
import java.util.HashSet;

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
        List<String[]> spaceConfigs = fileManager.loadSpecificParkingConfig(parkingName);
        List<String[]> parkedVehicles = fileManager.loadAllParkedVehicles();

        Set<Integer> occupiedSpaces = parkedVehicles.stream()
                .filter(v -> v.length > 10 && v[9].equalsIgnoreCase(parkingName))
                .map(v -> Integer.parseInt(v[10]))
                .collect(Collectors.toSet());

        for (String[] config : spaceConfigs) {
            int spaceNumber = Integer.parseInt(config[0]);
            if (!occupiedSpaces.contains(spaceNumber)) {
                if (config[1].equalsIgnoreCase(vehicleType) && Boolean.parseBoolean(config[2]) == isPreferential) {
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

                double rate = getRateFromFile(v[1]);

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
        return fileManager.readAllParkingLines().stream()
                .map(line -> line.split("\\|")[0])
                .collect(Collectors.toList());
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
        String data = id + "," + name + "," + isPreferential;
        fileManager.appendCustomer(data);
    }

    public FileManager getFileManager() {
        return this.fileManager;
    }

    private String getCustomerName(String ownerId) throws Exception {
        Customer owner = findCustomerById(ownerId);
        return (owner != null) ? owner.getName() : "Desconocido (" + ownerId + ")";
    }

    public double calculateAmount(String entryTimeStr, double hourlyRate) {
        LocalDateTime entryTime = LocalDateTime.parse(entryTimeStr, formatter);
        long minutes = java.time.Duration.between(entryTime, LocalDateTime.now()).toMinutes();
        double hours = Math.ceil(minutes / 60.0);
        if (hours <= 0) {
            hours = 1; // Cobrar al menos la primera hora
        }
        return hours * hourlyRate;
    }

    public void processPayment(String plate) throws IOException {
        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();
        List<String> remaining = allVehicles.stream()
                .filter(v -> !v[0].equalsIgnoreCase(plate))
                .map(v -> String.join("|", v))
                .collect(Collectors.toList());
        fileManager.saveLinesToFile("vehicles.txt", remaining);
    }

    public double calculateFinalPrice(String plate, String parkingName) throws Exception {
        // 1. Buscar el vehículo en el archivo de ocupación
        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();
        String[] vehicleData = allVehicles.stream()
                .filter(v -> v[0].equalsIgnoreCase(plate))
                .findFirst()
                .orElseThrow(() -> new Exception("Vehículo no encontrado en el sistema."));

        String type = vehicleData[1];
        String entryTimeStr = vehicleData[11];

        // 2. Obtener la tarifa usando el método que ya lee el archivo
        double hourlyRate = getRateFromFile(type);

        return calculateAmount(entryTimeStr, hourlyRate);
    }

    public double getRateFromFile(String type) {
        try {
            List<String> lines = fileManager.readLinesFromFile("rates.txt");
            for (String line : lines) {
                String[] parts = line.split("\\|");
                // Comparamos ignorando mayúsculas y espacios extra
                if (parts[0].trim().equalsIgnoreCase(type.trim())) {
                    return Double.parseDouble(parts[1]);
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo tarifas: " + e.getMessage());
        }

        // Backup de emergencia
        String t = type.toLowerCase();
        if (t.contains("auto")) {
            return 1000.0;
        }
        if (t.contains("moto")) {
            return 500.0;
        }
        if (t.contains("bici")) {
            return 200.0;
        }
        if (t.contains("pesado")) {
            return 2000.0;
        }

        return 1000.0; // Default absoluto
    }

    public void finalizeTransaction(String plate, double amount, String operator, String parkingName) throws Exception {
        // 1. Recuperar toda la información del vehículo antes de borrarlo
        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();
        String[] v = allVehicles.stream()
                .filter(veh -> veh[0].equalsIgnoreCase(plate))
                .findFirst()
                .orElseThrow(() -> new Exception("Error crítico: Vehículo no encontrado al finalizar transacción."));

        // v[0]=placa, v[1]=tipo, v[2]=marca, v[3]=modelo, v[4]=color, v[5]=detalles, 
        // v[6]=preferencial, v[7]=extras, v[8]=ownerId, v[9]=sede, v[10]=espacio, v[11]=entrada
        // 2. Preparar los datos extendidos
        String exitTime = LocalDateTime.now().format(formatter);
        String entryTime = v[11];
        String ownerId = v[8];
        String type = v[1];
        String brandModel = v[2] + " " + v[3];
        String space = v[10];
        String isPreferential = v[6].equalsIgnoreCase("true") ? "PREFERENCIAL" : "REGULAR";
        double appliedRate = getRateFromFile(type); // La tarifa que se le aplicó

        // 3. Crear la línea de historial "Mega Completa"
        StringBuilder sb = new StringBuilder();
        sb.append(exitTime).append("|") // Hora de salida
                .append(plate).append("|") // Placa
                .append(ownerId).append("|") // ID Cliente
                .append(type).append("|") // Tipo vehículo
                .append(brandModel).append("|") // Marca y Modelo
                .append(space).append("|") // Espacio físico
                .append(isPreferential).append("|") // Si fue preferencial
                .append(entryTime).append("|") // Hora de entrada
                .append(appliedRate).append("|") // Precio x hora
                .append(String.format("%.2f", amount)).append("|") // Total cobrado
                .append(operator).append("|") // Quien cobró
                .append(parkingName);                // En qué sucursal

        // 4. Guardar en history.txt
        fileManager.appendToFile("history.txt", sb.toString());

        // 5. Liberar el espacio (Eliminar de vehicles.txt)
        processPayment(plate);
    }

    public String getFullVehicleInfo(String plate) {
        try {
            List<String[]> allVehicles = fileManager.loadAllParkedVehicles();
            for (String[] v : allVehicles) {
                // Estructura del array v según tu processVehicleEntry:
                // [0]placa, [1]tipo, [2]marca, [3]modelo, [4]color, [5]detalles, 
                // [6]preferencial, [7]extras, [8]ownerId, [9]sede, [10]espacio, [11]entrada

                if (v[0].equalsIgnoreCase(plate)) {
                    String ownerName = getCustomerName(v[8]);
                    double rate = getRateFromFile(v[1]);

                    // Formatear autorizados
                    String autorizados = v[7].replace(";", ", ");
                    if (autorizados.equalsIgnoreCase("None") || autorizados.isEmpty()) {
                        autorizados = "<i>No se registraron responsables adicionales</i>";
                    }

                    // Determinar etiqueta de espacio preferencial
                    String prefStatus = v[6].equalsIgnoreCase("true")
                            ? "<b style='color: #FF5252;'> SÍ (Preferencial)</b>" : "No";

                    // Retorno con formato HTML para "Visión Empresarial"
                    return "<html><body style='width: 300px; font-family: sans-serif;'>"
                            + "<h2 style='color: #2196F3; border-bottom: 1px solid #ccc;'>EXPEDIENTE: " + v[0] + "</h2>"
                            + "<b>--- INFORMACIÓN DEL CLIENTE ---</b><br>"
                            + "<b>Dueño:</b> " + ownerName + "<br>"
                            + "<b>ID Propietario:</b> " + v[8] + "<br><br>"
                            + "<b>--- DETALLES DEL VEHÍCULO ---</b><br>"
                            + "<b>Tipo:</b> " + v[1] + "<br>"
                            + "<b>Marca/Modelo:</b> " + v[2] + " " + v[3] + "<br>"
                            + "<b>Color:</b> " + v[4] + "<br>"
                            + "<b>Observaciones:</b> " + (v[5].equals("N/A") ? "<i>Sin notas</i>" : v[5]) + "<br><br>"
                            + "<b>--- UBICACIÓN Y ACCESO ---</b><br>"
                            + "<b>Sede:</b> " + v[9] + "<br>"
                            + "<b>Espacio Asignado:</b> <span style='font-size: 12pt; color: #4CAF50;'>" + v[10] + "</span><br>"
                            + "<b>Acceso Preferencial:</b> " + prefStatus + "<br><br>"
                            + "<b>--- REGISTRO OPERATIVO ---</b><br>"
                            + "<b>Hora Entrada:</b> " + v[11] + "<br>"
                            + "<b>Tarifa Actual:</b> ₡" + rate + " / hora<br>"
                            + "<b>Autorizados a retirar:</b><br>" + autorizados
                            + "</body></html>";
                }
            }
        } catch (Exception e) {
            return "<html><b style='color:red;'>Error al obtener detalles:</b> " + e.getMessage() + "</html>";
        }
        return "Vehículo no encontrado.";
    }

    public List<String[]> getAllRates() {
        List<String> lines = fileManager.readLinesFromFile("rates.txt");
        return lines.stream()
                .map(line -> line.split("\\|"))
                .collect(Collectors.toList());
    }

    public void updateRate(String type, String newPrice) throws IOException {
        List<String[]> allRates = getAllRates();
        List<String> updatedLines = new ArrayList<>();

        for (String[] rate : allRates) {
            if (rate[0].equalsIgnoreCase(type)) {
                updatedLines.add(type + "|" + newPrice);
            } else {
                updatedLines.add(rate[0] + "|" + rate[1]);
            }
        }
        fileManager.saveLinesToFile("rates.txt", updatedLines);
    }

    public void updateAllRates(List<String> updatedLines) throws IOException {
        // Usamos el fileManager que ya tienes para sobreescribir el archivo con la nueva lista
        fileManager.saveLinesToFile("rates.txt", updatedLines);
    }
}
