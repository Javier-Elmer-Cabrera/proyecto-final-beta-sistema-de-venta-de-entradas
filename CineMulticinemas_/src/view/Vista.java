package view;

import controller.Controlador;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import model.Pelicula;
import model.Reserva;

/**
 * VISTA Ventana principal Swing. Solo se encarga de construir y actualizar la
 * interfaz grafica; toda la logica de negocio (arbol, cola, matriz de asientos)
 * vive en {@link Controlador}.
 */
public class Vista extends JFrame {

    private static final Color C_FONDO = new Color(15, 15, 25);
    private static final Color C_PANEL = new Color(25, 25, 40);
    private static final Color C_ACENTO = new Color(220, 38, 38);
    private static final Color C_ACENTO2 = new Color(251, 191, 36);
    private static final Color C_TEXTO = new Color(240, 240, 250);
    private static final Color C_TEXTO_DIM = new Color(140, 140, 160);
    private static final Color C_LIBRE = new Color(34, 197, 94);
    private static final Color C_OCUPADO = new Color(239, 68, 68);
    private static final Color C_SELEC = new Color(251, 191, 36);
    private static final Color C_CARD = new Color(30, 30, 48);
    private static final Color C_HOVER = new Color(45, 45, 68);

    private static final Font F_TITULO = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font F_SUB = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_MONO = new Font("Consolas", Font.BOLD, 13);

    private final Controlador controller;

    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JButton[][] botonesAsientos;
    private JLabel lblAsientoInfo;
    private JTextField txtNombreCliente;
    private JPanel gridAsientos;
    private DefaultListModel<String> modeloCola;
    private DefaultTableModel modeloCartelera;
    private DefaultTableModel modeloReporte;
    private JComboBox<String> cmbGenero;
    private JComboBox<String> cmbHorario;
    private boolean isUpdatingFilters = false;

