package co.edu.uniquindio.logistica.model.DTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EnvioDTO {
    public enum EstadoEnvio {
        SOLICITADO, CONFIRMADO, ASIGNADO, EN_RUTA, ENTREGADO, CANCELADO, INCIDENCIA
    }

    private Long id;
    private Long idUsuario;  // Solo ID, no la entidad completa
    private DireccionDTO origen;
    private DireccionDTO destino;
    private double peso;
    private double volumen;
    private boolean prioridad;
    private boolean seguro;
    private boolean fragil;
    private boolean firmaRequerida;
    private double costoEstimado;
    private EstadoEnvio estado;
    private Long idRepartidor;  // Solo ID, no la entidad completa
    private String incidenciaDescripcion;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaConfirmacion;
    private LocalDateTime fechaEntrega;
    private LocalDateTime fechaEntregaEstimada;
    private LocalDateTime fechaIncidencia;

    // ✅ Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public DireccionDTO getOrigen() { return origen; }
    public void setOrigen(DireccionDTO origen) { this.origen = origen; }

    public DireccionDTO getDestino() { return destino; }
    public void setDestino(DireccionDTO destino) { this.destino = destino; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getVolumen() { return volumen; }
    public void setVolumen(double volumen) { this.volumen = volumen; }

    public boolean isPrioridad() { return prioridad; }
    public void setPrioridad(boolean prioridad) { this.prioridad = prioridad; }

    public boolean isSeguro() { return seguro; }
    public void setSeguro(boolean seguro) { this.seguro = seguro; }

    public boolean isFragil() { return fragil; }
    public void setFragil(boolean fragil) { this.fragil = fragil; }

    public boolean isFirmaRequerida() { return firmaRequerida; }
    public void setFirmaRequerida(boolean firmaRequerida) { this.firmaRequerida = firmaRequerida; }

    public double getCostoEstimado() { return costoEstimado; }
    public void setCostoEstimado(double costoEstimado) { this.costoEstimado = costoEstimado; }

    public EstadoEnvio getEstado() { return estado; }
    public void setEstado(EstadoEnvio estado) { this.estado = estado; }

    public Long getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(Long idRepartidor) { this.idRepartidor = idRepartidor; }

    public String getIncidenciaDescripcion() { return incidenciaDescripcion; }
    public void setIncidenciaDescripcion(String incidenciaDescripcion) { this.incidenciaDescripcion = incidenciaDescripcion; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaConfirmacion() { return fechaConfirmacion; }
    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) { this.fechaConfirmacion = fechaConfirmacion; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public LocalDateTime getFechaEntregaEstimada() { return fechaEntregaEstimada; }
    public void setFechaEntregaEstimada(LocalDateTime fechaEntregaEstimada) { this.fechaEntregaEstimada = fechaEntregaEstimada; }

    public LocalDateTime getFechaIncidencia() { return fechaIncidencia; }
    public void setFechaIncidencia(LocalDateTime fechaIncidencia) { this.fechaIncidencia = fechaIncidencia; }

    // ✅ Métodos auxiliares para mostrar en tabla sin errores
    public String getOrigenDireccion() {
        return origen != null ? origen.getCalle() : "";
    }

    public String getOrigenCiudad() {
        return origen != null ? origen.getCiudad() : "";
    }

    public String getDestinoDireccion() {
        return destino != null ? destino.getCalle() : "";
    }

    public String getDestinoCiudad() {
        return destino != null ? destino.getCiudad() : "";
    }

    public String getFechaCreacionStr() {
        return fechaCreacion != null ? fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    public String getFechaConfirmacionStr() {
        return fechaConfirmacion != null ? fechaConfirmacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    public String getFechaEntregaStr() {
        return fechaEntrega != null ? fechaEntrega.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    public String getIncidenciaResumen() {
        return incidenciaDescripcion != null && !incidenciaDescripcion.isEmpty()
                ? (incidenciaDescripcion.length() > 25
                ? incidenciaDescripcion.substring(0, 25) + "..."
                : incidenciaDescripcion)
                : "";
    }

    @Override
    public String toString() {
        return String.format("Envío #%d - %s [%s → %s] %.2fkg - Estado: %s",
                id,
                idUsuario != null ? "Usuario #" + idUsuario : "Sin usuario",
                origen != null ? origen.getCiudad() : "?",
                destino != null ? destino.getCiudad() : "?",
                peso,
                estado != null ? estado : "Sin estado");
    }
}
