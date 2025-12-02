/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author young
 */
// **
// * Enumeración que define los niveles de prioridad para la atención de clientes.
/* Utiliza un displayName para mostrar valores legibles en la interfaz gráfica.
 */
public enum Prioridad {
    NORMAL("Normal"),
    URGENTE("Urgente");

    private final String displayName;

    /**
     * Constructor del enum Prioridad.
     *
     * @param displayName el nombre a mostrar en la interfaz
     */
    Prioridad(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Devuelve la representación en String de la prioridad. Este método es
     * usado por los JComboBox para mostrar los valores.
     *
     * @return el nombre legible de la prioridad
     */
    @Override
    public String toString() {
        return displayName;
    }
}
