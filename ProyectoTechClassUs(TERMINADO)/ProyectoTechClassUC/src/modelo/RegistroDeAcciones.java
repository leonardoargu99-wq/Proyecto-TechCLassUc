/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDateTime;

/**
 * Representa un registro de una acción realizada en el sistema. Se utiliza para
 * guardar el historial de acciones en un Stack (pila), permitiendo implementar
 * la funcionalidad de deshacer.
 *
 * Cada registro contiene: - El tipo de acción realizada (agregar, eliminar,
 * atender, finalizar) - El cliente involucrado en la acción - La fecha y hora
 * en que se realizó la acción
 *
 * @author young
 */
public class RegistroDeAcciones {

    private String tipoAccion;
    private Cliente cliente;
    private LocalDateTime fechaHora;

    /**
     * Constructor de RegistroDeAcciones. Crea un nuevo registro con el tipo de
     * acción y el cliente involucrado. La fecha y hora se establece
     * automáticamente al momento actual.
     *
     * @param tipoAccion el tipo de acción realizada (agregar, eliminar,
     * atender, finalizar)
     * @param cliente el cliente involucrado en la acción
     */
    public RegistroDeAcciones(String tipoAccion, Cliente cliente) {
        this.tipoAccion = tipoAccion;
        this.cliente = cliente;
        this.fechaHora = LocalDateTime.now();
    }

    /**
     * Obtiene el tipo de acción del registro.
     *
     * @return el tipo de acción (agregar, eliminar, atender, finalizar)
     */
    public String getTipoAccion() {
        return tipoAccion;
    }

    /**
     * Establece el tipo de acción del registro.
     *
     * @param tipoAccion el nuevo tipo de acción
     */
    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    /**
     * Obtiene el cliente asociado a esta acción.
     *
     * @return el objeto Cliente involucrado en la acción
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Establece el cliente asociado a esta acción.
     *
     * @param cliente el nuevo cliente a asociar
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Obtiene la fecha y hora en que se realizó la acción.
     *
     * @return la fecha y hora del registro
     */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    /**
     * Establece la fecha y hora del registro.
     *
     * @param fechaHora la nueva fecha y hora
     */
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    /**
     * Devuelve una representación en String del registro de acción. Incluye el
     * tipo de acción, el cliente y la fecha/hora.
     *
     * @return representación en texto del objeto RegistroDeAcciones
     */
    @Override
    public String toString() {
        return "Accion{"
                + "tipoAccion='" + tipoAccion + '\''
                + ", cliente=" + cliente
                + ", fechaHora=" + fechaHora
                + '}';
    }
}
