/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.entities.ParkingLot;
import model.entities.ParkingSpace;
import model.data.FileManager;

/**
 *
 * @author Caleb Murillo
 */
public class ParkingController {

    private ParkingLot tempParking; // Objeto temporal mientras se configura
    private int spacesConfigured = 0;
    private int prefSpacesAssigned = 0;
    
    public ParkingController() {
    }

    // RF-05: Crear la base del parqueo 
    public void prepareParking(String name, int total, int pref) throws Exception {
    if (name.trim().isEmpty()) throw new Exception("Error: El nombre no puede estar vacío."); // .trim() para evitar solo espacios
    if (total <= 0) throw new Exception("Error: La cantidad de espacios debe ser mayor a 0.");
    if (pref > total) throw new Exception("Error: La cuota de discapacidad no puede superar el total.");

    tempParking = new ParkingLot();
    tempParking.setName(name);
    tempParking.setNumberOfSpaces(total);
    tempParking.setPreferentialSpaces(pref);
    tempParking.setSpaces(new ParkingSpace[total]);
    this.spacesConfigured = 0;
    this.prefSpacesAssigned = 0;
}

    // Configuración dinámica de bloques de espacios
    public void configureSpaceBlock(int qty, boolean isPref, String type) throws Exception {
    int remaining = getRemainingSpaces(); // Usamos tus getters para consistencia
    int prefRemaining = getPrefRemaining();

    // 1. REGLA FÍSICA: No puedes configurar lo que no existe
    if (qty > remaining) {
        throw new Exception("Error: No puedes configurar más espacios de los restantes (" + remaining + ").");
    }

    // 2. REGLA DE PREFERENCIALES: No exceder la cuota definida al inicio
    if (isPref && qty > prefRemaining) {
        throw new Exception("Error: Solo quedan " + prefRemaining + " espacios de discapacidad por asignar.");
    }

    // 3. REGLA DE RESERVA (LA MÁS IMPORTANTE):
    // Si NO es preferencial, no puedes usar tantos espacios que dejes al sistema
    // sin capacidad física para cumplir con los preferenciales que faltan.
    if (!isPref && (remaining - qty) < prefRemaining) {
        throw new Exception("Error: Debes reservar " + prefRemaining + 
                           " espacios para discapacidad. Reduce la cantidad de espacios normales.");
    }

    // 4. LÓGICA DE LLENADO (ATÓMICA)
    for (int i = 0; i < qty; i++) {
        ParkingSpace newSpace = new ParkingSpace();
        newSpace.setSpaceNumber(spacesConfigured + 1);
        newSpace.setVehicleTypeSupported(type);
        newSpace.setIsPreferential(isPref);
        newSpace.setIsOccupied(false);

        tempParking.getSpaces()[spacesConfigured] = newSpace;
        
        // El contador de espacios totales configurados sube uno a uno para el índice
        spacesConfigured++;
    }

    // 5. ACTUALIZACIÓN DE CONTADOR PREFERENCIAL
    if (isPref) {
        prefSpacesAssigned += qty;
    }  
}
    
    public void validateAndSave() throws Exception {
    // 1. Validación de espacios totales (la que ya tienes)
    if (spacesConfigured < tempParking.getNumberOfSpaces()) {
        throw new Exception("Aún quedan espacios por configurar.");
    }

    // 2. NUEVA VALIDACIÓN: Cuota de discapacidad
    int totalPrefRequired = tempParking.getPreferentialSpaces();
    if (prefSpacesAssigned < totalPrefRequired) {
        int missing = totalPrefRequired - prefSpacesAssigned;
        throw new Exception("Error: Debes asignar los " + missing + 
                           " espacios para discapacidad restantes antes de finalizar.");
    }
}

    public int getRemainingSpaces() {
        return tempParking.getNumberOfSpaces() - spacesConfigured;
    }

    public boolean isConfigFinished() {
        return spacesConfigured == tempParking.getNumberOfSpaces();
    }

    public ParkingLot getTempParking() {
        return this.tempParking;
    }

   public int getPrefRemaining() {
    // 1. Cuota inicial definida por el Admin
    int cuotaTotalPref = tempParking.getPreferentialSpaces();
    
    // 2. REGLA DE ORO: Restar la variable que REALMENTE incrementamos
    // Cambiamos 'prefSpacesConfigured' por 'prefSpacesAssigned'
    return cuotaTotalPref - this.prefSpacesAssigned; 
}
   
   public void prepareForEditing(ParkingLot parking) {
    this.tempParking = parking;
    // Reseteamos contadores para la nueva configuración de bloques
    this.spacesConfigured = 0;
    this.prefSpacesAssigned = 0;
    // Limpiamos el arreglo de espacios para sobreescribirlo
    this.tempParking.setSpaces(new ParkingSpace[parking.getNumberOfSpaces()]);
}

}
