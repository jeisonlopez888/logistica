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
import java.util.Map;

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
        header.createCell(5).setCellValue("Dirección 1 (Calle)");
        header.createCell(6).setCellValue("Zona 1");
        header.createCell(7).setCellValue("Dirección 2 (Calle)");
        header.createCell(8).setCellValue("Zona 2");
        header.createCell(9).setCellValue("Métodos de Pago");
        header.createCell(10).setCellValue("Fecha Exportación");

        int rowIndex = 1;
        for (Usuario u : usuarios) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(u.getId() != null ? u.getId() : 0);
            row.createCell(1).setCellValue(safe(u.getNombre()));
            row.createCell(2).setCellValue(safe(u.getEmail()));
            row.createCell(3).setCellValue(safe(u.getTelefono()));
            row.createCell(4).setCellValue(u.isAdmin() ? "Sí" : "No");
            
            // Direcciones
            String dir1Calle = "";
            String dir1Zona = "";
            String dir2Calle = "";
            String dir2Zona = "";
            if (u.getDirecciones() != null && !u.getDirecciones().isEmpty()) {
                if (u.getDirecciones().size() > 0) {
                    Direccion d1 = u.getDirecciones().get(0);
                    dir1Calle = safe(d1.getCalle());
                    dir1Zona = safe(d1.getCiudad());
                }
                if (u.getDirecciones().size() > 1) {
                    Direccion d2 = u.getDirecciones().get(1);
                    dir2Calle = safe(d2.getCalle());
                    dir2Zona = safe(d2.getCiudad());
                }
            }
            row.createCell(5).setCellValue(dir1Calle);
            row.createCell(6).setCellValue(dir1Zona);
            row.createCell(7).setCellValue(dir2Calle);
            row.createCell(8).setCellValue(dir2Zona);
            
            // Métodos de pago
            String metodosPago = "";
            if (u.getMetodosPago() != null && !u.getMetodosPago().isEmpty()) {
                metodosPago = u.getMetodosPago().stream()
                        .map(Enum::name)
                        .collect(java.util.stream.Collectors.joining(", "));
            }
            row.createCell(9).setCellValue(metodosPago);
            row.createCell(10).setCellValue(LocalDateTime.now().format(DATE_FORMAT));
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
        header.createCell(2).setCellValue("Origen (Calle)");
        header.createCell(3).setCellValue("Zona Origen");
        header.createCell(4).setCellValue("Destino (Calle)");
        header.createCell(5).setCellValue("Zona Destino");
        header.createCell(6).setCellValue("Peso (Kg)");
        header.createCell(7).setCellValue("Volumen (m³)");
        header.createCell(8).setCellValue("Prioridad");
        header.createCell(9).setCellValue("Seguro");
        header.createCell(10).setCellValue("Frágil");
        header.createCell(11).setCellValue("Firma Requerida");
        header.createCell(12).setCellValue("Costo Estimado");
        header.createCell(13).setCellValue("Estado");
        header.createCell(14).setCellValue("Repartidor");
        header.createCell(15).setCellValue("Fecha Creación");
        header.createCell(16).setCellValue("Fecha Confirmación");
        header.createCell(17).setCellValue("Fecha Entrega");
        header.createCell(18).setCellValue("Incidencia");

        int rowIndex = 1;
        for (Envio e : envios) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(e.getId() != null ? e.getId() : 0);
            row.createCell(1).setCellValue(e.getUsuario() != null ? safe(e.getUsuario().getNombre()) : "");
            
            // Origen
            String origenCalle = e.getOrigen() != null ? safe(e.getOrigen().getCalle()) : "";
            String origenZona = e.getOrigen() != null ? safe(e.getOrigen().getCiudad()) : "";
            row.createCell(2).setCellValue(origenCalle);
            row.createCell(3).setCellValue(origenZona);
            
            // Destino
            String destinoCalle = e.getDestino() != null ? safe(e.getDestino().getCalle()) : "";
            String destinoZona = e.getDestino() != null ? safe(e.getDestino().getCiudad()) : "";
            row.createCell(4).setCellValue(destinoCalle);
            row.createCell(5).setCellValue(destinoZona);
            
            row.createCell(6).setCellValue(e.getPeso());
            row.createCell(7).setCellValue(e.getVolumen());
            row.createCell(8).setCellValue(e.isPrioridad() ? "Sí" : "No");
            row.createCell(9).setCellValue(e.isSeguro() ? "Sí" : "No");
            row.createCell(10).setCellValue(e.isFragil() ? "Sí" : "No");
            row.createCell(11).setCellValue(e.isFirmaRequerida() ? "Sí" : "No");
            row.createCell(12).setCellValue(e.getCostoEstimado());
            row.createCell(13).setCellValue(e.getEstado() != null ? e.getEstado().name() : "");
            row.createCell(14).setCellValue(e.getRepartidor() != null ? safe(e.getRepartidor().getNombre()) : "");
            row.createCell(15).setCellValue(e.getFechaCreacionStr());
            row.createCell(16).setCellValue(e.getFechaConfirmacionStr());
            row.createCell(17).setCellValue(e.getFechaEntregaStr());
            row.createCell(18).setCellValue(safe(e.getIncidenciaDescripcion()));
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
        header.createCell(2).setCellValue("Usuario");
        header.createCell(3).setCellValue("Monto Pagado");
        header.createCell(4).setCellValue("Método de Pago");
        header.createCell(5).setCellValue("Completado");
        header.createCell(6).setCellValue("Fecha Pago");
        header.createCell(7).setCellValue("Peso (Kg)");
        header.createCell(8).setCellValue("Volumen (m³)");
        header.createCell(9).setCellValue("Costo Base");
        header.createCell(10).setCellValue("Costo Final");

        int rowIndex = 1;
        for (Pago p : pagos) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(p.getId() != null ? p.getId() : 0);
            row.createCell(1).setCellValue(p.getEnvio() != null && p.getEnvio().getId() != null ? p.getEnvio().getId() : 0);
            row.createCell(2).setCellValue(p.getEnvio() != null && p.getEnvio().getUsuario() != null 
                    ? safe(p.getEnvio().getUsuario().getNombre()) : "");
            row.createCell(3).setCellValue(p.getMontoPagado());
            row.createCell(4).setCellValue(p.getMetodo() != null ? p.getMetodo().name() : "");
            row.createCell(5).setCellValue(p.isConfirmado() ? "Sí" : "No");
            row.createCell(6).setCellValue(p.getFechaPago() != null ? DATE_FORMAT.format(p.getFechaPago()) : "");
            row.createCell(7).setCellValue(p.getPeso());
            row.createCell(8).setCellValue(p.getVolumen());
            row.createCell(9).setCellValue(p.getCostoBase());
            row.createCell(10).setCellValue(p.getCostoFinal());
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
        h.createCell(5).setCellValue("Dirección 1 (Calle)");
        h.createCell(6).setCellValue("Zona 1");
        h.createCell(7).setCellValue("Dirección 2 (Calle)");
        h.createCell(8).setCellValue("Zona 2");
        h.createCell(9).setCellValue("Métodos de Pago");

        int ri = 1;
        for (Usuario u : usuarios) {
            Row r = s.createRow(ri++);
            r.createCell(0).setCellValue(u.getId() != null ? u.getId() : 0);
            r.createCell(1).setCellValue(safe(u.getNombre()));
            r.createCell(2).setCellValue(safe(u.getEmail()));
            r.createCell(3).setCellValue(safe(u.getTelefono()));
            r.createCell(4).setCellValue(u.isAdmin() ? "Sí" : "No");
            
            // Direcciones
            String dir1Calle = "";
            String dir1Zona = "";
            String dir2Calle = "";
            String dir2Zona = "";
            if (u.getDirecciones() != null && !u.getDirecciones().isEmpty()) {
                if (u.getDirecciones().size() > 0) {
                    Direccion d1 = u.getDirecciones().get(0);
                    dir1Calle = safe(d1.getCalle());
                    dir1Zona = safe(d1.getCiudad());
                }
                if (u.getDirecciones().size() > 1) {
                    Direccion d2 = u.getDirecciones().get(1);
                    dir2Calle = safe(d2.getCalle());
                    dir2Zona = safe(d2.getCiudad());
                }
            }
            r.createCell(5).setCellValue(dir1Calle);
            r.createCell(6).setCellValue(dir1Zona);
            r.createCell(7).setCellValue(dir2Calle);
            r.createCell(8).setCellValue(dir2Zona);
            
            // Métodos de pago
            String metodosPago = "";
            if (u.getMetodosPago() != null && !u.getMetodosPago().isEmpty()) {
                metodosPago = u.getMetodosPago().stream()
                        .map(Enum::name)
                        .collect(java.util.stream.Collectors.joining(", "));
            }
            r.createCell(9).setCellValue(metodosPago);
        }
    }

    private static void exportarEnviosSheet(Workbook workbook, List<Envio> envios) {
        Sheet s = workbook.createSheet("Envíos");
        Row h = s.createRow(0);
        h.createCell(0).setCellValue("ID");
        h.createCell(1).setCellValue("Usuario");
        h.createCell(2).setCellValue("Origen (Calle)");
        h.createCell(3).setCellValue("Zona Origen");
        h.createCell(4).setCellValue("Destino (Calle)");
        h.createCell(5).setCellValue("Zona Destino");
        h.createCell(6).setCellValue("Peso (Kg)");
        h.createCell(7).setCellValue("Volumen (m³)");
        h.createCell(8).setCellValue("Prioridad");
        h.createCell(9).setCellValue("Seguro");
        h.createCell(10).setCellValue("Frágil");
        h.createCell(11).setCellValue("Firma Requerida");
        h.createCell(12).setCellValue("Costo Estimado");
        h.createCell(13).setCellValue("Estado");
        h.createCell(14).setCellValue("Repartidor");
        h.createCell(15).setCellValue("Fecha Creación");
        h.createCell(16).setCellValue("Fecha Confirmación");
        h.createCell(17).setCellValue("Fecha Entrega");
        h.createCell(18).setCellValue("Incidencia");
        
        int ri = 1;
        for (Envio e : envios) {
            Row r = s.createRow(ri++);
            r.createCell(0).setCellValue(e.getId() != null ? e.getId() : 0);
            r.createCell(1).setCellValue(e.getUsuario() != null ? safe(e.getUsuario().getNombre()) : "");
            
            // Origen
            String origenCalle = e.getOrigen() != null ? safe(e.getOrigen().getCalle()) : "";
            String origenZona = e.getOrigen() != null ? safe(e.getOrigen().getCiudad()) : "";
            r.createCell(2).setCellValue(origenCalle);
            r.createCell(3).setCellValue(origenZona);
            
            // Destino
            String destinoCalle = e.getDestino() != null ? safe(e.getDestino().getCalle()) : "";
            String destinoZona = e.getDestino() != null ? safe(e.getDestino().getCiudad()) : "";
            r.createCell(4).setCellValue(destinoCalle);
            r.createCell(5).setCellValue(destinoZona);
            
            r.createCell(6).setCellValue(e.getPeso());
            r.createCell(7).setCellValue(e.getVolumen());
            r.createCell(8).setCellValue(e.isPrioridad() ? "Sí" : "No");
            r.createCell(9).setCellValue(e.isSeguro() ? "Sí" : "No");
            r.createCell(10).setCellValue(e.isFragil() ? "Sí" : "No");
            r.createCell(11).setCellValue(e.isFirmaRequerida() ? "Sí" : "No");
            r.createCell(12).setCellValue(e.getCostoEstimado());
            r.createCell(13).setCellValue(e.getEstado() != null ? e.getEstado().name() : "");
            r.createCell(14).setCellValue(e.getRepartidor() != null ? safe(e.getRepartidor().getNombre()) : "");
            r.createCell(15).setCellValue(e.getFechaCreacionStr());
            r.createCell(16).setCellValue(e.getFechaConfirmacionStr());
            r.createCell(17).setCellValue(e.getFechaEntregaStr());
            r.createCell(18).setCellValue(safe(e.getIncidenciaDescripcion()));
        }
    }

    private static void exportarPagosSheet(Workbook workbook, List<Pago> pagos) {
        Sheet s = workbook.createSheet("Pagos");
        Row h = s.createRow(0);
        h.createCell(0).setCellValue("ID");
        h.createCell(1).setCellValue("ID Envío");
        h.createCell(2).setCellValue("Usuario");
        h.createCell(3).setCellValue("Monto Pagado");
        h.createCell(4).setCellValue("Método de Pago");
        h.createCell(5).setCellValue("Completado");
        h.createCell(6).setCellValue("Fecha Pago");
        h.createCell(7).setCellValue("Peso (Kg)");
        h.createCell(8).setCellValue("Volumen (m³)");
        h.createCell(9).setCellValue("Costo Base");
        h.createCell(10).setCellValue("Costo Final");
        
        int ri = 1;
        for (Pago p : pagos) {
            Row r = s.createRow(ri++);
            r.createCell(0).setCellValue(p.getId() != null ? p.getId() : 0);
            r.createCell(1).setCellValue(p.getEnvio() != null && p.getEnvio().getId() != null ? p.getEnvio().getId() : 0);
            r.createCell(2).setCellValue(p.getEnvio() != null && p.getEnvio().getUsuario() != null 
                    ? safe(p.getEnvio().getUsuario().getNombre()) : "");
            r.createCell(3).setCellValue(p.getMontoPagado());
            r.createCell(4).setCellValue(p.getMetodo() != null ? p.getMetodo().name() : "");
            r.createCell(5).setCellValue(p.isConfirmado() ? "Sí" : "No");
            r.createCell(6).setCellValue(p.getFechaPago() != null ? DATE_FORMAT.format(p.getFechaPago()) : "");
            r.createCell(7).setCellValue(p.getPeso());
            r.createCell(8).setCellValue(p.getVolumen());
            r.createCell(9).setCellValue(p.getCostoBase());
            r.createCell(10).setCellValue(p.getCostoFinal());
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
        content.endText();
    }

    public static void exportarUsuariosPDF(List<Usuario> usuarios, String rutaArchivo) throws IOException {
        ensureParentDir(rutaArchivo);
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            iniciarPDFCabecera(content, "Reporte de Usuarios");

            float yPos = 700;
            for (Usuario u : usuarios) {
                if (yPos < 50) {
                    // Cerrar bloque de texto si está abierto
                    try {
                        content.endText();
                    } catch (Exception e) {
                        // No hay bloque abierto, continuar
                    }
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    iniciarPDFCabecera(content, "Reporte de Usuarios (cont.)");
                    yPos = 700;
                }
                
                content.setFont(PDType1Font.HELVETICA_BOLD, 11);
                content.beginText();
                content.newLineAtOffset(50, yPos);
                content.showText("ID: " + u.getId() + " | " + safe(u.getNombre()) + " | " + safe(u.getEmail()) + " | Tel: " + safe(u.getTelefono()));
                content.endText();
                yPos -= 15;
                
                content.setFont(PDType1Font.HELVETICA, 10);
                content.beginText();
                content.newLineAtOffset(50, yPos);
                
                // Direcciones
                String dirInfo = "Admin: " + (u.isAdmin() ? "Sí" : "No");
                if (u.getDirecciones() != null && !u.getDirecciones().isEmpty()) {
                    if (u.getDirecciones().size() > 0) {
                        Direccion d1 = u.getDirecciones().get(0);
                        dirInfo += " | Dir1: " + safe(d1.getCalle()) + " (Zona: " + safe(d1.getCiudad()) + ")";
                    }
                    if (u.getDirecciones().size() > 1) {
                        Direccion d2 = u.getDirecciones().get(1);
                        dirInfo += " | Dir2: " + safe(d2.getCalle()) + " (Zona: " + safe(d2.getCiudad()) + ")";
                    }
                }
                
                // Métodos de pago
                if (u.getMetodosPago() != null && !u.getMetodosPago().isEmpty()) {
                    String metodos = u.getMetodosPago().stream()
                            .map(Enum::name)
                            .collect(java.util.stream.Collectors.joining(", "));
                    dirInfo += " | Métodos: " + metodos;
                }
                
                content.showText(dirInfo);
                content.endText();
                yPos -= 20;
            }

            // Cerrar cualquier bloque de texto abierto antes de cerrar el content stream
            try {
                content.endText();
            } catch (Exception e) {
                // Ignorar si ya está cerrado
            }
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

            float yPos = 700;
            for (Envio e : envios) {
                if (yPos < 50) {
                    // Cerrar bloque de texto si está abierto
                    try {
                        content.endText();
                    } catch (Exception e2) {
                        // No hay bloque abierto, continuar
                    }
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    iniciarPDFCabecera(content, "Reporte de Envíos (cont.)");
                    yPos = 700;
                }
                
                content.setFont(PDType1Font.HELVETICA_BOLD, 11);
                content.beginText();
                content.newLineAtOffset(50, yPos);
                content.showText("ID: " + e.getId() + " | Usuario: " + 
                        (e.getUsuario() != null ? safe(e.getUsuario().getNombre()) : "") +
                        " | Estado: " + (e.getEstado() != null ? e.getEstado().name() : ""));
                content.endText();
                yPos -= 15;
                
                content.setFont(PDType1Font.HELVETICA, 10);
                content.beginText();
                content.newLineAtOffset(50, yPos);
                
                // Origen y destino
                String origenCalle = e.getOrigen() != null ? safe(e.getOrigen().getCalle()) : "";
                String origenZona = e.getOrigen() != null ? safe(e.getOrigen().getCiudad()) : "";
                String destinoCalle = e.getDestino() != null ? safe(e.getDestino().getCalle()) : "";
                String destinoZona = e.getDestino() != null ? safe(e.getDestino().getCiudad()) : "";
                
                String detalles = "Origen: " + origenCalle + " (Zona: " + origenZona + ") | " +
                                 "Destino: " + destinoCalle + " (Zona: " + destinoZona + ")";
                content.showText(detalles);
                content.endText();
                yPos -= 15;
                
                content.beginText();
                content.newLineAtOffset(50, yPos);
                String extras = "Peso: " + e.getPeso() + "kg | Volumen: " + e.getVolumen() + "m³ | " +
                               "Prioridad: " + (e.isPrioridad() ? "Sí" : "No") + " | " +
                               "Seguro: " + (e.isSeguro() ? "Sí" : "No") + " | " +
                               "Frágil: " + (e.isFragil() ? "Sí" : "No") + " | " +
                               "Firma: " + (e.isFirmaRequerida() ? "Sí" : "No") + " | " +
                               "Costo: $" + e.getCostoEstimado();
                content.showText(extras);
                content.endText();
                yPos -= 15;
                
                content.beginText();
                content.newLineAtOffset(50, yPos);
                String fechas = "Creación: " + e.getFechaCreacionStr() + " | " +
                               "Confirmación: " + e.getFechaConfirmacionStr() + " | " +
                               "Entrega: " + e.getFechaEntregaStr();
                if (e.getRepartidor() != null) {
                    fechas += " | Repartidor: " + safe(e.getRepartidor().getNombre());
                }
                if (e.getIncidenciaDescripcion() != null && !e.getIncidenciaDescripcion().isEmpty()) {
                    fechas += " | Incidencia: " + safe(e.getIncidenciaDescripcion());
                }
                content.showText(fechas);
                content.endText();
                yPos -= 25;
            }

            // Cerrar cualquier bloque de texto abierto antes de cerrar el content stream
            try {
                content.endText();
            } catch (Exception e) {
                // Ignorar si ya está cerrado
            }
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

            float yPos = 700;
            for (Pago p : pagos) {
                if (yPos < 50) {
                    // Cerrar bloque de texto si está abierto
                    try {
                        content.endText();
                    } catch (Exception e) {
                        // No hay bloque abierto, continuar
                    }
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    iniciarPDFCabecera(content, "Reporte de Pagos (cont.)");
                    yPos = 700;
                }
                
                content.setFont(PDType1Font.HELVETICA_BOLD, 11);
                content.beginText();
                content.newLineAtOffset(50, yPos);
                String usuario = p.getEnvio() != null && p.getEnvio().getUsuario() != null 
                        ? safe(p.getEnvio().getUsuario().getNombre()) : "N/A";
                content.showText("ID: " + p.getId() + " | Envío: " + 
                        (p.getEnvio() != null && p.getEnvio().getId() != null ? p.getEnvio().getId() : "N/A") +
                        " | Usuario: " + usuario + " | Monto: $" + p.getMontoPagado());
                content.endText();
                yPos -= 15;
                
                content.setFont(PDType1Font.HELVETICA, 10);
                content.beginText();
                content.newLineAtOffset(50, yPos);
                String detalles = "Método: " + (p.getMetodo() != null ? p.getMetodo().name() : "N/A") +
                                " | Completado: " + (p.isConfirmado() ? "Sí" : "No") +
                                " | Fecha: " + (p.getFechaPago() != null ? DATE_FORMAT.format(p.getFechaPago()) : "N/A");
                content.showText(detalles);
                content.endText();
                yPos -= 15;
                
                content.beginText();
                content.newLineAtOffset(50, yPos);
                String costos = "Peso: " + p.getPeso() + "kg | Volumen: " + p.getVolumen() + "m³ | " +
                               "Costo Base: $" + p.getCostoBase() + " | Costo Final: $" + p.getCostoFinal();
                content.showText(costos);
                content.endText();
                yPos -= 25;
            }

            // Cerrar cualquier bloque de texto abierto antes de cerrar el content stream
            try {
                content.endText();
            } catch (Exception e) {
                // Ignorar si ya está cerrado
            }
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

            float yPos = 700;
            for (Repartidor r : repartidores) {
                if (yPos < 50) {
                    // Cerrar bloque de texto si está abierto
                    try {
                        content.endText();
                    } catch (Exception e) {
                        // No hay bloque abierto, continuar
                    }
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    iniciarPDFCabecera(content, "Reporte de Repartidores (cont.)");
                    yPos = 700;
                }
                
                content.setFont(PDType1Font.HELVETICA_BOLD, 11);
                content.beginText();
                content.newLineAtOffset(50, yPos);
                content.showText("ID: " + r.getId() + " | " + safe(r.getNombre()) +
                        " | Tel: " + safe(r.getTelefono()) +
                        " | Zona: " + safe(r.getZona()) +
                        " | Disponible: " + (r.isDisponible() ? "Sí" : "No"));
                content.endText();
                yPos -= 20;
            }

            // Cerrar cualquier bloque de texto abierto antes de cerrar el content stream
            try {
                content.endText();
            } catch (Exception e) {
                // Ignorar si ya está cerrado
            }
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
        content.setFont(PDType1Font.HELVETICA, 10);
        content.newLineAtOffset(0, -15);
        for (Usuario u : usuarios) {
            String userInfo = "ID: " + u.getId() + " | " + safe(u.getNombre()) + " | " + safe(u.getEmail()) + 
                            " | Tel: " + safe(u.getTelefono()) + " | Admin: " + (u.isAdmin() ? "Sí" : "No");
            if (u.getDirecciones() != null && !u.getDirecciones().isEmpty()) {
                if (u.getDirecciones().size() > 0) {
                    Direccion d1 = u.getDirecciones().get(0);
                    userInfo += " | Dir1: " + safe(d1.getCalle()) + " (Zona: " + safe(d1.getCiudad()) + ")";
                }
            }
            content.showText(userInfo);
            content.newLineAtOffset(0, -15);
        }

        // ----- Envíos -----
        content.newLineAtOffset(0, -10);
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.showText("Envíos:");
        content.setFont(PDType1Font.HELVETICA, 10);
        content.newLineAtOffset(0, -15);
        for (Envio e : envios) {
            String origenCalle = e.getOrigen() != null ? safe(e.getOrigen().getCalle()) : "";
            String origenZona = e.getOrigen() != null ? safe(e.getOrigen().getCiudad()) : "";
            String destinoCalle = e.getDestino() != null ? safe(e.getDestino().getCalle()) : "";
            String destinoZona = e.getDestino() != null ? safe(e.getDestino().getCiudad()) : "";
            
            String envioInfo = "ID: " + e.getId() + " | Usuario: " + 
                    (e.getUsuario() != null ? safe(e.getUsuario().getNombre()) : "N/A") +
                    " | Origen: " + origenCalle + " (Zona: " + origenZona + ")" +
                    " | Destino: " + destinoCalle + " (Zona: " + destinoZona + ")" +
                    " | Estado: " + (e.getEstado() != null ? e.getEstado().name() : "N/A") +
                    " | Peso: " + e.getPeso() + "kg | Costo: $" + e.getCostoEstimado();
            content.showText(envioInfo);
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
                    " | Fecha: " + (p.getFechaPago() != null ? DATE_FORMAT.format(p.getFechaPago()) : "N/A") +
                    " | Estado: " + (p.isConfirmado() ? "Completado" : "Pendiente");
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

    // ------------------------------------------------------------
    // -------------------- EXPORTAR MÉTRICAS --------------------
    // ------------------------------------------------------------

    public static void exportarMetricasExcel(
            Map<String, Double> tiemposPorZona,
            Map<String, Long> serviciosAdicionales,
            Map<String, Double> ingresosPorServicio,
            Map<String, Long> incidenciasPorZona,
            double tiempoPromedio,
            double ingresosTotales,
            long totalEnvios,
            long totalIncidencias,
            String rutaArchivo) throws IOException {
        
        ensureParentDir(rutaArchivo);
        Workbook workbook = new XSSFWorkbook();
        
        // Hoja 1: Métricas Generales
        Sheet sheetGeneral = workbook.createSheet("Métricas Generales");
        Row h1 = sheetGeneral.createRow(0);
        h1.createCell(0).setCellValue("Métrica");
        h1.createCell(1).setCellValue("Valor");
        
        int rowIndex = 1;
        sheetGeneral.createRow(rowIndex++).createCell(0).setCellValue("Tiempo Promedio de Entrega (días)");
        sheetGeneral.getRow(rowIndex - 1).createCell(1).setCellValue(tiempoPromedio);
        sheetGeneral.createRow(rowIndex++).createCell(0).setCellValue("Ingresos Totales (COP)");
        sheetGeneral.getRow(rowIndex - 1).createCell(1).setCellValue(ingresosTotales);
        sheetGeneral.createRow(rowIndex++).createCell(0).setCellValue("Total de Envíos");
        sheetGeneral.getRow(rowIndex - 1).createCell(1).setCellValue(totalEnvios);
        sheetGeneral.createRow(rowIndex++).createCell(0).setCellValue("Total de Incidencias");
        sheetGeneral.getRow(rowIndex - 1).createCell(1).setCellValue(totalIncidencias);
        
        // Hoja 2: Tiempos por Zona
        Sheet sheetTiempos = workbook.createSheet("Tiempos por Zona");
        Row h2 = sheetTiempos.createRow(0);
        h2.createCell(0).setCellValue("Zona");
        h2.createCell(1).setCellValue("Tiempo Promedio (días)");
        rowIndex = 1;
        for (Map.Entry<String, Double> entry : tiemposPorZona.entrySet()) {
            Row row = sheetTiempos.createRow(rowIndex++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
        
        // Hoja 3: Servicios Adicionales
        Sheet sheetServicios = workbook.createSheet("Servicios Adicionales");
        Row h3 = sheetServicios.createRow(0);
        h3.createCell(0).setCellValue("Servicio");
        h3.createCell(1).setCellValue("Cantidad de Usos");
        rowIndex = 1;
        for (Map.Entry<String, Long> entry : serviciosAdicionales.entrySet()) {
            Row row = sheetServicios.createRow(rowIndex++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
        
        // Hoja 4: Ingresos por Servicio
        Sheet sheetIngresos = workbook.createSheet("Ingresos por Servicio");
        Row h4 = sheetIngresos.createRow(0);
        h4.createCell(0).setCellValue("Servicio");
        h4.createCell(1).setCellValue("Ingresos (COP)");
        rowIndex = 1;
        for (Map.Entry<String, Double> entry : ingresosPorServicio.entrySet()) {
            Row row = sheetIngresos.createRow(rowIndex++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
        
        // Hoja 5: Incidencias por Zona
        Sheet sheetIncidencias = workbook.createSheet("Incidencias por Zona");
        Row h5 = sheetIncidencias.createRow(0);
        h5.createCell(0).setCellValue("Zona");
        h5.createCell(1).setCellValue("Cantidad de Incidencias");
        rowIndex = 1;
        for (Map.Entry<String, Long> entry : incidenciasPorZona.entrySet()) {
            Row row = sheetIncidencias.createRow(rowIndex++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
        
        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    public static void exportarMetricasPDF(
            Map<String, Double> tiemposPorZona,
            Map<String, Long> serviciosAdicionales,
            Map<String, Double> ingresosPorServicio,
            Map<String, Long> incidenciasPorZona,
            double tiempoPromedio,
            double ingresosTotales,
            long totalEnvios,
            long totalIncidencias,
            String rutaArchivo) throws IOException {
        
        ensureParentDir(rutaArchivo);
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.beginText();
            content.newLineAtOffset(50, 750);
            content.showText("Reporte de Métricas Operativas - " + LocalDateTime.now().format(DATE_FORMAT));
            content.endText();
            
            float yPos = 700;
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.beginText();
            content.newLineAtOffset(50, yPos);
            content.showText("Métricas Generales:");
            content.endText();
            yPos -= 20;
            
            content.setFont(PDType1Font.HELVETICA, 10);
            content.beginText();
            content.newLineAtOffset(50, yPos);
            content.showText("Tiempo Promedio de Entrega: " + String.format("%.2f", tiempoPromedio) + " días");
            content.endText();
            yPos -= 15;
            
            content.beginText();
            content.newLineAtOffset(50, yPos);
            content.showText("Ingresos Totales: $" + String.format("%,.2f", ingresosTotales) + " COP");
            content.endText();
            yPos -= 15;
            
            content.beginText();
            content.newLineAtOffset(50, yPos);
            content.showText("Total de Envíos: " + totalEnvios);
            content.endText();
            yPos -= 15;
            
            content.beginText();
            content.newLineAtOffset(50, yPos);
            content.showText("Total de Incidencias: " + totalIncidencias);
            content.endText();
            yPos -= 30;
            
            // Tiempos por Zona
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.beginText();
            content.newLineAtOffset(50, yPos);
            content.showText("Tiempos Promedio por Zona:");
            content.endText();
            yPos -= 20;
            
            content.setFont(PDType1Font.HELVETICA, 10);
            for (Map.Entry<String, Double> entry : tiemposPorZona.entrySet()) {
                if (yPos < 50) {
                    // Cerrar bloque de texto si está abierto
                    try {
                        content.endText();
                    } catch (Exception e) {
                        // No hay bloque abierto, continuar
                    }
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    yPos = 750;
                }
                content.beginText();
                content.newLineAtOffset(50, yPos);
                content.showText(entry.getKey() + ": " + String.format("%.2f", entry.getValue()) + " días");
                content.endText();
                yPos -= 15;
            }
            yPos -= 10;
            
            // Servicios Adicionales
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.beginText();
            content.newLineAtOffset(50, yPos);
            content.showText("Servicios Adicionales:");
            content.endText();
            yPos -= 20;
            
            content.setFont(PDType1Font.HELVETICA, 10);
            for (Map.Entry<String, Long> entry : serviciosAdicionales.entrySet()) {
                if (yPos < 50) {
                    // Cerrar bloque de texto si está abierto
                    try {
                        content.endText();
                    } catch (Exception e) {
                        // No hay bloque abierto, continuar
                    }
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    yPos = 750;
                }
                content.beginText();
                content.newLineAtOffset(50, yPos);
                content.showText(entry.getKey() + ": " + entry.getValue() + " usos");
                content.endText();
                yPos -= 15;
            }
            yPos -= 10;
            
            // Ingresos por Servicio
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.beginText();
            content.newLineAtOffset(50, yPos);
            content.showText("Ingresos por Servicio:");
            content.endText();
            yPos -= 20;
            
            content.setFont(PDType1Font.HELVETICA, 10);
            for (Map.Entry<String, Double> entry : ingresosPorServicio.entrySet()) {
                if (yPos < 50) {
                    // Cerrar bloque de texto si está abierto
                    try {
                        content.endText();
                    } catch (Exception e) {
                        // No hay bloque abierto, continuar
                    }
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    yPos = 750;
                }
                content.beginText();
                content.newLineAtOffset(50, yPos);
                content.showText(entry.getKey() + ": $" + String.format("%,.2f", entry.getValue()) + " COP");
                content.endText();
                yPos -= 15;
            }
            yPos -= 10;
            
            // Incidencias por Zona
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.beginText();
            content.newLineAtOffset(50, yPos);
            content.showText("Incidencias por Zona:");
            content.endText();
            yPos -= 20;
            
            content.setFont(PDType1Font.HELVETICA, 10);
            for (Map.Entry<String, Long> entry : incidenciasPorZona.entrySet()) {
                if (yPos < 50) {
                    // Cerrar bloque de texto si está abierto
                    try {
                        content.endText();
                    } catch (Exception e) {
                        // No hay bloque abierto, continuar
                    }
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    yPos = 750;
                }
                content.beginText();
                content.newLineAtOffset(50, yPos);
                content.showText(entry.getKey() + ": " + entry.getValue() + " incidencias");
                content.endText();
                yPos -= 15;
            }
            
            // Cerrar el último beginText si quedó abierto
            try {
                content.endText();
            } catch (Exception e) {
                // Ya estaba cerrado, no hacer nada
            }
            content.close();
            doc.save(rutaArchivo);
        }
    }

    // ========== FACTURA / GUÍA DE ENVÍO ==========
    
    /**
     * Genera una factura o guía de envío en PDF para un envío confirmado
     */
    public static void exportarFacturaEnvio(Envio envio, Pago pago, String rutaArchivo) throws IOException {
        ensureParentDir(rutaArchivo);
        
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            
            float yPos = 750;
            float margin = 50;
            
            // Título
            content.setFont(PDType1Font.HELVETICA_BOLD, 20);
            content.beginText();
            content.newLineAtOffset(margin, yPos);
            content.showText("FACTURA / GUÍA DE ENVÍO");
            content.endText();
            yPos -= 30;
            
            // Línea separadora
            content.setStrokingColor(0, 0, 0);
            content.setLineWidth(1);
            content.moveTo(margin, yPos);
            content.lineTo(550, yPos);
            content.stroke();
            yPos -= 20;
            
            // Información del envío
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.beginText();
            content.newLineAtOffset(margin, yPos);
            content.showText("INFORMACIÓN DEL ENVÍO");
            content.endText();
            yPos -= 25;
            
            content.setFont(PDType1Font.HELVETICA, 10);
            String[] infoEnvio = {
                "Número de Envío: #" + (envio.getId() != null ? envio.getId() : "N/A"),
                "Estado: " + (envio.getEstado() != null ? envio.getEstado().name() : "N/A"),
                "Fecha de Creación: " + (envio.getFechaCreacion() != null ? DATE_FORMAT.format(envio.getFechaCreacion()) : "N/A"),
                "Fecha de Confirmación: " + (envio.getFechaConfirmacion() != null ? DATE_FORMAT.format(envio.getFechaConfirmacion()) : "N/A"),
                "Fecha Estimada de Entrega: " + (envio.getFechaEntregaEstimada() != null ? DATE_FORMAT.format(envio.getFechaEntregaEstimada()) : "N/A"),
                ""
            };
            
            for (String line : infoEnvio) {
                if (yPos < 100) {
                    content.endText();
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    yPos = 750;
                }
                content.beginText();
                content.newLineAtOffset(margin, yPos);
                content.showText(line);
                content.endText();
                yPos -= 15;
            }
            
            // Información del remitente y destinatario
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.beginText();
            content.newLineAtOffset(margin, yPos);
            content.showText("ORIGEN (REMITENTE)");
            content.endText();
            yPos -= 20;
            
            content.setFont(PDType1Font.HELVETICA, 10);
            String[] origen = {
                "Usuario: " + (envio.getUsuario() != null ? safe(envio.getUsuario().getNombre()) : "N/A"),
                "Dirección: " + (envio.getOrigen() != null ? safe(envio.getOrigen().getCalle()) : "N/A"),
                "Zona: " + (envio.getOrigen() != null ? safe(envio.getOrigen().getCiudad()) : "N/A"),
                ""
            };
            
            for (String line : origen) {
                if (yPos < 100) {
                    content.endText();
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    yPos = 750;
                }
                content.beginText();
                content.newLineAtOffset(margin, yPos);
                content.showText(line);
                content.endText();
                yPos -= 15;
            }
            
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.beginText();
            content.newLineAtOffset(margin, yPos);
            content.showText("DESTINO (DESTINATARIO)");
            content.endText();
            yPos -= 20;
            
            content.setFont(PDType1Font.HELVETICA, 10);
            String[] destino = {
                "Dirección: " + (envio.getDestino() != null ? safe(envio.getDestino().getCalle()) : "N/A"),
                "Zona: " + (envio.getDestino() != null ? safe(envio.getDestino().getCiudad()) : "N/A"),
                ""
            };
            
            for (String line : destino) {
                if (yPos < 100) {
                    content.endText();
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    yPos = 750;
                }
                content.beginText();
                content.newLineAtOffset(margin, yPos);
                content.showText(line);
                content.endText();
                yPos -= 15;
            }
            
            // Detalles del paquete
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.beginText();
            content.newLineAtOffset(margin, yPos);
            content.showText("DETALLES DEL PAQUETE");
            content.endText();
            yPos -= 20;
            
            content.setFont(PDType1Font.HELVETICA, 10);
            String[] detalles = {
                "Peso: " + String.format("%.2f", envio.getPeso()) + " kg",
                "Volumen: " + String.format("%.4f", envio.getVolumen()) + " m³",
                "Tipo de Tarifa: " + (envio.getTipoTarifa() != null ? envio.getTipoTarifa() : "Normal"),
                ""
            };
            
            for (String line : detalles) {
                if (yPos < 100) {
                    content.endText();
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    yPos = 750;
                }
                content.beginText();
                content.newLineAtOffset(margin, yPos);
                content.showText(line);
                content.endText();
                yPos -= 15;
            }
            
            // Servicios adicionales
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.beginText();
            content.newLineAtOffset(margin, yPos);
            content.showText("SERVICIOS ADICIONALES");
            content.endText();
            yPos -= 20;
            
            content.setFont(PDType1Font.HELVETICA, 10);
            java.util.List<String> servicios = new java.util.ArrayList<>();
            if (envio.isPrioridad()) servicios.add("* Prioridad (Entrega Express)");
            if (envio.isSeguro()) servicios.add("* Seguro");
            if (envio.isFragil()) servicios.add("* Fragil");
            if (envio.isFirmaRequerida()) servicios.add("* Firma Requerida");
            
            if (servicios.isEmpty()) {
                servicios.add("Ninguno");
            }
            
            for (String servicio : servicios) {
                if (yPos < 100) {
                    content.endText();
                    content.close();
                    page = new PDPage();
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    yPos = 750;
                }
                content.beginText();
                content.newLineAtOffset(margin, yPos);
                content.showText(servicio);
                content.endText();
                yPos -= 15;
            }
            yPos -= 10;
            
            // Repartidor
            if (envio.getRepartidor() != null) {
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(margin, yPos);
                content.showText("REPARTIDOR ASIGNADO");
                content.endText();
                yPos -= 20;
                
                content.setFont(PDType1Font.HELVETICA, 10);
                String[] repartidor = {
                    "Nombre: " + safe(envio.getRepartidor().getNombre()),
                    "Teléfono: " + safe(envio.getRepartidor().getTelefono()),
                    "Zona: " + safe(envio.getRepartidor().getZona()),
                    ""
                };
                
                for (String line : repartidor) {
                    if (yPos < 100) {
                        content.endText();
                        content.close();
                        page = new PDPage();
                        doc.addPage(page);
                        content = new PDPageContentStream(doc, page);
                        yPos = 750;
                    }
                    content.beginText();
                    content.newLineAtOffset(margin, yPos);
                    content.showText(line);
                    content.endText();
                    yPos -= 15;
                }
            }
            
            // Información de pago
            if (pago != null) {
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(margin, yPos);
                content.showText("INFORMACIÓN DE PAGO");
                content.endText();
                yPos -= 20;
                
                // Usar el costo estimado del envío como fuente de verdad (debe coincidir con monto pagado)
                double montoTotal = envio.getCostoEstimado() > 0 ? envio.getCostoEstimado() : pago.getMontoPagado();
                long montoRedondeado = Math.round(montoTotal);
                
                content.setFont(PDType1Font.HELVETICA, 10);
                String[] pagoInfo = {
                    "Número de Pago: #" + (pago.getId() != null ? pago.getId() : "N/A"),
                    "Monto Pagado: $" + String.format("%,d", montoRedondeado) + " COP",
                    "Método de Pago: " + (pago.getMetodo() != null ? pago.getMetodo().name() : "N/A"),
                    "Estado: " + (pago.isConfirmado() ? "CONFIRMADO" : "PENDIENTE"),
                    "Fecha de Pago: " + (pago.getFechaPago() != null ? DATE_FORMAT.format(pago.getFechaPago()) : "N/A"),
                    ""
                };
                
                for (String line : pagoInfo) {
                    if (yPos < 100) {
                        // Cerrar el content actual si hay un beginText activo
                        try {
                            content.endText();
                        } catch (Exception e) {
                            // Si no hay beginText activo, ignorar
                        }
                        content.close();
                        page = new PDPage();
                        doc.addPage(page);
                        content = new PDPageContentStream(doc, page);
                        yPos = 750;
                    }
                    content.setFont(PDType1Font.HELVETICA, 10);
                    content.beginText();
                    content.newLineAtOffset(margin, yPos);
                    content.showText(line);
                    content.endText();
                    yPos -= 15;
                }
            }
            
            // Total - usar el costo estimado del envío (sin decimales)
            double totalFinal = envio.getCostoEstimado() > 0 ? envio.getCostoEstimado() : (pago != null ? pago.getMontoPagado() : 0.0);
            long totalRedondeado = Math.round(totalFinal);
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.beginText();
            content.newLineAtOffset(margin, yPos);
            content.showText("TOTAL: $" + String.format("%,d", totalRedondeado) + " COP");
            content.endText();
            
            // Cerrar el último beginText si quedó abierto
            try {
                content.endText();
            } catch (Exception e) {
                // Ya estaba cerrado, no hacer nada
            }
            content.close();
            doc.save(rutaArchivo);
        }
    }

}



