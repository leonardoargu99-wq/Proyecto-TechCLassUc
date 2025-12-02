/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Sistema de gestión de clientes para TechClassUC. Administra la cola de
 * espera, el historial de atendidos y el registro de acciones.
 *
 * Utiliza tres estructuras de datos principales: - ArrayDeque: Cola FIFO para
 * clientes en espera - LinkedList: Lista para historial de clientes atendidos -
 * Stack: Pila LIFO para registro de acciones (permite deshacer)
 *
 * @author young
 */
public class SistemaDeGestion {

    private ArrayDeque<Cliente> colaClientes;
    private LinkedList<Cliente> historialAtendidos;
    private Stack<RegistroDeAcciones> pilaAcciones;
    private Cliente clienteEnAtencion;
    private int contadorNormalesAtendidos;

    /**
     * Constructor del sistema de gestión. Inicializa las tres estructuras de
     * datos vacías: - ArrayDeque para la cola de clientes - LinkedList para el
     * historial de atendidos - Stack para el registro de acciones
     */
    public SistemaDeGestion() {
        this.colaClientes = new ArrayDeque<>();
        this.historialAtendidos = new LinkedList<>();
        this.pilaAcciones = new Stack<>();
        this.clienteEnAtencion = null;
        this.contadorNormalesAtendidos = 0;
    }

    /**
     * Agrega un cliente a la cola de espera. Utiliza offer() del ArrayDeque
     * para agregar al final de la cola. Registra la acción en el Stack para
     * poder deshacerla.
     *
     * @param cliente el cliente a agregar a la cola
     */
    public void agregarCliente(Cliente cliente) {
        colaClientes.offer(cliente);
        pilaAcciones.push(new RegistroDeAcciones("agregar", cliente));
    }

    /**
     * Atiende al siguiente cliente de la cola. Utiliza poll() del ArrayDeque
     * para extraer el primer cliente. Registra la hora de atención, lo agrega
     * al historial (LinkedList) y guarda la acción en el Stack.
     *
     * @return el cliente atendido, o null si la cola está vacía
     */
    public Cliente atenderCliente() {
        if (colaClientes.isEmpty()) {
            return null;
        }

        Cliente cliente = null;

        if (contadorNormalesAtendidos >= 2) {
            cliente = buscarYExtraerUrgente();

            // Si encontramos urgente, reiniciamos contador
            if (cliente != null) {
                contadorNormalesAtendidos = 0;
            } else {
                // No hay urgentes, atendemos normal
                cliente = colaClientes.poll();
                if (cliente.getPrioridad() == Prioridad.NORMAL) {
                    contadorNormalesAtendidos++;
                }
            }
        } else {
            // Aún no llegamos a 2 normales, atendemos el siguiente
            cliente = colaClientes.poll();

            // Incrementamos solo si es normal
            if (cliente.getPrioridad() == Prioridad.NORMAL) {
                contadorNormalesAtendidos++;
            }
        }

        if (cliente != null) {
            cliente.setHoraAtencion(java.time.LocalDateTime.now());
            clienteEnAtencion = cliente;
            historialAtendidos.add(cliente);
            pilaAcciones.push(new RegistroDeAcciones("atender", cliente));
        }

        return cliente;
    }

    /**
     * Elimina un cliente específico de la cola de espera por su ID. Recorre el
     * ArrayDeque usando un Iterator para buscar y eliminar. Registra la acción
     * en el Stack para poder deshacerla.
     *
     * @param id el identificador del cliente a eliminar
     * @return true si se eliminó exitosamente, false si no se encontró
     */
    public boolean eliminarClienteDeCola(String id) {
        Iterator<Cliente> iterator = colaClientes.iterator();
        while (iterator.hasNext()) {
            Cliente c = iterator.next();
            if (c.getId().equals(id)) {
                iterator.remove();
                pilaAcciones.push(new RegistroDeAcciones("eliminar", c));
                return true;
            }
        }
        return false;
    }

    /**
     * 🆕 Busca y extrae el primer cliente URGENTE de la cola.
     *
     * @return el cliente urgente encontrado, o null si no hay
     */
    private Cliente buscarYExtraerUrgente() {
        Iterator<Cliente> iterator = colaClientes.iterator();
        Cliente clienteUrgente = null;

        while (iterator.hasNext()) {
            Cliente c = iterator.next();
            if (c.getPrioridad() == Prioridad.URGENTE) {
                clienteUrgente = c;
                iterator.remove(); // Lo sacamos de la cola
                break;
            }
        }

        return clienteUrgente;
    }

    /**
     * 🆕 Reinicia el contador de prioridad (útil para deshacer)
     */
    public void reiniciarContadorPrioridad() {
        this.contadorNormalesAtendidos = 0;
    }

    /**
     * 🆕 Obtiene el contador actual de normales atendidos
     */
    public int getContadorNormalesAtendidos() {
        return contadorNormalesAtendidos;
    }

