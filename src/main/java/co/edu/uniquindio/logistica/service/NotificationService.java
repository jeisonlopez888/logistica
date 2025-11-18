package co.edu.uniquindio.logistica.service;

import co.edu.uniquindio.logistica.model.Envio;
import co.edu.uniquindio.logistica.model.Usuario;
import co.edu.uniquindio.logistica.model.Repartidor;
import co.edu.uniquindio.logistica.observer.Observer;
import co.edu.uniquindio.logistica.observer.TipoEvento;

/**
 * Servicio de notificaciones que implementa Observer
 * Maneja el envío de notificaciones por diferentes canales
 */
public class NotificationService implements Observer {

    public enum CanalNotificacion {
        WHATSAPP("WhatsApp"),
        EMAIL("Correo Electrónico"),
        SMS("SMS");

        private final String nombre;

        CanalNotificacion(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return nombre;
        }
    }

    private CanalNotificacion canalPreferido = CanalNotificacion.EMAIL;

    /**
     * Establece el canal de notificación preferido
     */
    public void setCanalPreferido(CanalNotificacion canal) {
        this.canalPreferido = canal;
    }

    @Override
    public void notificar(Envio envio, TipoEvento tipoEvento, String mensaje) {
        // Determinar destinatarios según el tipo de evento
        if (tipoEvento == TipoEvento.CREACION_ENVIO) {
            // Notificar al usuario y administrador
            if (envio.getUsuario() != null) {
                enviarNotificacion(envio.getUsuario(), envio, tipoEvento, mensaje);
            }
            notificarAdministradores(envio, tipoEvento, mensaje);
        } else if (tipoEvento == TipoEvento.CAMBIO_ESTADO ||
                tipoEvento == TipoEvento.ASIGNACION_REPARTIDOR ||
                tipoEvento == TipoEvento.INCIDENCIA ||
                tipoEvento == TipoEvento.ENTREGA_COMPLETADA) {
            // Notificar al usuario, administrador y repartidor
            if (envio.getUsuario() != null) {
                enviarNotificacion(envio.getUsuario(), envio, tipoEvento, mensaje);
            }
            notificarAdministradores(envio, tipoEvento, mensaje);
            if (envio.getRepartidor() != null) {
                enviarNotificacionRepartidor(envio.getRepartidor(), envio, tipoEvento, mensaje);
            }
        }
    }

    /**
     * Envía notificación a un usuario
     */
    private void enviarNotificacion(Usuario usuario, Envio envio, TipoEvento tipoEvento, String mensaje) {
        String notificacionCompleta = String.format(
                "📧 NOTIFICACIÓN [%s] para %s (%s)\n" +
                        "   Envío #%d\n" +
                        "   Mensaje: %s\n" +
                        "   Canal: %s\n" +
                        "   Fecha: %s\n",
                tipoEvento.getDescripcion(),
                usuario.getNombre(),
                usuario.getEmail(),
                envio.getId(),
                mensaje,
                canalPreferido.getNombre(),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );

        // Imprimir en consola
        System.out.println("=".repeat(60));
        System.out.println(notificacionCompleta);
        System.out.println("=".repeat(60));

        // Simular envío por el canal seleccionado
        switch (canalPreferido) {
            case WHATSAPP:
                enviarWhatsApp(usuario, notificacionCompleta);
                break;
            case EMAIL:
                enviarEmail(usuario, notificacionCompleta);
                break;
            case SMS:
                enviarSMS(usuario, notificacionCompleta);
                break;
        }
    }

