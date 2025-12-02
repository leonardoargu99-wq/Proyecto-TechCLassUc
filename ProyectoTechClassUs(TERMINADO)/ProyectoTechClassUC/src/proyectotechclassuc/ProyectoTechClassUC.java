/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectotechclassuc;

import controlador.MVCTechClassUC;
import vista.VentanaTechClassUC;

/**
 * Clase principal del proyecto TechClassUC.
 * Punto de entrada de la aplicación que inicializa el sistema
 * de gestión de clientes siguiendo el patrón MVC (Modelo-Vista-Controlador).
 * 
 * El sistema utiliza tres estructuras de datos fundamentales:
 * - ArrayDeque: Para la cola de clientes en espera (FIFO)
 * - LinkedList: Para el historial de clientes atendidos
 * - Stack: Para el registro de acciones y funcionalidad deshacer (LIFO)
 * 
 *
 */
public class ProyectoTechClassUC {

    /** 
     * Método principal que inicia la aplicación.
     * Crea la ventana principal, inicializa el controlador MVC
     * y muestra la interfaz gráfica al usuario.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        VentanaTechClassUC ventana = new VentanaTechClassUC();
        MVCTechClassUC controlador = new MVCTechClassUC(ventana);
        controlador.iniciar();
    }
    
}