    /**
     * Deshace la última acción realizada en el sistema. Utiliza pop() del Stack
     * para obtener la última acción. Revierte el estado según el tipo de
     * acción: - "agregar": remueve el cliente del ArrayDeque - "atender":
     * devuelve el cliente a la cola (offerFirst para ponerlo al inicio) -
     * "eliminar": devuelve el cliente al ArrayDeque - "finalizar": remueve el
     * cliente del historial y lo pone en atención
     */
    public void deshacerUltimaAccion() {
        if (pilaAcciones.isEmpty()) {
            return;
        }

        RegistroDeAcciones ultimaAccion = pilaAcciones.pop();
        String tipoAccion = ultimaAccion.getTipoAccion();
        Cliente cliente = ultimaAccion.getCliente();

        switch (tipoAccion) {
            case "agregar":
                colaClientes.remove(cliente);
                break;

            case "atender":
                historialAtendidos.remove(cliente);
                colaClientes.offerFirst(cliente);
                clienteEnAtencion = null;
                if (cliente.getPrioridad() == Prioridad.NORMAL) {
                    contadorNormalesAtendidos = Math.max(0, contadorNormalesAtendidos - 1);
                }
                break;

            case "eliminar":
                colaClientes.offer(cliente);
                break;

            case "finalizar":
                historialAtendidos.remove(cliente);
                clienteEnAtencion = cliente;
                break;
        }
    }

    /**
     * Registra una acción en el Stack sin ejecutar ninguna lógica. Útil para
     * registrar acciones que se manejan manualmente en el controlador.
     *
     * @param tipoAccion el tipo de acción a registrar
     * @param cliente el cliente involucrado en la acción
     */
    public void registrarAccion(String tipoAccion, Cliente cliente) {
        pilaAcciones.push(new RegistroDeAcciones(tipoAccion, cliente));
    }

    /**
     * Obtiene el cliente actualmente en atención.
     *
     * @return el cliente en atención, o null si no hay ninguno
     */
    public Cliente getClienteEnAtencion() {
        return clienteEnAtencion;
    }

    /**
     * Finaliza la atención del cliente actual. Establece el cliente en atención
     * como null.
     */
    public void finalizarAtencion() {
        clienteEnAtencion = null;
    }

    /**
     * Busca clientes en el historial por tipo de solicitud. Recorre la
     * LinkedList de historial y filtra por tipo.
     *
     * @param tipo el tipo de solicitud a buscar (Soporte, Mantenimiento,
     * Reclamo)
     * @return una LinkedList con los clientes que coinciden con el tipo
     */
    public LinkedList<Cliente> buscarPorTipoSolicitud(String tipo) {
        LinkedList<Cliente> resultado = new LinkedList<>();
        for (Cliente c : historialAtendidos) {
            if (c.getTipoSolicitud().equalsIgnoreCase(tipo)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    /**
     * Busca un cliente específico en el historial por su ID. Recorre la
     * LinkedList de historial buscando coincidencia.
     *
     * @param id el identificador del cliente a buscar
     * @return el cliente encontrado, o null si no existe
     */
    public Cliente buscarPorId(String id) {
        for (Cliente c : historialAtendidos) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Obtiene el número total de clientes en espera. Utiliza size() del
     * ArrayDeque.
     *
     * @return la cantidad de clientes en la cola de espera
     */
    public int getTotalClientesEnEspera() {
        return colaClientes.size();
    }

    /**
     * Obtiene el número total de clientes atendidos. Utiliza size() de la
     * LinkedList.
     *
     * @return la cantidad de clientes en el historial de atendidos
     */
    public int getTotalClientesAtendidos() {
        return historialAtendidos.size();
    }

    /**
     * Calcula el promedio de tiempo de atención de los clientes. Recorre la
     * LinkedList de historial y calcula la diferencia entre hora de llegada y
     * hora de atención.
     *
     * @return el promedio de tiempo en minutos, o 0.0 si no hay datos
     */
    public double getPromedioTiempoAtencion() {
        if (historialAtendidos.isEmpty()) {
            return 0.0;
        }

        long totalMinutos = 0;
        int count = 0;

        for (Cliente c : historialAtendidos) {
            if (c.getHoraAtencion() != null) {
                Duration duracion = Duration.between(c.getHoraLlegada(), c.getHoraAtencion());
                totalMinutos += duracion.toMinutes();
                count++;
            }
        }

        return count > 0 ? (double) totalMinutos / count : 0.0;
    }

    /**
     * Obtiene la cola de clientes en espera.
     *
     * @return el ArrayDeque con todos los clientes en espera
     */
    public ArrayDeque<Cliente> getColaClientes() {
        return colaClientes;
    }

    /**
     * Obtiene el historial de clientes atendidos.
     *
     * @return la LinkedList con todos los clientes atendidos
     */
    public LinkedList<Cliente> getHistorialAtendidos() {
        return historialAtendidos;
    }

    /**
     * Obtiene la pila de acciones realizadas.
     *
     * @return el Stack con todos los registros de acciones
     */
    public Stack<RegistroDeAcciones> getPilaAcciones() {
        return pilaAcciones;
    }

    /**
     * Obtiene las acciones en orden inverso (de más reciente a más antigua).
     * Convierte el Stack en una List y la invierte usando
     * Collections.reverse().
     *
     * @return una List con las acciones en orden inverso
     */
    public List<RegistroDeAcciones> getAccionesEnOrdenInverso() {
        List<RegistroDeAcciones> acciones = new ArrayList<>(pilaAcciones);
        Collections.reverse(acciones);
        return acciones;
    }

    /**
     * Obtiene la última acción realizada sin removerla del Stack. Utiliza
     * peek() del Stack para ver el elemento superior.
     *
     * @return la última acción registrada, o null si no hay acciones
     */
    public RegistroDeAcciones getUltimaAccion() {
        if (pilaAcciones.isEmpty()) {
            return null;
        }
        return pilaAcciones.peek();
    }
}
