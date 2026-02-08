package controller;

import view.CustomerFrame;
import model.data.FileManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerController {

    private CustomerFrame view;
    private FileManager fileManager;

    public CustomerController(CustomerFrame view) {
        this.view = view;
        this.fileManager = new FileManager();
    }

    // Logic for customer registration
    public void processRegistration() throws IOException, IllegalArgumentException {
        String id = view.getId().trim();
        String name = view.getName().trim();

        if (id.isEmpty() || name.isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        if (isIdDuplicate(id)) {
            throw new IllegalArgumentException("El ID ya se encuentra registrado.");
        }

        fileManager.saveCustomer(id, name, view.isPreferential());
        view.getTableModel().addRow(new Object[]{id, name, view.isPreferential() ? "Sí" : "No"});
        view.clearFields();
    }

    // Logic for customer deletion
    public void processDeletion() throws IOException, IllegalStateException {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            throw new IllegalStateException("Debe seleccionar un cliente.");
        }

        view.getTableModel().removeRow(row);
        syncFileData();
        view.clearFields();
    }

    // Logic for customer update with validation
    public void processUpdate() throws IOException, IllegalStateException, IllegalArgumentException {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            throw new IllegalStateException("Debe seleccionar un cliente.");
        }

        String updatedName = view.getName().trim();

        // Validation: prevent empty name
        if (updatedName.isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }

        view.getTableModel().setValueAt(updatedName, row, 1);
        view.getTableModel().setValueAt(view.isPreferential() ? "Sí" : "No", row, 2);

        syncFileData();
        view.clearFields();
    }

    // Check for existing IDs in table
    private boolean isIdDuplicate(String id) {
        for (int i = 0; i < view.getTableModel().getRowCount(); i++) {
            if (view.getTableModel().getValueAt(i, 0).toString().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // Sync JTable data with TXT file
    private void syncFileData() throws IOException {
        List<String[]> data = new ArrayList<>();
        for (int i = 0; i < view.getTableModel().getRowCount(); i++) {
            String id = view.getTableModel().getValueAt(i, 0).toString();
            String name = view.getTableModel().getValueAt(i, 1).toString();
            String pref = view.getTableModel().getValueAt(i, 2).toString().equals("Sí") ? "true" : "false";
            data.add(new String[]{id, name, pref});
        }
        fileManager.overwriteCustomers(data);
    }

    // Initial data load from file
    public void initDataLoad() throws IOException {
        List<String[]> customers = fileManager.loadCustomersRaw();
        for (String[] d : customers) {
            view.getTableModel().addRow(new Object[]{d[0], d[1], d[2].equals("true") ? "Sí" : "No"});
        }
    }
}