    /**
     * Envía notificación a un repartidor
     */
    private void enviarNotificacionRepartidor(Repartidor repartidor, Envio envio, TipoEvento tipoEvento, String mensaje) {
        String notificacionCompleta = String.format(
                "📧 NOTIFICACIÓN [%s] para Repartidor %s (%s)\n" +
                        "   Envío #%d\n" +
                        "   Mensaje: %s\n" +
                        "   Canal: %s\n" +
                        "   Fecha: %s\n",
                tipoEvento.getDescripcion(),
                repartidor.getNombre(),
                repartidor.getTelefono(),
                envio.getId(),
                mensaje,
                canalPreferido.getNombre(),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );

        // Imprimir en consola
        System.out.println("=".repeat(60));
        System.out.println(notificacionCompleta);
        System.out.println("=".repeat(60));

        // Simular envío por el canal seleccionado
        switch (canalPreferido) {
            case WHATSAPP:
                enviarWhatsAppRepartidor(repartidor, notificacionCompleta);
                break;
            case EMAIL:
                enviarEmailRepartidor(repartidor, notificacionCompleta);
                break;
            case SMS:
                enviarSMSRepartidor(repartidor, notificacionCompleta);
                break;
        }
    }

    /**
     * Notifica a todos los administradores
     */
    private void notificarAdministradores(Envio envio, TipoEvento tipoEvento, String mensaje) {
        co.edu.uniquindio.logistica.store.DataStore store = co.edu.uniquindio.logistica.store.DataStore.getInstance();

        for (Usuario admin : store.getUsuarios()) {
            if (admin.isAdmin()) {
                String notificacionCompleta = String.format(
                        "📧 NOTIFICACIÓN [%s] para Administrador %s (%s)\n" +
                                "   Envío #%d\n" +
                                "   Mensaje: %s\n" +
                                "   Canal: %s\n" +
                                "   Fecha: %s\n",
                        tipoEvento.getDescripcion(),
                        admin.getNombre(),
                        admin.getEmail(),
                        envio.getId(),
                        mensaje,
                        canalPreferido.getNombre(),
                        java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                );

                // Imprimir en consola
                System.out.println("=".repeat(60));
                System.out.println(notificacionCompleta);
                System.out.println("=".repeat(60));

                // Simular envío por el canal seleccionado
                switch (canalPreferido) {
                    case WHATSAPP:
                        enviarWhatsApp(admin, notificacionCompleta);
                        break;
                    case EMAIL:
                        enviarEmail(admin, notificacionCompleta);
                        break;
                    case SMS:
                        enviarSMS(admin, notificacionCompleta);
                        break;
                }
            }
        }
    }

    // Métodos simulados para envío de notificaciones

    private void enviarWhatsApp(Usuario usuario, String mensaje) {
        System.out.println("📱 [SIMULACIÓN] Enviando WhatsApp a " + usuario.getTelefono() + ":");
        System.out.println("   " + mensaje.replace("\n", "\n   "));
    }

    private void enviarEmail(Usuario usuario, String mensaje) {
        System.out.println("📧 [SIMULACIÓN] Enviando Email a " + usuario.getEmail() + ":");
        System.out.println("   Asunto: Notificación de Envío");
        System.out.println("   " + mensaje.replace("\n", "\n   "));
    }

    private void enviarSMS(Usuario usuario, String mensaje) {
        System.out.println("💬 [SIMULACIÓN] Enviando SMS a " + usuario.getTelefono() + ":");
        System.out.println("   " + mensaje.replace("\n", "\n   "));
    }

    private void enviarWhatsAppRepartidor(Repartidor repartidor, String mensaje) {
        System.out.println("📱 [SIMULACIÓN] Enviando WhatsApp a Repartidor " + repartidor.getTelefono() + ":");
        System.out.println("   " + mensaje.replace("\n", "\n   "));
    }

    private void enviarEmailRepartidor(Repartidor repartidor, String mensaje) {
        // Los repartidores no tienen email en el modelo actual, usar SMS como alternativa
        System.out.println("💬 [SIMULACIÓN] Enviando SMS a Repartidor " + repartidor.getTelefono() + " (no tiene email):");
        System.out.println("   " + mensaje.replace("\n", "\n   "));
    }

    private void enviarSMSRepartidor(Repartidor repartidor, String mensaje) {
        System.out.println("💬 [SIMULACIÓN] Enviando SMS a Repartidor " + repartidor.getTelefono() + ":");
        System.out.println("   " + mensaje.replace("\n", "\n   "));
    }
}


