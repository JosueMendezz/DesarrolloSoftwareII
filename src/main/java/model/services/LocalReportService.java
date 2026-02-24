package model.services;

import model.data.FileManager;
import model.entities.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

public class LocalReportService {

    private final FileManager fileManager;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalReportService(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void generateLocalPDF(String parkingName, User operator) {
        try {
            boolean isAllSedes = parkingName.equalsIgnoreCase("TODOS");

            // 1. OBTENER DATOS DE INFRAESTRUCTURA (Sedes y capacidades)
            List<String[]> allParkings = fileManager.readAllParkingLines().stream()
                    .map(l -> l.split("\\|"))
                    .collect(Collectors.toList());

            // Capacidad total (Suma de todas si es global, o solo la seleccionada)
            int capacidadTotal = isAllSedes
                    ? allParkings.stream().mapToInt(p -> Integer.parseInt(p[1])).sum()
                    : allParkings.stream().filter(p -> p[0].equalsIgnoreCase(parkingName))
                            .mapToInt(p -> Integer.parseInt(p[1])).findFirst().orElse(0);

            // Definimos exactamente qué sedes vamos a iterar para los cuadros de KPI
            List<String[]> sedesAProcesar = isAllSedes ? allParkings
                    : allParkings.stream().filter(p -> p[0].equalsIgnoreCase(parkingName)).collect(Collectors.toList());

            // 2. OBTENER OCUPACIÓN ACTUAL
            List<String[]> currentVehicles = fileManager.loadAllParkedVehicles().stream()
                    .filter(v -> isAllSedes || v[9].equalsIgnoreCase(parkingName))
                    .collect(Collectors.toList());

            // 3. OBTENER HISTÓRICO Y ASOCIAR DUEÑOS
            List<String[]> historyRaw = fileManager.readLinesFromFile("history.txt").stream()
                    .map(l -> l.split("\\|"))
                    .filter(h -> h.length >= 12)
                    .filter(h -> isAllSedes || h[11].equalsIgnoreCase(parkingName))
                    .collect(Collectors.toList());

            List<String[]> historyWithNames = historyRaw.stream().map(row -> {
                String ownerId = row[2];
                String ownerName = fileManager.getCustomerNameById(ownerId);
                row[2] = ownerName; // Sustitución ID -> Nombre
                return row;
            }).collect(Collectors.toList());

            // 4. CÁLCULOS GENERALES
            double totalRecaudado = historyWithNames.stream()
                    .mapToDouble(h -> Double.parseDouble(h[9].replace(",", ".")))
                    .sum();

            String[] sedeDataSimulada = {parkingName, String.valueOf(capacidadTotal)};
            buildPdfDocument(parkingName, operator, sedeDataSimulada, currentVehicles, historyWithNames, totalRecaudado, sedesAProcesar);

        } catch (Exception e) {
            System.err.println("Error en reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void buildPdfDocument(String parkingName, User operator, String[] sedeData,
            List<String[]> currentVehicles, List<String[]> history, double totalRecaudado, List<String[]> sedesAProcesar) {

        String dest = "Reporte_" + parkingName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";

        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            DeviceRgb heapBlue = new DeviceRgb(33, 150, 243);
            DeviceRgb darkGrey = new DeviceRgb(40, 40, 40);
            boolean isGlobal = parkingName.equalsIgnoreCase("TODOS");

            // --- ENCABEZADO ---
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();
            headerTable.addCell(new Cell().add(new Paragraph("HEAP HAVEN").setFontSize(24).setBold().setFontColor(heapBlue)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            headerTable.addCell(new Cell().add(new Paragraph(isGlobal ? "REPORTE GLOBAL\nADMINISTRATIVO" : "REPORTE LOCAL\nOPERATIVO").setTextAlignment(TextAlignment.RIGHT).setFontSize(10).setFontColor(ColorConstants.GRAY)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            document.add(headerTable);

            document.add(new Paragraph("ÁMBITO: " + parkingName.toUpperCase()).setBold().setFontSize(14).setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(heapBlue, 1)));
            document.add(new Paragraph("Generado por: " + operator.getFullName() + " | Fecha: " + LocalDateTime.now().format(dtf)).setFontSize(9));
            document.add(new Paragraph("\n"));

            // --- PANEL DE MÉTRICAS POR SEDE ---
            for (String[] sede : sedesAProcesar) {
                String nombreSede = sede[0];
                String capacidadSede = sede[1];

                long ocupacionSede = currentVehicles.stream().filter(v -> v[9].equalsIgnoreCase(nombreSede)).count();
                double recaudadoSede = history.stream()
                        .filter(h -> h[11].equalsIgnoreCase(nombreSede))
                        .mapToDouble(h -> Double.parseDouble(h[9].replace(",", "."))).sum();
                long salidasSede = history.stream().filter(h -> h[11].equalsIgnoreCase(nombreSede)).count();

                if (isGlobal) {
                    document.add(new Paragraph("SEDE: " + nombreSede.toUpperCase()).setBold().setFontSize(10).setFontColor(heapBlue));
                }

                Table kpiTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25})).useAllAvailableWidth();
                kpiTable.setMarginBottom(10);
                addKpiCell(kpiTable, "CAPACIDAD", capacidadSede, darkGrey);
                addKpiCell(kpiTable, "OCUPACIÓN", String.valueOf(ocupacionSede), ColorConstants.BLACK);
                addKpiCell(kpiTable, "SALIDAS", String.valueOf(salidasSede), ColorConstants.BLACK);
                addKpiCell(kpiTable, "RECAUDADO", "₡" + String.format("%.2f", recaudadoSede), new DeviceRgb(76, 175, 80));
                document.add(kpiTable);
            }

            // --- RESUMEN GLOBAL (Solo si es TODOS) ---
            if (isGlobal) {
                document.add(new Paragraph("RESUMEN CONSOLIDADO").setBold().setFontSize(11).setFontColor(darkGrey).setMarginTop(10));
                Table totalTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
                addKpiCell(totalTable, "VEHÍCULOS TOTALES EN SISTEMA", String.valueOf(currentVehicles.size()), heapBlue);
                addKpiCell(totalTable, "RECAUDACIÓN TOTAL GLOBAL", "₡" + String.format("%.2f", totalRecaudado), new DeviceRgb(76, 175, 80));
                document.add(totalTable);
            }

            document.add(new Paragraph("\n"));

            // --- TABLA DE TRANSACCIONES ---
            document.add(new Paragraph("HISTORIAL DE TRANSACCIONES").setBold().setFontSize(12).setFontColor(heapBlue));
            String midHeader = isGlobal ? "Sede" : "Tipo de Vehículo";
            Table table = new Table(UnitValue.createPercentArray(new float[]{12, 18, 15, 15, 15, 13, 12})).useAllAvailableWidth();

            String[] headers = {"Placa", "Dueño", midHeader, "Entrada", "Salida", "Monto", "Cajero"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setBold().setFontColor(ColorConstants.WHITE)).setBackgroundColor(darkGrey).setTextAlignment(TextAlignment.CENTER));
            }

            for (String[] row : history) {
                table.addCell(new Cell().add(new Paragraph(row[1])).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(row[2])).setTextAlignment(TextAlignment.LEFT));

                String midValue = isGlobal ? row[11] : row[3];
                Paragraph pMid = new Paragraph(midValue);
                if (isGlobal) {
                    pMid.setFontColor(heapBlue).setBold();
                }
                table.addCell(new Cell().add(pMid).setTextAlignment(TextAlignment.CENTER));

                table.addCell(new Cell().add(new Paragraph(row[7].substring(11))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(row[0].substring(11))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph("₡" + row[9])).setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(row[10])).setTextAlignment(TextAlignment.CENTER));
            }
            document.add(table);

            document.add(new Paragraph("\n\n-- Fin del Reporte Oficial Heap Haven --").setTextAlignment(TextAlignment.CENTER).setFontSize(8).setFontColor(ColorConstants.GRAY));
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addKpiCell(Table table, String label, String value, com.itextpdf.kernel.colors.Color color) {
        Cell cell = new Cell().add(new Paragraph(label).setFontSize(8).setFontColor(ColorConstants.GRAY))
                .add(new Paragraph(value).setFontSize(14).setBold().setFontColor(color))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        table.addCell(cell);
    }
}
