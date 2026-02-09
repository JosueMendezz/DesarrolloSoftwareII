package controller;

import model.data.FileManager;
import java.io.IOException;
import java.util.List;

public class CustomerController {

    private final FileManager fileManager;

    public CustomerController(FileManager fileManager) {
        this.fileManager = fileManager;
    }

   public void registerCustomer(String id, String name, boolean isPreferential) throws IOException {
        validateCustomerData(id, name);
        // Cargamos datos actuales para verificar duplicados
        List<String[]> currentData = getAllCustomers();
        checkDuplicateId(id, currentData);

        fileManager.saveCustomer(id, name, isPreferential);
    }

    public void removeCustomer(int selectedRow, List<String[]> tableData) 
            throws IOException, IllegalStateException {
        
        if (selectedRow < 0) {
            throw new IllegalStateException("Debe seleccionar un cliente de la tabla.");
        }

        tableData.remove(selectedRow);
        syncFileData(tableData);
    }

    public void updateCustomer(int selectedRow, String newName, boolean isPreferential, List<String[]> tableData) 
            throws IOException, IllegalStateException, IllegalArgumentException {
        
        if (selectedRow < 0) {
            throw new IllegalStateException("Debe seleccionar un cliente.");
        }

        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }

        String[] customer = tableData.get(selectedRow);
        customer[1] = newName.trim();
        customer[2] = String.valueOf(isPreferential);
        
        syncFileData(tableData);
    }

    public List<String[]> getAllCustomers() throws IOException {
        return fileManager.loadCustomersRaw();
    }

    private void validateCustomerData(String id, String name) throws IllegalArgumentException {
        if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Por favor complete todos los campos");
        }
    }

    private void checkDuplicateId(String id, List<String[]> currentData) throws IllegalArgumentException {
        for (String[] row : currentData) {
            if (row[0].equals(id)) {
                throw new IllegalArgumentException("El ID ya se encuentra registrado.");
            }
        }
    }

    private void syncFileData(List<String[]> data) throws IOException {
        fileManager.overwriteCustomers(data);
    }
    
    public FileManager getFileManager() {
    return this.fileManager;
}
}
