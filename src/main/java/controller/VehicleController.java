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

/**
 * @author Caleb Murillo
 */
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

    /**
     * 1. BÚSQUEDA DE ESPACIO (Lógica Dinámica)
     */
    public int findClosestAvailableSpace(String parkingName, String vehicleType, boolean isPreferential) throws Exception {
        // Cargar configuración detallada (numero|tipo|preferencial)
        List<String[]> spaceConfigs = fileManager.loadSpecificParkingConfig(parkingName);

        // Cargar ocupación actual para saber qué espacios están ocupados
        List<String[]> parkedVehicles = fileManager.loadAllParkedVehicles();
        Set<Integer> occupiedSpaces = new HashSet<>();
        for (String[] v : parkedVehicles) {
            if (v.length > 10 && v[9].equalsIgnoreCase(parkingName)) {
                occupiedSpaces.add(Integer.parseInt(v[10]));
            }
        }

        // Búsqueda con triple validación: Disponibilidad + Tipo + Preferencia
        for (String[] config : spaceConfigs) {
            int spaceNumber = Integer.parseInt(config[0]);
            String allowedType = config[1];
            boolean spaceIsPreferential = Boolean.parseBoolean(config[2]);

            // Solo asignar si el espacio está libre
            if (!occupiedSpaces.contains(spaceNumber)) {
                // Validación estricta: Tipo coincide Y Categoría (Preferencial) coincide
                if (allowedType.equalsIgnoreCase(vehicleType) && spaceIsPreferential == isPreferential) {
                    return spaceNumber;
                }
            }
        }
        return -1; // No se encontró espacio que cumpla los requisitos
    }

    /**
     * 2. PROCESO DE ENTRADA Coordina la búsqueda del espacio y el guardado
     * físico del registro.
     */
    public int processVehicleEntry(String parkingName, String plate, String type,
            String brand, String model, String color,
            String details, boolean isPreferential,
            List<String> extraResponsibles, String ownerId) throws Exception {

        // Buscamos espacio usando los 3 parámetros necesarios
        int space = findClosestAvailableSpace(parkingName, type, isPreferential);
        if (space == -1) {
            return -1;
        }

        String entryTime = LocalDateTime.now().format(formatter);
        String extras = extraResponsibles.isEmpty() ? "None" : String.join(";", extraResponsibles);

        // Formato: PLACA|TIPO|MARCA|MODELO|COLOR|DETALLES|PREF|EXTRAS|OWNER|PARKING|SPACE|TIME
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

    /**
     * 3. ESTADO DEL MONITOR Genera las filas para la tabla del JTable.
     */
    public List<Object[]> getParkedVehiclesStatus(String selectedParking, String searchSpace) throws Exception {
        // 1. Cargar todos los vehículos desde el archivo vehicles.txt
        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();
        List<Object[]> tableData = new ArrayList<>();

        for (String[] v : allVehicles) {
            // Validar que la línea tenga datos y pertenezca al parqueo seleccionado
            if (v.length > 10 && v[9].equalsIgnoreCase(selectedParking)) {
                String space = v[10];

                // 2. Aplicar filtro de búsqueda por espacio (si el usuario escribió algo)
                if (searchSpace != null && !searchSpace.trim().isEmpty()) {
                    if (!space.equals(searchSpace.trim())) {
                        continue; // Saltar si no coincide con la búsqueda
                    }
                }

                // 3. OBTENER TARIFA DINÁMICA:
                // Creamos una instancia temporal del objeto vehículo usando su tipo
                // v[0]=plate, v[1]=type, v[2]=brand, v[3]=model, v[4]=color, v[8]=ownerId
                Vehicle instance = createVehicleInstance(v[0], v[1], v[2], v[3], v[4], v[8], new ArrayList<>());

                // Aquí el polimorfismo hace su magia: Car devolverá 8.0, Heavy devolverá 14.0, etc.
                double rate = instance.getHourlyRate();

                // 4. Obtener nombre del dueño
                String ownerName = getCustomerName(v[8]);

                // 5. Agregar la fila con la tarifa correcta
                tableData.add(new Object[]{
                    space,
                    v[0], // Placa
                    ownerName, // Dueño
                    v[3], // Modelo
                    v[2], // Marca
                    v[1], // Tipo
                    "₡" + rate, //Símbolo al frente
                    (v.length > 11 ? v[11] : "N/A") // Hora de entrada
                });
            }
        }
        return tableData;
    }

    // --- MÉTODOS DE APOYO Y UTILIDAD ---
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

    public void registerNewOwner(String id, String name, boolean isPreferential) throws Exception {
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

        // Calculamos la diferencia en minutos
        long minutes = java.time.Duration.between(entryTime, exitTime).toMinutes();

        // Cobramos por hora o fracción (mínimo 1 hora)
        double hours = Math.ceil(minutes / 60.0);
        return hours * hourlyRate;
    }

    public void processPayment(String plate) throws IOException {
        List<String[]> allVehicles = fileManager.loadAllParkedVehicles();
        // Filtramos la lista para quitar el vehículo con esa placa
        List<String[]> updatedList = allVehicles.stream()
                .filter(v -> !v[0].equalsIgnoreCase(plate))
                .collect(java.util.stream.Collectors.toList());

        // Convertimos de String[] a String con el formato original (separado por |)
        List<String> lines = updatedList.stream()
                .map(v -> String.join("|", v))
                .collect(java.util.stream.Collectors.toList());

        fileManager.saveLinesToFile("vehicles.txt", lines);
    }
}
