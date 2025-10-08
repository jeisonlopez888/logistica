package co.edu.uniquindio.logistica.util;

import co.edu.uniquindio.logistica.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportUtil {

    // 🔹 Formateador de fecha para nombres y contenido
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /** Crea el directorio de salida si no existe */
    private static void ensureParentDir(String rutaArchivo) {
        File f = new File(rutaArchivo).getAbsoluteFile();
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    /** Evita valores nulos */
    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /** Añade la fecha al nombre del archivo */
    public static String agregarFechaNombre(String nombreBase, boolean esExcel) {
        String extension = esExcel ? ".xlsx" : ".pdf";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm"));
        return nombreBase.replace(" ", "_").toLowerCase() + "_" + fecha + extension;
    }

    // ------------------------------------------------------------
    // ---------------------- EXCEL -------------------------------
    // ------------------------------------------------------------

    public static void exportarUsuariosExcel(List<Usuario> usuarios, String rutaArchivo) throws IOException {
        ensureParentDir(rutaArchivo);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Email");
        header.createCell(3).setCellValue("Teléfono");
        header.createCell(4).setCellValue("Administrador");
        header.createCell(5).setCellValue("Fecha Exportación");

        int rowIndex = 1;
        for (Usuario u : usuarios) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(u.getId() != null ? u.getId() : 0);
            row.createCell(1).setCellValue(safe(u.getNombre()));
            row.createCell(2).setCellValue(safe(u.getEmail()));
            row.createCell(3).setCellValue(safe(u.getTelefono()));
            row.createCell(4).setCellValue(u.isAdmin() ? "Sí" : "No");
            row.createCell(5).setCellValue(LocalDateTime.now().format(DATE_FORMAT));
        }

        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    public static void exportarEnviosExcel(List<Envio> envios, String rutaArchivo) throws IOException {
        ensureParentDir(rutaArchivo);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Envíos");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Usuario");
        header.createCell(2).setCellValue("Origen");
        header.createCell(3).setCellValue("Destino");
        header.createCell(4).setCellValue("Peso (Kg)");
        header.createCell(5).setCellValue("Estado");
        header.createCell(6).setCellValue("Fecha Creación");
        header.createCell(7).setCellValue("Fecha Entrega");

        int rowIndex = 1;
        for (Envio e : envios) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(e.getId() != null ? e.getId() : 0);
            row.createCell(1).setCellValue(e.getUsuario() != null ? safe(e.getUsuario().getNombre()) : "");
            row.createCell(2).setCellValue(e.getOrigen() != null ? safe(e.getOrigen().toString()) : "");
            row.createCell(3).setCellValue(e.getDestino() != null ? safe(e.getDestino().toString()) : "");
            row.createCell(4).setCellValue(e.getPeso());
            row.createCell(5).setCellValue(e.getEstado() != null ? e.getEstado().name() : "");
            row.createCell(6).setCellValue(e.getFechaCreacionStr());
            row.createCell(7).setCellValue(e.getFechaEntregaStr());
        }

        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    public static void exportarPagosExcel(List<Pago> pagos, String rutaArchivo) throws IOException {
        ensureParentDir(rutaArchivo);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pagos");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("ID Envío");
        header.createCell(2).setCellValue("Monto Pagado");
        header.createCell(3).setCellValue("Completado");
        header.createCell(4).setCellValue("Fecha Pago");

        int rowIndex = 1;
        for (Pago p : pagos) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(p.getId() != null ? p.getId() : 0);
            row.createCell(1).setCellValue(p.getEnvio() != null && p.getEnvio().getId() != null ? p.getEnvio().getId() : 0);
            row.createCell(2).setCellValue(p.getMontoPagado());
            row.createCell(3).setCellValue(p.isCompletado() ? "Sí" : "No");
            row.createCell(4).setCellValue(p.getFechaPago() != null ? p.getFechaPago().format(DATE_FORMAT) : "");
        }

        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    public static void exportarRepartidoresExcel(List<Repartidor> repartidores, String rutaArchivo) throws IOException {
        ensureParentDir(rutaArchivo);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Repartidores");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Teléfono");
        header.createCell(3).setCellValue("Zona");
        header.createCell(4).setCellValue("Disponible");
        header.createCell(5).setCellValue("Fecha Exportación");

        int rowIndex = 1;
        for (Repartidor r : repartidores) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(r.getId() != null ? r.getId() : 0);
            row.createCell(1).setCellValue(safe(r.getNombre()));
            row.createCell(2).setCellValue(safe(r.getTelefono()));
            row.createCell(3).setCellValue(safe(r.getZona()));
            row.createCell(4).setCellValue(r.isDisponible() ? "Sí" : "No");
            row.createCell(5).setCellValue(LocalDateTime.now().format(DATE_FORMAT));
        }

        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    public static void exportarReporteGeneralExcel(
            List<Usuario> usuarios,
            List<Envio> envios,
            List<Pago> pagos,
            List<Repartidor> repartidores,
            String rutaArchivo) throws IOException {

        ensureParentDir(rutaArchivo);
        Workbook workbook = new XSSFWorkbook();

        // Secciones
        exportarUsuariosSheet(workbook, usuarios);
        exportarEnviosSheet(workbook, envios);
        exportarPagosSheet(workbook, pagos);
        exportarRepartidoresSheet(workbook, repartidores);

        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    private static void exportarUsuariosSheet(Workbook workbook, List<Usuario> usuarios) {
        Sheet s = workbook.createSheet("Usuarios");
        Row h = s.createRow(0);
        h.createCell(0).setCellValue("ID");
        h.createCell(1).setCellValue("Nombre");
        h.createCell(2).setCellValue("Email");
        h.createCell(3).setCellValue("Teléfono");
        h.createCell(4).setCellValue("Admin");
        int ri = 1;
        for (Usuario u : usuarios) {
            Row r = s.createRow(ri++);
            r.createCell(0).setCellValue(u.getId() != null ? u.getId() : 0);
            r.createCell(1).setCellValue(safe(u.getNombre()));
            r.createCell(2).setCellValue(safe(u.getEmail()));
            r.createCell(3).setCellValue(safe(u.getTelefono()));
            r.createCell(4).setCellValue(u.isAdmin() ? "Sí" : "No");
        }
    }

    private static void exportarEnviosSheet(Workbook workbook, List<Envio> envios) {
        Sheet s = workbook.createSheet("Envíos");
        Row h = s.createRow(0);
        h.createCell(0).setCellValue("ID");
        h.createCell(1).setCellValue("Usuario");
        h.createCell(2).setCellValue("Peso");
        h.createCell(3).setCellValue("Estado");
        h.createCell(4).setCellValue("Fecha Creación");
        h.createCell(5).setCellValue("Fecha Entrega");
        int ri = 1;
        for (Envio e : envios) {
            Row r = s.createRow(ri++);
            r.createCell(0).setCellValue(e.getId() != null ? e.getId() : 0);
            r.createCell(1).setCellValue(e.getUsuario() != null ? safe(e.getUsuario().getNombre()) : "");
            r.createCell(2).setCellValue(e.getPeso());
            r.createCell(3).setCellValue(e.getEstado() != null ? e.getEstado().name() : "");
            r.createCell(4).setCellValue(e.getFechaCreacionStr());
            r.createCell(5).setCellValue(e.getFechaEntregaStr());
        }
    }

    private static void exportarPagosSheet(Workbook workbook, List<Pago> pagos) {
        Sheet s = workbook.createSheet("Pagos");
        Row h = s.createRow(0);
        h.createCell(0).setCellValue("ID");
        h.createCell(1).setCellValue("Envío");
        h.createCell(2).setCellValue("Monto Pagado");
        h.createCell(3).setCellValue("Completado");
        h.createCell(4).setCellValue("Fecha");
        int ri = 1;
        for (Pago p : pagos) {
            Row r = s.createRow(ri++);
            r.createCell(0).setCellValue(p.getId() != null ? p.getId() : 0);
            r.createCell(1).setCellValue(p.getEnvio() != null && p.getEnvio().getId() != null ? p.getEnvio().getId() : 0);
            r.createCell(2).setCellValue(p.getMontoPagado());
            r.createCell(3).setCellValue(p.isCompletado() ? "Sí" : "No");
            r.createCell(4).setCellValue(p.getFechaPago() != null ? p.getFechaPago().format(DATE_FORMAT) : "");
        }
    }

    private static void exportarRepartidoresSheet(Workbook workbook, List<Repartidor> repartidores) {
        Sheet s = workbook.createSheet("Repartidores");
        Row h = s.createRow(0);
        h.createCell(0).setCellValue("ID");
        h.createCell(1).setCellValue("Nombre");
        h.createCell(2).setCellValue("Teléfono");
        h.createCell(3).setCellValue("Zona");
        h.createCell(4).setCellValue("Disponible");
        int ri = 1;
        for (Repartidor r : repartidores) {
            Row row = s.createRow(ri++);
            row.createCell(0).setCellValue(r.getId() != null ? r.getId() : 0);
            row.createCell(1).setCellValue(safe(r.getNombre()));
            row.createCell(2).setCellValue(safe(r.getTelefono()));
            row.createCell(3).setCellValue(safe(r.getZona()));
            row.createCell(4).setCellValue(r.isDisponible() ? "Sí" : "No");
        }
    }

    // ------------------------------------------------------------
    // ----------------------- PDF -------------------------------
    // ------------------------------------------------------------

    private static void iniciarPDFCabecera(PDPageContentStream content, String titulo) throws IOException {
        content.setFont(PDType1Font.HELVETICA_BOLD, 14);
        content.beginText();
        content.newLineAtOffset(50, 750);
        content.showText(titulo + " - " + LocalDateTime.now().format(DATE_FORMAT));
        content.newLineAtOffset(0, -25);
        content.setFont(PDType1Font.HELVETICA, 10);
    }

    public static void exportarUsuariosPDF(List<Usuario> usuarios, String rutaArchivo) throws IOException {
        ensureParentDir(rutaArchivo);
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            iniciarPDFCabecera(content, "Reporte de Usuarios");

            for (Usuario u : usuarios) {
                content.showText("ID: " + u.getId() + " | " + safe(u.getNombre()) + " | " + safe(u.getEmail()));
                content.newLineAtOffset(0, -15);
            }

            content.endText();
            content.close();
            doc.save(rutaArchivo);
        }
    }

    public static void exportarEnviosPDF(List<Envio> envios, String rutaArchivo) throws IOException {
        ensureParentDir(rutaArchivo);
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            iniciarPDFCabecera(content, "Reporte de Envíos");

            for (Envio e : envios) {
                content.showText("ID: " + e.getId() + " | Usuario: " +
                        (e.getUsuario() != null ? safe(e.getUsuario().getNombre()) : "") +
                        " | Estado: " + (e.getEstado() != null ? e.getEstado().name() : ""));
                content.newLineAtOffset(0, -15);
            }

            content.endText();
            content.close();
            doc.save(rutaArchivo);
        }
    }

    public static void exportarPagosPDF(List<Pago> pagos, String rutaArchivo) throws IOException {
        ensureParentDir(rutaArchivo);
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            iniciarPDFCabecera(content, "Reporte de Pagos");

            for (Pago p : pagos) {
                content.showText("ID: " + p.getId() + " | Monto: " + p.getMontoPagado() +
                        " | Fecha: " + (p.getFechaPago() != null ? p.getFechaPago().format(DATE_FORMAT) : ""));
                content.newLineAtOffset(0, -15);
            }

            content.endText();
            content.close();
            doc.save(rutaArchivo);
        }
    }

    public static void exportarRepartidoresPDF(List<Repartidor> repartidores, String rutaArchivo) throws IOException {
        ensureParentDir(rutaArchivo);
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            iniciarPDFCabecera(content, "Reporte de Repartidores");

            for (Repartidor r : repartidores) {
                content.showText("ID: " + r.getId() + " | " + safe(r.getNombre()) +
                        " | Zona: " + safe(r.getZona()) +
                        " | Disponible: " + (r.isDisponible() ? "Sí" : "No"));
                content.newLineAtOffset(0, -15);
            }

            content.endText();
            content.close();
            doc.save(rutaArchivo);
        }
    }

    public static void exportarReporteGeneralPDF(
            List<Usuario> usuarios,
            List<Envio> envios,
            List<Pago> pagos,
            List<Repartidor> repartidores,
            String rutaArchivo) throws IOException {

        ensureParentDir(rutaArchivo);
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        PDPageContentStream content = new PDPageContentStream(doc, page);
        content.setFont(PDType1Font.HELVETICA_BOLD, 14);
        content.beginText();
        content.newLineAtOffset(50, 750);
        content.showText("Reporte General de Logística - " +
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        content.newLineAtOffset(0, -20);

        content.setFont(PDType1Font.HELVETICA, 12);
        content.showText("Usuarios: " + usuarios.size() +
                " | Envíos: " + envios.size() +
                " | Pagos: " + pagos.size() +
                " | Repartidores: " + repartidores.size());
        content.newLineAtOffset(0, -30);

        // ----- Usuarios -----
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.showText("Usuarios:");
        content.setFont(PDType1Font.HELVETICA, 11);
        content.newLineAtOffset(0, -15);
        for (Usuario u : usuarios) {
            content.showText("Usuario: " + safe(u.getNombre()));
            content.newLineAtOffset(0, -15);
        }

        // ----- Envíos -----
        content.newLineAtOffset(0, -10);
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.showText("Envíos:");
        content.setFont(PDType1Font.HELVETICA, 11);
        content.newLineAtOffset(0, -15);
        for (Envio e : envios) {
            content.showText("Envio: #" + e.getId() + " - " + e.getEstado());
            content.newLineAtOffset(0, -15);
        }

        // ----- Pagos -----
        content.newLineAtOffset(0, -10);
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.showText("Pagos:");
        content.setFont(PDType1Font.HELVETICA, 11);
        content.newLineAtOffset(0, -15);
        for (Pago p : pagos) {
            String pagoStr = "Pago #" + (p.getId() != null ? p.getId() : "") +
                    " | Envío: " + (p.getEnvio() != null && p.getEnvio().getId() != null ? p.getEnvio().getId() : "N/A") +
                    " | Monto: $" + p.getMontoPagado() +
                    " | Fecha: " + (p.getFechaPago() != null ? p.getFechaPago().toString() : "N/A") +
                    " | Estado: " + (p.isCompletado() ? "Completado" : "Pendiente");
            content.showText(pagoStr);
            content.newLineAtOffset(0, -15);
        }

        // ----- Repartidores -----
        content.newLineAtOffset(0, -10);
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.showText("Repartidores:");
        content.setFont(PDType1Font.HELVETICA, 11);
        content.newLineAtOffset(0, -15);
        for (Repartidor r : repartidores) {
            content.showText("Repartidor: " + safe(r.getNombre()) +
                    " (" + safe(r.getZona()) + ")");
            content.newLineAtOffset(0, -15);
        }

        content.endText();
        content.close();
        doc.save(rutaArchivo);
        doc.close();
    }

}



