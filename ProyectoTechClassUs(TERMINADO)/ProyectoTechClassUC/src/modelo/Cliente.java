/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDateTime;

/**
 * Representa un cliente del sistema de gestión de TechClassUC. Contiene toda la
 * información relacionada con un cliente, incluyendo sus datos personales, tipo
 * de solicitud, prioridad, problema reportado y diagnóstico dado.
 *
 * @author young
 */
public class Cliente {

    private String id;
    private String nombre;
    private String tipoSolicitud;
    private Prioridad prioridad;
    private String problema;
    private String diagnostico;
    private String fechaRegistro;
    private LocalDateTime horaLlegada;
    private LocalDateTime horaAtencion;

    /**
     * Constructor básico de Cliente. Inicializa un cliente con sus datos
     * principales. La hora de llegada se establece automáticamente al momento
     * actual.
     *
     * @param id identificador único del cliente
     * @param nombre nombre completo del cliente
     * @param tipoSolicitud tipo de solicitud (Soporte, Mantenimiento, Reclamo)
     * @param prioridad nivel de prioridad (Normal, Urgente)
     */
    public Cliente(String id, String nombre, String tipoSolicitud, Prioridad prioridad) {
        this.id = id;
        this.nombre = nombre;
        this.tipoSolicitud = tipoSolicitud;
        this.prioridad = prioridad;
        this.horaLlegada = LocalDateTime.now();
    }

    /**
     * Constructor completo de Cliente. Inicializa un cliente con todos sus
     * datos incluyendo el problema reportado y la fecha de registro. La hora de
     * llegada se establece automáticamente al momento actual.
     *
     * @param id identificador único del cliente
     * @param nombre nombre completo del cliente
     * @param tipoSolicitud tipo de solicitud (Soporte, Mantenimiento, Reclamo)
     * @param prioridad nivel de prioridad (Normal, Urgente)
     * @param problema descripción del problema del cliente
     * @param fechaRegistro fecha en que se registró el cliente
     */
    public Cliente(String id, String nombre, String tipoSolicitud, Prioridad prioridad,
            String problema, String fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.tipoSolicitud = tipoSolicitud;
        this.prioridad = prioridad;
        this.problema = problema;
        this.fechaRegistro = fechaRegistro;
        this.horaLlegada = LocalDateTime.now();
    }

    /**
     * Obtiene el identificador del cliente.
     *
     * @return el ID del cliente
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del cliente.
     *
     * @param id el nuevo ID del cliente
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del cliente.
     *
     * @return el nombre completo del cliente
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del cliente.
     *
     * @param nombre el nuevo nombre del cliente
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el tipo de solicitud del cliente.
     *
     * @return el tipo de solicitud (Soporte, Mantenimiento, Reclamo)
     */
    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    /**
     * Establece el tipo de solicitud del cliente.
     *
     * @param tipoSolicitud el nuevo tipo de solicitud
     */
    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    /**
     * Obtiene la prioridad del cliente.
     *
     * @return la prioridad (Normal o Urgente)
     */
    public Prioridad getPrioridad() {
        return prioridad;
    }

    /**
     * Establece la prioridad del cliente.
     *
     * @param prioridad la nueva prioridad
     */
    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    /**
     * Obtiene la hora de llegada del cliente.
     *
     * @return la fecha y hora en que el cliente llegó al sistema
     */
    public LocalDateTime getHoraLlegada() {
        return horaLlegada;
    }

    /**
     * Establece la hora de llegada del cliente.
     *
     * @param horaLlegada la nueva hora de llegada
     */
    public void setHoraLlegada(LocalDateTime horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    /**
     * Obtiene la hora en que el cliente fue atendido.
     *
     * @return la fecha y hora de atención, null si aún no ha sido atendido
     */
    public LocalDateTime getHoraAtencion() {
        return horaAtencion;
    }

    /**
     * Establece la hora en que el cliente fue atendido.
     *
     * @param horaAtencion la hora de atención
     */
    public void setHoraAtencion(LocalDateTime horaAtencion) {
        this.horaAtencion = horaAtencion;
    }

    /**
     * Obtiene la descripción del problema reportado por el cliente.
     *
     * @return el problema del cliente
     */
    public String getProblema() {
        return problema;
    }

    /**
     * Establece la descripción del problema del cliente.
     *
     * @param problema la descripción del problema
     */
    public void setProblema(String problema) {
        this.problema = problema;
    }

    /**
     * Obtiene la fecha de registro del cliente.
     *
     * @return la fecha de registro en formato String
     */
    public String getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Establece la fecha de registro del cliente.
     *
     * @param fechaRegistro la nueva fecha de registro
     */
    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Obtiene el diagnóstico dado al cliente.
     *
     * @return el diagnóstico, null si aún no ha sido diagnosticado
     */
    public String getDiagnostico() {
        return diagnostico;
    }

    /**
     * Establece el diagnóstico del cliente.
     *
     * @param diagnostico el diagnóstico a registrar
     */
    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    /**
     * Devuelve una representación en String del cliente. Incluye ID, nombre,
     * tipo de solicitud y prioridad.
     *
     * @return representación en texto del objeto Cliente
     */
    @Override
    public String toString() {
        return "Cliente{"
                + "id='" + id + '\''
                + ", nombre='" + nombre + '\''
                + ", tipoSolicitud='" + tipoSolicitud + '\''
                + ", prioridad='" + prioridad + '\''
                + '}';
    }
}