    public Vista(Controlador controller) {
        this.controller = controller;
        configurarVentana();
        construirUI();
        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("Cinema System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 700);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_FONDO);
    }

    /**
     * Configura el layout principal usando CardLayout para alternar entre
     * pantallas.
     */
    private void construirUI() {
        setLayout(new BorderLayout());
        add(crearNavBar(), BorderLayout.WEST);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(C_FONDO);
        mainPanel.add(crearPanelCartelera(), "CARTELERA");
        mainPanel.add(crearPanelReserva(), "RESERVA");
        mainPanel.add(crearPanelCola(), "COLA");
        mainPanel.add(crearPanelReporte(), "REPORTE");
        add(mainPanel, BorderLayout.CENTER);
        cardLayout.show(mainPanel, "CARTELERA");
    }

    private JPanel crearNavBar() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(C_PANEL);
        nav.setPreferredSize(new Dimension(200, 0));
        nav.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(50, 50, 70)));

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(C_ACENTO);
        logoPanel.setMaximumSize(new Dimension(200, 80));
        logoPanel.setPreferredSize(new Dimension(200, 80));
        JLabel logo = new JLabel("CINEMA", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        logoPanel.add(logo, BorderLayout.CENTER);
        nav.add(logoPanel);
        nav.add(Box.createVerticalStrut(20));

        String[][] opciones = {
            {"Cartelera", "CARTELERA"},
            {"Reservar", "RESERVA"},
            {"Cola Pagos", "COLA"},
            {"Reporte", "REPORTE"},};
        for (String[] op : opciones) {
            nav.add(crearBotonNav(op[0], op[1]));
            nav.add(Box.createVerticalStrut(4));
        }

        nav.add(Box.createVerticalGlue());
        JLabel version = new JLabel("v2.0 MVC", SwingConstants.CENTER);
        version.setFont(F_SMALL);
        version.setForeground(C_TEXTO_DIM);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        nav.add(version);
        nav.add(Box.createVerticalStrut(12));
        return nav;
    }

    private JButton crearBotonNav(String texto, String card) {
        JButton btn = new JButton(texto);
        btn.setFont(F_SUB);
        btn.setForeground(C_TEXTO);
        btn.setBackground(C_PANEL);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(200, 48));
        btn.setPreferredSize(new Dimension(200, 48));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(C_HOVER);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(C_PANEL);
            }
        });
        btn.addActionListener(e -> {
            cardLayout.show(mainPanel, card);
            if (card.equals("COLA")) {
                refrescarCola();
            }
            if (card.equals("REPORTE")) {
                actualizarTablaReporte();
            }
        });
        return btn;
    }

    /**
     * Construye la pantalla de cartelera con sus filtros y tabla de peliculas.
     */
    private JPanel crearPanelCartelera() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(C_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel norte = new JPanel(new BorderLayout(0, 12));
        norte.setBackground(C_FONDO);
        norte.add(crearEncabezado("Cartelera de Peliculas", "Ordenadas por Genero/Horario/Sala (recorrido InOrder BST)"), BorderLayout.NORTH);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        filtros.setBackground(C_CARD);
        filtros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 70)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel lblFiltro = new JLabel("Buscar por:");
        lblFiltro.setFont(F_SUB);
        lblFiltro.setForeground(C_ACENTO2);
        filtros.add(lblFiltro);

        JLabel lblGenero = new JLabel("Genero:");
        lblGenero.setFont(F_NORMAL);
        lblGenero.setForeground(C_TEXTO);
        filtros.add(lblGenero);

        cmbGenero = new JComboBox<>();
        cmbGenero.addItem("Todos");
        for (String g : controller.obtenerGeneros()) {
            cmbGenero.addItem(g);
        }
        crearComboEstilizado(cmbGenero);
        cmbGenero.addActionListener(e -> {
            if (isUpdatingFilters) {
                return;
            }
            isUpdatingFilters = true;
            cmbHorario.setSelectedIndex(0);
            actualizarTablaCartelera();
            isUpdatingFilters = false;
        });
        filtros.add(cmbGenero);

        filtros.add(Box.createHorizontalStrut(8));

        JLabel lblHorario = new JLabel("Horario:");
        lblHorario.setFont(F_NORMAL);
        lblHorario.setForeground(C_TEXTO);
        filtros.add(lblHorario);

        cmbHorario = new JComboBox<>();
        cmbHorario.addItem("Todos");
        for (String h : controller.obtenerHorarios()) {
            cmbHorario.addItem(h);
        }
        crearComboEstilizado(cmbHorario);
        cmbHorario.addActionListener(e -> {
            if (isUpdatingFilters) {
                return;
            }
            isUpdatingFilters = true;
            cmbGenero.setSelectedIndex(0);
            actualizarTablaCartelera();
            isUpdatingFilters = false;
        });
        filtros.add(cmbHorario);

        norte.add(filtros, BorderLayout.SOUTH);
        panel.add(norte, BorderLayout.NORTH);

        String[] columnas = {"ID", "Titulo", "Genero", "Horario", "Sala"};
        modeloCartelera = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        for (Pelicula p : controller.obtenerCartelera()) {
            modeloCartelera.addRow(new Object[]{p.id, p.titulo, p.genero, p.horario, p.sala.getNombre()});
        }

        JTable tabla = new JTable(modeloCartelera);
        estilizarTabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(C_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));
        panel.add(scroll, BorderLayout.CENTER);

        JButton btn = crearBotonPrimario("Ir a Reservar un Asiento");
        btn.addActionListener(e -> cardLayout.show(mainPanel, "RESERVA"));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(C_FONDO);
        south.add(btn);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Refresca la tabla de cartelera segun los filtros seleccionados.
     */
    private void actualizarTablaCartelera() {
        String genero = (String) cmbGenero.getSelectedItem();
        String horario = (String) cmbHorario.getSelectedItem();

        List<Pelicula> lista;
        if (genero != null && !genero.equals("Todos")) {
            lista = controller.buscarPorGenero(genero);
        } else if (horario != null && !horario.equals("Todos")) {
            lista = controller.buscarPorHorario(horario);
        } else {
            lista = controller.obtenerCartelera();
        }

        modeloCartelera.setRowCount(0);
        for (Pelicula p : lista) {
            modeloCartelera.addRow(new Object[]{p.id, p.titulo, p.genero, p.horario, p.sala.getNombre()});
        }
    }

    /**
     * Aplica estilos oscuros a un JComboBox.
     */
    private void crearComboEstilizado(JComboBox<String> cmb) {
        Color C_TEXTO = new Color(0, 0, 0);
        cmb.setFont(F_NORMAL);
        cmb.setBackground(C_CARD);
        cmb.setForeground(C_TEXTO);
        cmb.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 90)));
        cmb.setFocusable(false);
        cmb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Construye la pantalla interactiva para seleccionar pelicula y hacer clic
     * en un asiento.
     */
    private JPanel crearPanelReserva() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(C_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        panel.add(crearEncabezado("Reservar Asiento", "Selecciona una pelicula, luego haz clic en un asiento libre"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(C_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 12);
        centro.add(crearPanelSeleccionPelicula(), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 0);
        centro.add(crearPanelMapaAsientos(), gbc);
        panel.add(centro, BorderLayout.CENTER);
        panel.add(crearPanelFormularioReserva(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelSeleccionPelicula() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(C_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 70)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel titulo = new JLabel("Selecciona una Pelicula");
        titulo.setFont(F_SUB);
        titulo.setForeground(C_ACENTO2);
        p.add(titulo, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(C_CARD);

        for (Pelicula peli : controller.obtenerCartelera()) {
            lista.add(crearCardPelicula(peli));
            lista.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(lista);
        scroll.getViewport().setBackground(C_CARD);
        scroll.setBorder(null);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearCardPelicula(Pelicula peli) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(new Color(35, 35, 55));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 55, 80)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel id = new JLabel("#" + peli.id);
        id.setFont(new Font("Segoe UI", Font.BOLD, 18));
        id.setForeground(C_ACENTO);
        id.setPreferredSize(new Dimension(40, 40));
        card.add(id, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel nombre = new JLabel(peli.titulo);
        nombre.setFont(F_SUB);
        nombre.setForeground(C_TEXTO);
        JLabel detalle = new JLabel(peli.genero + "  |  " + peli.horario + "  |  " + peli.sala.getNombre());
        detalle.setFont(F_SMALL);
        detalle.setForeground(C_TEXTO_DIM);
        info.add(nombre);
        info.add(detalle);
        card.add(info, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                seleccionarPelicula(peli);
            }

            public void mouseEntered(MouseEvent e) {
                card.setBackground(C_HOVER);
            }

            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(35, 35, 55));
            }
        });
        return card;
    }

    private JPanel crearPanelMapaAsientos() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(C_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 70)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel titulo = new JLabel("Mapa de Asientos", SwingConstants.CENTER);
        titulo.setFont(F_SUB);
        titulo.setForeground(C_ACENTO2);
        p.add(titulo, BorderLayout.NORTH);

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        leyenda.setBackground(C_CARD);
        leyenda.add(crearItemLeyenda(C_LIBRE, "Libre"));
        leyenda.add(crearItemLeyenda(C_OCUPADO, "Ocupado"));
        leyenda.add(crearItemLeyenda(C_SELEC, "Seleccionado"));
        p.add(leyenda, BorderLayout.SOUTH);

        gridAsientos = new JPanel(new GridBagLayout());
        gridAsientos.setBackground(C_CARD);

        JLabel pantalla = new JLabel("PANTALLA", SwingConstants.CENTER);
        pantalla.setFont(F_SMALL);
        pantalla.setForeground(C_ACENTO2);
        pantalla.setOpaque(true);
        pantalla.setBackground(new Color(40, 30, 10));
        pantalla.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        lblAsientoInfo = new JLabel("Selecciona una pelicula primero", SwingConstants.CENTER);
        lblAsientoInfo.setFont(F_NORMAL);
        lblAsientoInfo.setForeground(C_TEXTO_DIM);

        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(C_CARD);
        wrapper.add(pantalla, BorderLayout.NORTH);
        wrapper.add(gridAsientos, BorderLayout.CENTER);
        wrapper.add(lblAsientoInfo, BorderLayout.SOUTH);
        p.add(wrapper, BorderLayout.CENTER);
        return p;
    }

    private void seleccionarPelicula(Pelicula peli) {
        try {
            controller.seleccionarPelicula(peli);
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        gridAsientos.removeAll();
        botonesAsientos = new JButton[peli.sala.getFILAS()][peli.sala.getCOLUMNAS()];

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);

        for (int c = 0; c < peli.sala.getCOLUMNAS(); c++) {
            gbc.gridy = 0;
            gbc.gridx = c + 1;
            JLabel lbl = new JLabel(String.valueOf(c + 1), SwingConstants.CENTER);
            lbl.setFont(F_SMALL);
            lbl.setForeground(C_TEXTO_DIM);
            lbl.setPreferredSize(new Dimension(44, 20));
            gridAsientos.add(lbl, gbc);
        }

        for (int f = 0; f < peli.sala.getFILAS(); f++) {
            gbc.gridx = 0;
            gbc.gridy = f + 1;
            JLabel lblFila = new JLabel(String.valueOf((char) ('A' + f)), SwingConstants.CENTER);
            lblFila.setFont(F_MONO);
            lblFila.setForeground(C_TEXTO_DIM);
            lblFila.setPreferredSize(new Dimension(24, 44));
            gridAsientos.add(lblFila, gbc);

            for (int c = 0; c < peli.sala.getCOLUMNAS(); c++) {
                JButton btn = crearBotonAsiento(f, c, peli);
                botonesAsientos[f][c] = btn;
                gbc.gridx = c + 1;
                gridAsientos.add(btn, gbc);
            }
        }

        lblAsientoInfo.setText("Pelicula: " + peli.titulo + "  |  Haz clic en un asiento libre");
        lblAsientoInfo.setForeground(C_TEXTO);
        gridAsientos.revalidate();
        gridAsientos.repaint();
    }

    private JButton crearBotonAsiento(int f, int c, Pelicula peli) {
        boolean libre = peli.sala.verificarAsiento(f, c);
        JButton btn = new JButton(libre ? "O" : "X");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(44, 44));
        btn.setFocusPainted(false);
        btn.setEnabled(libre);
        btn.setBackground(libre ? C_LIBRE : C_OCUPADO);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(libre ? C_LIBRE.darker() : C_OCUPADO.darker(), 1));
        btn.setCursor(libre ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

        if (libre) {
            btn.addActionListener(e -> seleccionarAsiento(f, c));
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (controller.getFilaSeleccionada() != f || controller.getColSeleccionada() != c) {
                        btn.setBackground(C_LIBRE.darker());

                    }
                }

                public void mouseExited(MouseEvent e) {
                    if (controller.getFilaSeleccionada() != f || controller.getColSeleccionada() != c) {
                        btn.setBackground(C_LIBRE);

                    }
                }
            });
        }
        return btn;
    }

    private void seleccionarAsiento(int f, int c) {
        int filaAnterior = controller.getFilaSeleccionada();
        int colAnterior = controller.getColSeleccionada();

        try {
            controller.seleccionarAsiento(f, c);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        if (filaAnterior >= 0 && colAnterior >= 0) {
            JButton ant = botonesAsientos[filaAnterior][colAnterior];
            ant.setBackground(C_LIBRE);
            ant.setForeground(Color.WHITE);
            ant.setBorder(BorderFactory.createLineBorder(C_LIBRE.darker(), 1));
        }
        JButton btn = botonesAsientos[f][c];
        btn.setBackground(C_SELEC);
        btn.setForeground(C_FONDO);
        btn.setBorder(BorderFactory.createLineBorder(C_SELEC.darker(), 2));
        lblAsientoInfo.setText("Seleccionado: Fila " + (char) ('A' + f) + ", Columna " + (c + 1));
        lblAsientoInfo.setForeground(C_ACENTO2);
    }

    private JPanel crearPanelFormularioReserva() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        p.setBackground(C_FONDO);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 50, 70)));

        JLabel lbl = new JLabel("Nombre del cliente:");
        lbl.setFont(F_NORMAL);
        lbl.setForeground(C_TEXTO);
        p.add(lbl);

        txtNombreCliente = new JTextField(18);
        estilizarTextField(txtNombreCliente);
        p.add(txtNombreCliente);

        JButton btnConfirmar = crearBotonPrimario("Confirmar Reserva");
        btnConfirmar.addActionListener(e -> procesarReserva());
        p.add(btnConfirmar);
        return p;
    }

    /**
     * Extrae los datos del cliente y solicita al controlador confirmar la
     * reserva.
     */
    private void procesarReserva() {
        int fila = controller.getFilaSeleccionada();
        int col = controller.getColSeleccionada();
        String cliente = txtNombreCliente.getText().trim();

        Reserva r;
        try {
            r = controller.confirmarReserva(cliente);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        JButton btn = botonesAsientos[fila][col];
        btn.setBackground(C_OCUPADO);
        btn.setForeground(Color.WHITE);
        btn.setText("X");
        btn.setEnabled(false);
        btn.setBorder(BorderFactory.createLineBorder(C_OCUPADO.darker(), 1));

        mostrarExito("Reserva registrada!\nCliente: " + r.cliente
                + "\nPelicula: " + r.pelicula.titulo
                + "\nAsiento: Fila " + (char) ('A' + r.fila) + ", Columna " + (r.columna + 1));

        txtNombreCliente.setText("");
        lblAsientoInfo.setText("Asiento reservado. Selecciona otro si lo deseas.");
    }

    /**
     * Construye la pantalla que muestra las reservas pendientes ordenadas segun
     * llegaron.
     */
    private JPanel crearPanelCola() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(C_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        panel.add(crearEncabezado("Cola de Confirmacion de Pagos", "Politica FIFO: la reserva mas antigua se procesa primero"), BorderLayout.NORTH);

        modeloCola = new DefaultListModel<>();
        JList<String> listaCola = new JList<>(modeloCola);
        listaCola.setBackground(C_CARD);
        listaCola.setForeground(C_TEXTO);
        listaCola.setFont(F_NORMAL);
        listaCola.setSelectionBackground(C_ACENTO);
        listaCola.setFixedCellHeight(52);
        listaCola.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, i, sel, foc);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
                if (!sel) {
                    lbl.setBackground(i % 2 == 0 ? C_CARD : new Color(35, 35, 55));
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(listaCola);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));
        scroll.getViewport().setBackground(C_CARD);
        panel.add(scroll, BorderLayout.CENTER);

        JButton btnProcesar = crearBotonPrimario("Procesar Pago (Desencolar)");
        btnProcesar.addActionListener(e -> procesarPago());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(C_FONDO);
        south.add(btnProcesar);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void refrescarCola() {
        modeloCola.clear();
        for (Reserva r : controller.obtenerColaReservas()) {
            modeloCola.addElement("Cliente: " + r.cliente + "  |  Pelicula: " + r.pelicula.titulo
                    + "  |  Asiento: Fila " + (char) ('A' + r.fila) + (r.columna + 1) + "  |  PENDIENTE");
        }
    }

    /**
     * Solicita al controlador desencolar y procesar el pago de la reserva mas
     * antigua.
     */
    private void procesarPago() {
        Reserva res;
        try {
            res = controller.procesarPago();
        } catch (NoSuchElementException ex) {
            mostrarError(ex.getMessage());
            return;
        }

        String boleto
                = "================================\n"
                + "       BOLETO EMITIDO\n"
                + "================================\n"
                + "Cliente: " + res.cliente + "\n"
                + "Pelicula: " + res.pelicula.titulo + "\n"
                + "Horario: " + res.pelicula.horario + "\n"
                + "Sala: " + res.pelicula.sala.getNombre() + "\n"
                + "Asiento: Fila " + (char) ('A' + res.fila) + ", Columna " + (res.columna + 1) + "\n"
                + "Estado: COMPRA CONFIRMADA\n"
                + "================================";

        JTextArea ta = new JTextArea(boleto);
        ta.setFont(F_MONO);
        ta.setEditable(false);
        ta.setBackground(C_PANEL);
        ta.setForeground(C_TEXTO);
        ta.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JOptionPane.showMessageDialog(this, ta, "Boleto Emitido", JOptionPane.INFORMATION_MESSAGE);
        refrescarCola();
    }

    /**
     * Construye la pantalla que visualiza las estadisticas y ocupacion de las
     * salas.
     */
    private JPanel crearPanelReporte() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(C_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        panel.add(crearEncabezado("Reporte de Ocupacion", "Estadisticas en tiempo real de todas las salas"), BorderLayout.NORTH);

        String[] cols = {"Sala", "Pelicula", "Vendidos", "Disponibles", "Total", "Ocupacion %"};
        modeloReporte = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tablaReporte = new JTable(modeloReporte);
        estilizarTabla(tablaReporte);

        tablaReporte.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel && v != null) {
                    try {
                        double pct = Double.parseDouble(v.toString().replace("%", "").trim());
                        lbl.setForeground(pct >= 80 ? C_OCUPADO : pct >= 50 ? C_ACENTO2 : C_LIBRE);
                    } catch (NumberFormatException ignored) {
                    }
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaReporte);
        scroll.getViewport().setBackground(C_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));
        panel.add(scroll, BorderLayout.CENTER);

        JButton btnRefrescar = crearBotonSecundario("Actualizar Reporte");
        btnRefrescar.addActionListener(e -> actualizarTablaReporte());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(C_FONDO);
        south.add(btnRefrescar);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void actualizarTablaReporte() {
        modeloReporte.setRowCount(0);
        for (Object[] fila : controller.generarReporteOcupacion()) {
            modeloReporte.addRow(fila);
        }
    }

    private JPanel crearEncabezado(String titulo, String sub) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2));
        p.setBackground(C_FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        JLabel t = new JLabel(titulo);
        t.setFont(F_TITULO);
        t.setForeground(C_TEXTO);
        JLabel s = new JLabel(sub);
        s.setFont(F_NORMAL);
        s.setForeground(C_TEXTO_DIM);
        p.add(t);
        p.add(s);
        return p;
    }

    private JPanel crearItemLeyenda(Color color, String texto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(C_CARD);
        JLabel cuadro = new JLabel("  ");
        cuadro.setOpaque(true);
        cuadro.setBackground(color);
        cuadro.setPreferredSize(new Dimension(16, 16));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(F_SMALL);
        lbl.setForeground(C_TEXTO_DIM);
        p.add(cuadro);
        p.add(lbl);
        return p;
    }

    private JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(F_SUB);
        btn.setBackground(C_ACENTO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(C_ACENTO.darker());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(C_ACENTO);
            }
        });
        return btn;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(F_SUB);
        btn.setBackground(C_CARD);
        btn.setForeground(C_ACENTO2);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_ACENTO2, 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void estilizarTabla(JTable t) {
        t.setBackground(C_PANEL);
        t.setForeground(C_TEXTO);
        t.setFont(F_NORMAL);
        t.setRowHeight(36);
        t.setGridColor(new Color(45, 45, 65));
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(C_ACENTO);
        t.setSelectionForeground(Color.WHITE);
        t.setIntercellSpacing(new Dimension(0, 1));
        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(20, 20, 35));
        h.setForeground(C_ACENTO2);
        h.setFont(F_SUB);
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_ACENTO));
        h.setReorderingAllowed(false);
    }

    private void estilizarTextField(JTextField tf) {
        tf.setBackground(C_CARD);
        tf.setForeground(C_TEXTO);
        tf.setCaretColor(C_TEXTO);
        tf.setFont(F_NORMAL);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 90)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Exito", JOptionPane.INFORMATION_MESSAGE);
    }
}
