/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Cliente;
import modelo.Prioridad;
import modelo.RegistroDeAcciones;
import modelo.SistemaDeGestion;
import vista.VentanaTechClassUC;

/**
 * Controlador MVC para el sistema de gestión de clientes TechClassUC. Gestiona
 * la lógica de negocio y coordina la comunicación entre el modelo
 * (SistemaDeGestion) y la vista (VentanaTechClassUC).
 *
 * Utiliza las siguientes estructuras de datos: - ArrayDeque: Cola de clientes
 * en espera - LinkedList: Historial de clientes atendidos - Stack: Pila de
 * acciones para deshacer
 *
 * @author young
 */
public class MVCTechClassUC {

    private SistemaDeGestion sistema;
    private VentanaTechClassUC vista;
    private DefaultTableModel modeloTabla;

    /**
     * Constructor del controlador MVC. Inicializa el sistema de gestión,
     * configura los componentes y establece los listeners de eventos.
     *
     * @param vista la ventana principal de la aplicación
     */
    public MVCTechClassUC(VentanaTechClassUC vista) {
        this.vista = vista;
        this.sistema = new SistemaDeGestion();
        inicializarComponentes();
        configurarEventos();
    }

    /**
     * Inicializa y muestra la ventana principal. Configura el título, centra la
     * ventana y la hace visible.
     */
    public void iniciar() {
        this.vista.setTitle("ORGANIZADOR DE TURNOS - TechClassUC Solutions");
        this.vista.setLocationRelativeTo(null);
        this.vista.setVisible(true);
    }

    /**
     * Inicializa todos los componentes de la interfaz gráfica. Carga los
     * ComboBox y configura el modelo de la tabla.
     */
    private void inicializarComponentes() {

        // Cargar tipos de solicitud en el ComboBox de RECEPCIÓN
        vista.getComboRecepcion().removeAllItems();
        vista.getComboRecepcion().addItem("Soporte");
        vista.getComboRecepcion().addItem("Mantenimiento");
        vista.getComboRecepcion().addItem("Reclamo");

        // Cargar combo de filtro en INFORME
        vista.getComboFiltro().removeAllItems();
        vista.getComboFiltro().addItem("Todos");
        vista.getComboFiltro().addItem("Soporte");
        vista.getComboFiltro().addItem("Mantenimiento");
        vista.getComboFiltro().addItem("Reclamo");

        // Configurar modelo de tabla
        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Tipo Solicitud", "Prioridad", "Problema", "Fecha"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vista.getTablaClientes().setModel(modeloTabla);
    }

