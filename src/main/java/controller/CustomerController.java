package controller;

import view.CustomerFrame;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import javax.swing.JOptionPane;

public class CustomerController {

    private CustomerFrame view;
    private final String CUSTOMERS_FILE_PATH = "customers.txt";

    public CustomerController(CustomerFrame view) {

        this.view = view;

        loadExistingCustomers();

        this.view.addSaveListener(e -> {
            saveCustomer();
        });
    }

    private void saveCustomer() {

        String id = view.getId();
        String name = view.getName();

        boolean isPreferential = view.isPreferential();

        if (id.isEmpty() || name.isEmpty()) {

            JOptionPane.showMessageDialog(view, "Por favor complete todos los campos");

            return;
        }

        String line = id + "," + name + "," + isPreferential;

        try {

            Files.write(Paths.get(CUSTOMERS_FILE_PATH), (line + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            view.getTableModel().addRow(new Object[]{id, name, isPreferential ? "Sí" : "No"});
            view.clearFields();

            JOptionPane.showMessageDialog(view, "Cliente guardado exitosamente");

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(view, "Error al acceder al archivo de clientes");

        }
    }

    private void loadExistingCustomers() {

        try {
            Path path = Paths.get(CUSTOMERS_FILE_PATH);

            if (!Files.exists(path)) {
                return;
            }

            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {

                String[] data = line.split(",");

                if (data.length == 3) {

                    String prefLabel = Boolean.parseBoolean(data[2]) ? "Sí" : "No";
                    view.getTableModel().addRow(new Object[]{data[0], data[1], prefLabel});

                }
            }
        } catch (IOException e) {

            System.err.println("Archivo de clientes no encontrado, se creará uno nuevo al guardar.");

        }
    }
}