    /**
     * Configura los listeners de eventos para todos los botones y componentes.
     * Utiliza expresiones lambda para simplificar los ActionListeners.
     */
    private void configurarEventos() {
        // PESTAÑA RECEPCIÓN
        vista.getBotonAgregar().addActionListener(e -> agregarCliente());
        vista.getBotonEliminar().addActionListener(e -> eliminarCliente());
        vista.getBotonContinuar().addActionListener(e -> continuarADiagnostico());
        vista.getBotonDeshacer().addActionListener(e -> deshacerAccion());

        // PESTAÑA DIAGNÓSTICO
        vista.getBotonAtender().addActionListener(e -> atenderCliente());
        vista.getBotonDeshacer2().addActionListener(e -> deshacerAccion());

        // PESTAÑA INFORME
        vista.getComboFiltro().addActionListener(e -> filtrarPorTipo());

        // Filtrar por ID (al escribir)
        vista.getFiltrarID().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarPorId();
            }
        });
    }

    // ==================== MÉTODOS DE RECEPCIÓN ====================
    /**
     * Agrega un nuevo cliente al sistema. Valida los campos del formulario,
     * crea un nuevo cliente y lo agrega a la cola de espera (ArrayDeque).
     * También registra la acción en el Stack para poder deshacerla.
     */
    private void agregarCliente() {
        try {
            String id = vista.getCampoId().getText().trim();
            String nombre = vista.getCampoNombre().getText().trim();
            String tipoSolicitud = (String) vista.getComboRecepcion().getSelectedItem();
            String problema = vista.getProblemaDelCliente().getText().trim();

            Prioridad prioridad;
            if (vista.getRadioPrioridadUrgente().isSelected()) {
                prioridad = Prioridad.URGENTE;
            } else {
                prioridad = Prioridad.NORMAL; // Por defecto o si Normal está seleccionado
            }

            // Validaciones
            if (id.isEmpty() || nombre.isEmpty() || problema.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obtener fecha del calendario
            Date fechaSeleccionada = vista.getCalendario().getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = sdf.format(fechaSeleccionada);

            // Crear cliente y agregarlo al sistema (usando ArrayDeque)
            Cliente nuevoCliente = new Cliente(id, nombre, tipoSolicitud, prioridad, problema, fecha);
            sistema.agregarCliente(nuevoCliente);

            // Agregar a la tabla
            modeloTabla.addRow(new Object[]{
                id, nombre, tipoSolicitud, prioridad.toString(), problema, fecha
            });

            // Limpiar campos
            limpiarCamposRecepcion();

            // Actualizar vistas
            actualizarAreaEspera();
            actualizarInformeAcciones();

            JOptionPane.showMessageDialog(vista, "Cliente agregado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al agregar cliente: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina un cliente seleccionado de la cola de espera. Remueve el cliente
     * del ArrayDeque y actualiza la tabla. Registra la acción en el Stack para
     * poder deshacerla.
     */
    private void eliminarCliente() {
        int filaSeleccionada = vista.getTablaClientes().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un cliente de la tabla",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idCliente = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        // Eliminar de la cola (ArrayDeque)
        if (sistema.eliminarClienteDeCola(idCliente)) {
            modeloTabla.removeRow(filaSeleccionada);
            actualizarAreaEspera();
            actualizarInformeAcciones();
            JOptionPane.showMessageDialog(vista, "Cliente eliminado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo eliminar el cliente",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mueve el siguiente cliente de la cola de espera a diagnóstico. Utiliza
     * poll() en el ArrayDeque para obtener el primer cliente. Verifica que no
     * haya un cliente ya en diagnóstico antes de continuar.
     */
    private void continuarADiagnostico() {
        // Verificar si ya hay un cliente en diagnóstico
        if (sistema.getClienteEnAtencion() != null) {
            JOptionPane.showMessageDialog(vista,
                    "Ya hay un cliente en diagnóstico. Debe atenderlo primero antes de continuar con otro.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (sistema.getColaClientes().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay clientes en espera",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Atender el siguiente cliente de la cola (poll en ArrayDeque)
        Cliente cliente = sistema.atenderCliente();

        if (cliente != null) {
            //Buscar y eliminar la fila correcta por ID del cliente
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String idEnTabla = (String) modeloTabla.getValueAt(i, 0);
                if (idEnTabla.equals(cliente.getId())) {
                    modeloTabla.removeRow(i);
                    break;
                }
            }

            // Actualizar área de diagnóstico
            actualizarAreaDiagnostico(cliente);
            actualizarAreaEspera();
            actualizarInformeAcciones();

            String mensajePrioridad = cliente.getPrioridad() == Prioridad.URGENTE
                    ? " (Cliente URGENTE atendido por el sistema de prioridad 2:1)"
                    : " (Cliente NORMAL)";

            JOptionPane.showMessageDialog(vista, "Cliente pasó a diagnóstico",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Limpia todos los campos del formulario de recepción.
     */
    private void limpiarCamposRecepcion() {
        vista.getCampoId().setText("");
        vista.getCampoNombre().setText("");
        vista.getProblemaDelCliente().setText("");
        vista.getRadioPrioridadNormal().setSelected(true);
        vista.getComboRecepcion().setSelectedIndex(0);
    }

    // ==================== MÉTODOS DE DIAGNÓSTICO ====================
    /**
     * Finaliza la atención del cliente actual. Guarda el diagnóstico ingresado
     * y agrega el cliente al historial de atendidos (LinkedList). Registra la
     * acción "finalizar" en el Stack. Actualiza todas las vistas
     * correspondientes.
     */
    private void atenderCliente() {
        String diagnosticoTexto = vista.getDiagnostico().getText().trim();

        if (diagnosticoTexto.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Debe ingresar un diagnóstico",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente clienteEnAtencion = sistema.getClienteEnAtencion();

        if (clienteEnAtencion == null) {
            JOptionPane.showMessageDialog(vista, "No hay cliente en atención",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        clienteEnAtencion.setDiagnostico(diagnosticoTexto);

        // SOLUCIÓN: Agregar al historial si no está ya
        if (!sistema.getHistorialAtendidos().contains(clienteEnAtencion)) {
            sistema.getHistorialAtendidos().add(clienteEnAtencion);
        }

        // Registrar la acción "finalizar" ANTES de finalizarla
        sistema.registrarAccion("finalizar", clienteEnAtencion);

        sistema.finalizarAtencion();

        vista.getDiagnostico().setText("");
        vista.getAreaDeDiagnostico().setText("No hay cliente en atención actualmente");

        actualizarAreaEspera();
        actualizarInformeAcciones();
        actualizarReporteAtendidos();

        JOptionPane.showMessageDialog(vista, "Cliente atendido exitosamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Actualiza el área de diagnóstico con la información del cliente. Muestra
     * ID, nombre, tipo de solicitud, prioridad y problema.
     *
     * @param cliente el cliente cuya información se mostrará
     */
    private void actualizarAreaDiagnostico(Cliente cliente) {
        String info = "Cliente en atención:\n\n"
                + "ID: " + cliente.getId() + "\n"
                + "Nombre: " + cliente.getNombre() + "\n"
                + "Tipo de solicitud: " + cliente.getTipoSolicitud() + "\n"
                + "Prioridad: " + cliente.getPrioridad() + "\n"
                + "Problema: " + cliente.getProblema() + "\n";

        vista.getAreaDeDiagnostico().setText(info);
    }

    // ==================== MÉTODO DESHACER ====================
    /**
     * Deshace la última acción realizada en el sistema. Utiliza pop() en el
     * Stack para obtener la última acción. Restaura el estado anterior según el
     * tipo de acción: - "agregar": elimina el cliente de la cola y de la tabla
     * - "eliminar": devuelve el cliente a la cola y a la tabla - "atender":
     * devuelve el cliente de diagnóstico a la cola y a la tabla - "finalizar":
     * devuelve el cliente del historial a diagnóstico
     */
    private void deshacerAccion() {
        RegistroDeAcciones ultimaAccion = sistema.getUltimaAccion();

        if (ultimaAccion == null) {
            JOptionPane.showMessageDialog(vista, "No hay acciones para deshacer",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipoAccion = ultimaAccion.getTipoAccion();
        Cliente clienteAfectado = ultimaAccion.getCliente();

        // Deshacer la acción (pop del Stack)
        sistema.deshacerUltimaAccion();

        // Actualizar la interfaz según el tipo de acción
        switch (tipoAccion) {
            case "agregar":
                // Si se agregó un cliente, al deshacer se elimina de la tabla
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    if (modeloTabla.getValueAt(i, 0).equals(clienteAfectado.getId())) {
                        modeloTabla.removeRow(i);
                        break;
                    }
                }
                break;

            case "eliminar":
                // Si se eliminó un cliente, al deshacer se agrega de vuelta a la tabla
                modeloTabla.addRow(new Object[]{
                    clienteAfectado.getId(),
                    clienteAfectado.getNombre(),
                    clienteAfectado.getTipoSolicitud(),
                    clienteAfectado.getPrioridad(),
                    clienteAfectado.getProblema() != null ? clienteAfectado.getProblema() : "",
                    clienteAfectado.getFechaRegistro() != null ? clienteAfectado.getFechaRegistro() : ""
                });
                break;

            case "atender":
                // Si se envió a diagnóstico, al deshacer vuelve a la cola y a la tabla
                modeloTabla.insertRow(0, new Object[]{
                    clienteAfectado.getId(),
                    clienteAfectado.getNombre(),
                    clienteAfectado.getTipoSolicitud(),
                    clienteAfectado.getPrioridad(),
                    clienteAfectado.getProblema() != null ? clienteAfectado.getProblema() : "",
                    clienteAfectado.getFechaRegistro() != null ? clienteAfectado.getFechaRegistro() : ""
                });
                // Limpiar área de diagnóstico
                vista.getAreaDeDiagnostico().setText("No hay cliente en atención actualmente");
                vista.getDiagnostico().setText("");
                break;

            case "finalizar":
                // Si se finalizó atención, al deshacer vuelve a diagnóstico
                actualizarAreaDiagnostico(clienteAfectado);
                // Restaurar el diagnóstico que tenía
                if (clienteAfectado.getDiagnostico() != null) {
                    vista.getDiagnostico().setText(clienteAfectado.getDiagnostico());
                }
                break;
        }

        actualizarAreaEspera();
        actualizarInformeAcciones();
        actualizarReporteAtendidos();

        JOptionPane.showMessageDialog(vista, "Acción deshecha: " + tipoAccion,
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== MÉTODOS DE INFORME ====================
    /**
     * Filtra los clientes atendidos por tipo de solicitud. Busca en la
     * LinkedList de clientes atendidos.
     */
    private void filtrarPorTipo() {
        String tipoSeleccionado = (String) vista.getComboFiltro().getSelectedItem();

        if (tipoSeleccionado.equals("Todos")) {
            mostrarTodosLosAtendidos();
        } else {
            // Buscar en LinkedList de atendidos
            java.util.LinkedList<Cliente> clientesFiltrados = sistema.buscarPorTipoSolicitud(tipoSeleccionado);
            mostrarClientesFiltrados(clientesFiltrados);
        }
    }

    /**
     * Filtra y muestra un cliente específico por su ID. Busca en la LinkedList
     * de clientes atendidos. Si el campo está vacío, muestra todos los
     * clientes.
     */
    private void filtrarPorId() {
        String idBuscado = vista.getFiltrarID().getText().trim();

        if (idBuscado.isEmpty()) {
            mostrarTodosLosAtendidos();
            return;
        }

        // Buscar en LinkedList de atendidos
        Cliente cliente = sistema.buscarPorId(idBuscado);

        if (cliente != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("ID: ").append(cliente.getId()).append("\n");
            sb.append("Nombre: ").append(cliente.getNombre()).append("\n");
            sb.append("Tipo: ").append(cliente.getTipoSolicitud()).append("\n");
            sb.append("Prioridad: ").append(cliente.getPrioridad()).append("\n");

            if (cliente.getProblema() != null && !cliente.getProblema().isEmpty()) {
                sb.append("Problema: ").append(cliente.getProblema()).append("\n");
            }

            if (cliente.getDiagnostico() != null && !cliente.getDiagnostico().isEmpty()) {
                sb.append("Diagnóstico: ").append(cliente.getDiagnostico()).append("\n");
            }

            if (cliente.getHoraLlegada() != null) {
                sb.append("Hora llegada: ").append(formatearFechaHora(cliente.getHoraLlegada())).append("\n");
            }
            if (cliente.getHoraAtencion() != null) {
                sb.append("Hora atención: ").append(formatearFechaHora(cliente.getHoraAtencion())).append("\n");
            }

            vista.getReporteAtendidos().setText(sb.toString());
        } else {
            vista.getReporteAtendidos().setText("No se encontró cliente con ID: " + idBuscado);
        }
    }

    /**
     * Muestra todos los clientes atendidos sin filtrar. Obtiene la LinkedList
     * completa del historial.
     */
    private void mostrarTodosLosAtendidos() {
        // Obtener LinkedList de atendidos
        java.util.LinkedList<Cliente> atendidos = sistema.getHistorialAtendidos();
        mostrarClientesFiltrados(atendidos);
    }

    /**
     * Muestra una lista filtrada de clientes en el área de reporte. Recorre la
     * LinkedList y muestra los detalles de cada cliente.
     *
     * @param clientes la lista de clientes a mostrar
     */
    private void mostrarClientesFiltrados(java.util.LinkedList<Cliente> clientes) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HISTORIAL DE CLIENTES ATENDIDOS ===\n\n");

        if (clientes.isEmpty()) {
            sb.append("No hay clientes atendidos con este filtro\n");
        } else {
            // Recorrer LinkedList
            for (Cliente c : clientes) {
                sb.append("ID: ").append(c.getId()).append("\n");
                sb.append("Nombre: ").append(c.getNombre()).append("\n");
                sb.append("Tipo: ").append(c.getTipoSolicitud()).append("\n");
                sb.append("Prioridad: ").append(c.getPrioridad()).append("\n");

                if (c.getProblema() != null && !c.getProblema().isEmpty()) {
                    sb.append("Problema: ").append(c.getProblema()).append("\n");
                }

                if (c.getDiagnostico() != null && !c.getDiagnostico().isEmpty()) {
                    sb.append("Diagnóstico: ").append(c.getDiagnostico()).append("\n");
                }

                if (c.getHoraLlegada() != null) {
                    sb.append("Hora llegada: ").append(formatearFechaHora(c.getHoraLlegada())).append("\n");
                }
                if (c.getHoraAtencion() != null) {
                    sb.append("Hora atención: ").append(formatearFechaHora(c.getHoraAtencion())).append("\n");
                }

                sb.append("----------------------------------------\n\n");
            }
        }

        vista.getReporteAtendidos().setText(sb.toString());
    }

    // ==================== MÉTODOS DE ACTUALIZACIÓN DE VISTAS ====================
    /**
     * Actualiza el área de espera con el número actual de clientes. Muestra el
     * tamaño del ArrayDeque (cola de espera).
     */
    private void actualizarAreaEspera() {
        int clientesEnEspera = sistema.getTotalClientesEnEspera();
        vista.getAreaDeEspera().setText("Clientes en espera: " + clientesEnEspera);
    }

    /**
     * Actualiza el reporte de clientes atendidos y estadísticas. Muestra el
     * total de clientes atendidos (tamaño de LinkedList) y el promedio de
     * tiempo de atención.
     */
    private void actualizarReporteAtendidos() {
        mostrarTodosLosAtendidos();

        int totalAtendidos = sistema.getTotalClientesAtendidos();
        double promedioTiempo = sistema.getPromedioTiempoAtencion();

        String stats = "Total de clientes atendidos: " + totalAtendidos + "\n"
                + "Promedio tiempo de atención: " + String.format("%.2f", promedioTiempo) + " minutos";

        vista.getTotalAtendidos().setText(stats);
    }

    /**
     * Actualiza el informe de acciones realizadas. Obtiene las acciones del
     * Stack en orden inverso y las muestra en formato legible.
     */
    private void actualizarInformeAcciones() {
        // Obtener acciones del Stack en orden inverso
        java.util.List<RegistroDeAcciones> acciones = sistema.getAccionesEnOrdenInverso();
        StringBuilder sb = new StringBuilder();
        sb.append("=== HISTORIAL DE ACCIONES (Stack) ===\n\n");

        for (RegistroDeAcciones accion : acciones) {
            sb.append("Acción: ").append(accion.getTipoAccion().toUpperCase()).append("\n");
            sb.append("Cliente: ").append(accion.getCliente().getNombre());
            sb.append(" (ID: ").append(accion.getCliente().getId()).append(")\n");
            sb.append("Fecha/Hora: ").append(formatearFechaHora(accion.getFechaHora())).append("\n");
            sb.append("----------------------------------------\n\n");
        }

        vista.getInformeAcciones().setText(sb.toString());
    }

    // ==================== MÉTODO AUXILIAR ====================
    /**
     * Formatea una fecha y hora a un formato legible. Patrón: dd/MM/yyyy
     * HH:mm:ss Ejemplo: 25/11/2025 16:55:37
     *
     * @param fechaHora el LocalDateTime a formatear
     * @return la fecha y hora en formato String
     */
    private String formatearFechaHora(java.time.LocalDateTime fechaHora) {
        java.time.format.DateTimeFormatter formatter
                = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fechaHora.format(formatter);
    }
}
