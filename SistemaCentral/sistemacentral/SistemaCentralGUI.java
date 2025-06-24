package sistemacentral;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SistemaCentralGUI extends JFrame {

    // Componentes de la interfaz
    private JButton btnIniciar;
    private JButton btnDetener;
    private JButton btnLimpiarConsola;
    private JButton btnMinimizar;
    private JButton btnConfiguracion;
    private JTextPane areaConsola; // Cambiado a JTextPane para colores
    private JLabel lblEstado;
    private JLabel lblTiempo;
    private JLabel lblMemoria;
    private JProgressBar progressBar;
    private JProgressBar memoryBar;
    private StyledDocument consolaDoc;

    // Control del sistema
    private Thread sistemaThread;
    private MailApplication mailApp;
    private boolean sistemaEjecutandose = false;
    private Timer timerActualizacion;
    private Timer animacionTimer;
    private long tiempoInicio;
    private int animacionContador = 0;

    // Redirección de consola
    private PrintStream consolaOriginal;
    private PrintStream consolaPersonalizada;

    // Paleta de colores moderna
    private static final Color COLOR_PRIMARIO = new Color(74, 144, 226);      // Azul moderno
    private static final Color COLOR_SECUNDARIO = new Color(52, 199, 89);     // Verde moderno
    private static final Color COLOR_ACENTO = new Color(255, 149, 0);         // Naranja vibrante
    private static final Color COLOR_FONDO = new Color(248, 249, 250);        // Gris muy claro
    private static final Color COLOR_FONDO_CARD = Color.WHITE;                // Blanco puro
    private static final Color COLOR_TEXTO = new Color(28, 30, 33);           // Casi negro
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(101, 103, 107); // Gris medio
    private static final Color COLOR_EXITO = new Color(40, 167, 69);          // Verde éxito
    private static final Color COLOR_ERROR = new Color(220, 53, 69);          // Rojo error
    private static final Color COLOR_WARNING = new Color(255, 193, 7);        // Amarillo warning
    private static final Color COLOR_SOMBRA = new Color(0, 0, 0, 15);         // Sombra sutil

    // Estilos de texto para la consola
    private SimpleAttributeSet estiloNormal;
    private SimpleAttributeSet estiloComando;
    private SimpleAttributeSet estiloEmail;
    private SimpleAttributeSet estiloError;
    private SimpleAttributeSet estiloExito;
    private SimpleAttributeSet estiloTimestamp;
    private SimpleAttributeSet estiloInfo;

    public SistemaCentralGUI() {
        initStyles();
        initComponents();
        setupConsoleRedirection();
        setupTimers();
        setupAnimations();
    }

    private void initStyles() {
        // Inicializar estilos para la consola
        estiloNormal = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloNormal, new Color(233, 237, 237));
        StyleConstants.setFontFamily(estiloNormal, "Fira Code");
        StyleConstants.setFontSize(estiloNormal, 12);

        estiloTimestamp = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloTimestamp, new Color(108, 117, 125));
        StyleConstants.setFontFamily(estiloTimestamp, "Fira Code");
        StyleConstants.setFontSize(estiloTimestamp, 11);

        estiloComando = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloComando, new Color(138, 201, 38));
        StyleConstants.setBold(estiloComando, true);
        StyleConstants.setFontFamily(estiloComando, "Fira Code");
        StyleConstants.setFontSize(estiloComando, 12);

        estiloEmail = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloEmail, new Color(255, 206, 84));
        StyleConstants.setBold(estiloEmail, true);
        StyleConstants.setFontFamily(estiloEmail, "Fira Code");
        StyleConstants.setFontSize(estiloEmail, 12);

        estiloError = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloError, new Color(239, 83, 80));
        StyleConstants.setBold(estiloError, true);
        StyleConstants.setFontFamily(estiloError, "Fira Code");
        StyleConstants.setFontSize(estiloError, 12);

        estiloExito = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloExito, new Color(102, 187, 106));
        StyleConstants.setBold(estiloExito, true);
        StyleConstants.setFontFamily(estiloExito, "Fira Code");
        StyleConstants.setFontSize(estiloExito, 12);

        estiloInfo = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloInfo, new Color(66, 165, 245));
        StyleConstants.setBold(estiloInfo, true);
        StyleConstants.setFontFamily(estiloInfo, "Fira Code");
        StyleConstants.setFontSize(estiloInfo, 12);
    }

    private void initComponents() {
        // Configuración de la ventana principal
        setTitle("AgroVeterinaria La Fortaleza - Control Panel v2.1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        setUndecorated(false);

        // Establecer icono de la ventana
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("https://i.postimg.cc/tCc62QzQ/LOGO.png"));
        } catch (Exception e) {
            // Si no se puede cargar el icono, continuar sin él
        }

        // Layout principal con márgenes
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(COLOR_FONDO);

        // Panel principal con padding
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel panelHeader = crearPanelHeader();
        panelPrincipal.add(panelHeader, BorderLayout.NORTH);

        // Panel superior (controles)
        JPanel panelControles = crearPanelControles();
        panelPrincipal.add(panelControles, BorderLayout.CENTER);

        // Panel inferior (estado)
        JPanel panelInferior = crearPanelEstado();
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);

        // Estilo general
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
        });
    }

    private JPanel crearPanelHeader() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(COLOR_FONDO_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new SombaBorder(),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Título principal
        JLabel lblTitulo = new JLabel("Agroveterinaria La Fortaleza");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_TEXTO);

        JLabel lblSubtitulo = new JLabel("Control Panel Avanzado - Gestión de Emails");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(COLOR_TEXTO_SECUNDARIO);

        JPanel panelTitulos = new JPanel(new BorderLayout(0, 5));
        panelTitulos.setBackground(COLOR_FONDO_CARD);
        panelTitulos.add(lblTitulo, BorderLayout.NORTH);
        panelTitulos.add(lblSubtitulo, BorderLayout.CENTER);

        // Botones del header
        JPanel panelBotonesHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotonesHeader.setBackground(COLOR_FONDO_CARD);

        btnConfiguracion = crearBotonCircular("⚙️", COLOR_TEXTO_SECUNDARIO);
        btnConfiguracion.setToolTipText("Configuración");
        btnConfiguracion.addActionListener(e -> mostrarConfiguracion());

        btnMinimizar = crearBotonCircular("−", COLOR_TEXTO_SECUNDARIO);
        btnMinimizar.setToolTipText("Minimizar");
        btnMinimizar.addActionListener(e -> setState(JFrame.ICONIFIED));

        panelBotonesHeader.add(btnConfiguracion);
        panelBotonesHeader.add(btnMinimizar);

        panel.add(panelTitulos, BorderLayout.WEST);
        panel.add(panelBotonesHeader, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelControles() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 15));
        panelPrincipal.setBackground(COLOR_FONDO);

        // Panel de controles superior
        JPanel panelControles = new JPanel(new BorderLayout(15, 15));
        panelControles.setBackground(COLOR_FONDO_CARD);
        panelControles.setBorder(BorderFactory.createCompoundBorder(
                new SombaBorder(),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Botones principales
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setBackground(COLOR_FONDO_CARD);

        btnIniciar = crearBotonModerno("INICIAR SISTEMA", "", COLOR_SECUNDARIO);
        btnIniciar.addActionListener(e -> iniciarSistema());

        btnDetener = crearBotonModerno("DETENER SISTEMA", "", COLOR_ERROR);
        btnDetener.setEnabled(false);
        btnDetener.addActionListener(e -> detenerSistema());

        btnLimpiarConsola = crearBotonModerno("LIMPIAR CONSOLA", "", COLOR_ACENTO);
        btnLimpiarConsola.addActionListener(e -> limpiarConsola());

        panelBotones.add(btnIniciar);
        panelBotones.add(btnDetener);
        panelBotones.add(btnLimpiarConsola);

        // Barras de progreso
        JPanel panelProgreso = new JPanel(new GridLayout(2, 1, 0, 10));
        panelProgreso.setBackground(COLOR_FONDO_CARD);

        // Barra principal
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Sistema detenido");
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        progressBar.setForeground(COLOR_PRIMARIO);
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        progressBar.setPreferredSize(new Dimension(0, 30));

        // Barra de memoria
        memoryBar = new JProgressBar(0, 100);
        memoryBar.setStringPainted(true);
        memoryBar.setString("Memoria: 0%");
        memoryBar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        memoryBar.setForeground(COLOR_WARNING);
        memoryBar.setBackground(new Color(240, 240, 240));
        memoryBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        memoryBar.setPreferredSize(new Dimension(0, 20));

        panelProgreso.add(progressBar);
        panelProgreso.add(memoryBar);

        panelControles.add(panelBotones, BorderLayout.CENTER);
        panelControles.add(panelProgreso, BorderLayout.SOUTH);

        // Panel de consola
        JPanel panelConsola = crearPanelConsola();

        panelPrincipal.add(panelControles, BorderLayout.NORTH);
        panelPrincipal.add(panelConsola, BorderLayout.CENTER);

        return panelPrincipal;
    }

    private JPanel crearPanelConsola() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(COLOR_FONDO_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new SombaBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header de la consola
        JPanel headerConsola = new JPanel(new BorderLayout(10, 0));
        headerConsola.setBackground(new Color(45, 45, 45));
        headerConsola.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblTituloConsola = new JLabel("Terminal del Sistema");
        lblTituloConsola.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTituloConsola.setForeground(Color.WHITE);

        // Indicadores de la consola
        JPanel indicadores = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        indicadores.setBackground(new Color(45, 45, 45));

        JLabel punto1 = new JLabel("●");
        punto1.setForeground(COLOR_ERROR);
        punto1.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel punto2 = new JLabel("●");
        punto2.setForeground(COLOR_WARNING);
        punto2.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel punto3 = new JLabel("●");
        punto3.setForeground(COLOR_SECUNDARIO);
        punto3.setFont(new Font("Arial", Font.BOLD, 14));

        indicadores.add(punto1);
        indicadores.add(punto2);
        indicadores.add(punto3);

        headerConsola.add(lblTituloConsola, BorderLayout.WEST);
        headerConsola.add(indicadores, BorderLayout.EAST);

        // Área de texto para la consola (JTextPane para colores)
        areaConsola = new JTextPane();
        areaConsola.setEditable(false);
        areaConsola.setBackground(new Color(30, 30, 30));
        areaConsola.setCaretColor(COLOR_SECUNDARIO);
        areaConsola.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        consolaDoc = areaConsola.getStyledDocument();

        // Auto-scroll
        DefaultCaret caret = (DefaultCaret) areaConsola.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(areaConsola);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Mensaje inicial
        appendToConsole("=" + "=".repeat(78) + "=\n", estiloNormal);
        appendToConsole("🚀 Agroveterinaria La Fortaleza - TERMINAL DE CONTROL\n", estiloExito);
        appendToConsole("=" + "=".repeat(78) + "=\n", estiloNormal);
        appendToConsole("💡 Presiona 'INICIAR SISTEMA' para comenzar la operación...\n\n", estiloInfo);

        panel.add(headerConsola, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBackground(COLOR_FONDO);

        // Card de estado
        JPanel cardEstado = crearCardEstado();

        // Card de tiempo
        JPanel cardTiempo = crearCardTiempo();

        // Card de memoria
        JPanel cardMemoria = crearCardMemoria();

        panel.add(cardEstado);
        panel.add(cardTiempo);
        panel.add(cardMemoria);

        return panel;
    }

    private JPanel crearCardEstado() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_FONDO_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new SombaBorder(),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel("Estado del Sistema");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_TEXTO_SECUNDARIO);

        lblEstado = new JLabel("Sistema Detenido");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEstado.setForeground(COLOR_ERROR);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblEstado, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearCardTiempo() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_FONDO_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new SombaBorder(),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel("Tiempo de Ejecución");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_TEXTO_SECUNDARIO);

        lblTiempo = new JLabel("00:00:00");
        lblTiempo.setFont(new Font("Consolas", Font.BOLD, 16));
        lblTiempo.setForeground(COLOR_TEXTO);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblTiempo, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearCardMemoria() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_FONDO_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new SombaBorder(),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel("Uso de Memoria");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_TEXTO_SECUNDARIO);

        lblMemoria = new JLabel("0 MB / 0 MB");
        lblMemoria.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMemoria.setForeground(COLOR_TEXTO);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblMemoria, BorderLayout.CENTER);

        return card;
    }

    private JButton crearBotonModerno(String texto, String icono, Color color) {
        JButton boton = new JButton(icono + " " + texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover() && isEnabled()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(isEnabled() ? color : color.darker().darker());
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                super.paintComponent(g);
                g2.dispose();
            }
        };

        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(160, 45));

        return boton;
    }

    private JButton crearBotonCircular(String texto, Color color) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }

                g2.fillOval(0, 0, getWidth(), getHeight());

                super.paintComponent(g);
                g2.dispose();
            }
        };

        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(35, 35));

        return boton;
    }

    private void setupConsoleRedirection() {
        consolaOriginal = System.out;

        consolaPersonalizada = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                consolaOriginal.write(b);
                SwingUtilities.invokeLater(() -> {
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    char c = (char) b;

                    if (b == '\n') {
                        try {
                            consolaDoc.insertString(consolaDoc.getLength(), "\n", estiloNormal);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String currentText = areaConsola.getText();
                        if (currentText.endsWith("\n") || currentText.isEmpty()) {
                            appendToConsole("[" + timestamp + "] ", estiloTimestamp);
                        }

                        String charStr = String.valueOf(c);
                        SimpleAttributeSet style = determinarEstilo(charStr, currentText);
                        appendToConsole(charStr, style);
                    }

                    areaConsola.setCaretPosition(consolaDoc.getLength());
                });
            }
        });
    }

    private SimpleAttributeSet determinarEstilo(String texto, String contexto) {
        // Detectar comandos de email
        if (texto.toLowerCase().contains("mail") || texto.toLowerCase().contains("email") ||
                texto.toLowerCase().contains("smtp") || texto.toLowerCase().contains("@")) {
            return estiloEmail;
        }

        // Detectar errores
        if (contexto.toLowerCase().contains("error") || contexto.toLowerCase().contains("❌")) {
            return estiloError;
        }

        // Detectar éxito
        if (contexto.toLowerCase().contains("éxito") || contexto.toLowerCase().contains("✅") ||
                contexto.toLowerCase().contains("success")) {
            return estiloExito;
        }

        // Detectar comandos del sistema
        if (contexto.toLowerCase().contains("🚀") || contexto.toLowerCase().contains("📧") ||
                contexto.toLowerCase().contains("🔄")) {
            return estiloInfo;
        }

        return estiloNormal;
    }

    private void appendToConsole(String texto, SimpleAttributeSet style) {
        try {
            consolaDoc.insertString(consolaDoc.getLength(), texto, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void setupTimers() {
        // Timer principal de actualización
        timerActualizacion = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sistemaEjecutandose) {
                    actualizarTiempo();
                    actualizarMemoria();
                    actualizarProgressBar();
                }
            }
        });
    }

    private void setupAnimations() {
        // Timer para animaciones sutiles
        animacionTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sistemaEjecutandose) {
                    animacionContador++;

                    // Animación sutil en la barra de progreso
                    if (animacionContador % 20 == 0) {
                        int value = progressBar.getValue();
                        progressBar.setValue((value + 1) % 101);
                    }
                }
            }
        });
    }

    private void actualizarTiempo() {
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
        long horas = tiempoTranscurrido / 3600000;
        long minutos = (tiempoTranscurrido % 3600000) / 60000;
        long segundos = (tiempoTranscurrido % 60000) / 1000;

        lblTiempo.setText(String.format("⏱️ %02d:%02d:%02d", horas, minutos, segundos));
    }

    private void actualizarMemoria() {
        Runtime runtime = Runtime.getRuntime();
        long memoriaTotal = runtime.totalMemory();
        long memoriaLibre = runtime.freeMemory();
        long memoriaUsada = memoriaTotal - memoriaLibre;
        long memoriaMaxima = runtime.maxMemory();

        int porcentajeUso = (int) ((memoriaUsada * 100) / memoriaMaxima);

        memoryBar.setValue(porcentajeUso);
        memoryBar.setString(String.format("Memoria: %d%%", porcentajeUso));

        lblMemoria.setText(String.format("📊 %d MB / %d MB",
                memoriaUsada / (1024 * 1024),
                memoriaMaxima / (1024 * 1024)));

        // Cambiar color según el uso
        if (porcentajeUso > 80) {
            memoryBar.setForeground(COLOR_ERROR);
        } else if (porcentajeUso > 60) {
            memoryBar.setForeground(COLOR_WARNING);
        } else {
            memoryBar.setForeground(COLOR_SECUNDARIO);
        }
    }

    private void actualizarProgressBar() {
        if (sistemaEjecutandose) {
            progressBar.setString("Sistema ejecutándose...");
        }
    }

    private void mostrarConfiguracion() {
        JOptionPane.showMessageDialog(this,
                "Panel de Configuración\n\nVersión: 2.1\nDesarrollado con Java Swing\nTema: Moderno",
                "Configuración",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void iniciarSistema() {
        if (!sistemaEjecutandose) {
            sistemaEjecutandose = true;
            tiempoInicio = System.currentTimeMillis();

            // Actualizar UI
            btnIniciar.setEnabled(false);
            btnDetener.setEnabled(true);
            lblEstado.setText("🟢 Sistema Ejecutándose");
            lblEstado.setForeground(COLOR_EXITO);
            progressBar.setString("Iniciando sistema...");
            progressBar.setIndeterminate(false);

            // Redireccionar consola
            System.setOut(consolaPersonalizada);

            // Iniciar timers
            timerActualizacion.start();
            animacionTimer.start();

            // Crear y ejecutar hilo del sistema
            sistemaThread = new Thread(() -> {
                try {
                    SwingUtilities.invokeLater(() -> {
                        appendToConsole("\n🚀 INICIANDO SISTEMA VETERINARIO...\n", estiloExito);
                        appendToConsole("📧 Configurando servicio de emails...\n", estiloEmail);
                        appendToConsole("🔄 Cargando módulos del sistema...\n\n", estiloInfo);
                    });

                    mailApp = new MailApplication();
                    mailApp.start();

                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        appendToConsole("❌ ERROR: " + e.getMessage() + "\n", estiloError);
                        detenerSistema();
                    });
                }
            });

            sistemaThread.setDaemon(true);
            sistemaThread.start();

            appendToConsole("✅ Sistema iniciado correctamente!\n\n", estiloExito);
        }
    }

    private void detenerSistema() {
        if (sistemaEjecutandose) {
            sistemaEjecutandose = false;

            // Actualizar UI
            btnIniciar.setEnabled(true);
            btnDetener.setEnabled(false);
            lblEstado.setText("🔴 Sistema Detenido");
            lblEstado.setForeground(COLOR_ERROR);
            lblTiempo.setText("⏱️ 00:00:00");
            progressBar.setString("Sistema detenido");
            progressBar.setValue(0);
            progressBar.setIndeterminate(false);
            memoryBar.setValue(0);
            memoryBar.setString("Memoria: 0%");

            // Detener timers
            timerActualizacion.stop();
            animacionTimer.stop();

            // Detener hilo del sistema
            if (sistemaThread != null && sistemaThread.isAlive()) {
                sistemaThread.interrupt();
            }

            // Restaurar consola original
            System.setOut(consolaOriginal);

            appendToConsole("\n🛑 SISTEMA DETENIDO POR EL USUARIO\n", estiloError);
            appendToConsole("📊 Sesión finalizada: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n\n", estiloTimestamp);
        }
    }

    private void limpiarConsola() {
        areaConsola.setText("");
        try {
            consolaDoc.remove(0, consolaDoc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        appendToConsole("=" + "=".repeat(78) + "=\n", estiloNormal);
        appendToConsole("🚀 AgroVeterinaria La Fortaleza - CONSOLA LIMPIADA\n", estiloExito);
        appendToConsole("=" + "=".repeat(78) + "=\n", estiloNormal);
        appendToConsole("📅 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n\n", estiloTimestamp);
    }

    // Clase para crear bordes con sombra
    class SombaBorder implements javax.swing.border.Border {
        private final int shadowSize = 4;
        private final Color shadowColor = COLOR_SOMBRA;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dibujar sombra
            g2.setColor(shadowColor);
            for (int i = 0; i < shadowSize; i++) {
                g2.drawRoundRect(x + i + shadowSize, y + i + shadowSize,
                        width - (2 * i) - shadowSize, height - (2 * i) - shadowSize,
                        8, 8);
            }

            // Dibujar borde principal
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, width - shadowSize, height - shadowSize, 8, 8);

            g2.setColor(new Color(230, 230, 230));
            g2.drawRoundRect(x, y, width - shadowSize, height - shadowSize, 8, 8);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, shadowSize, shadowSize);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    // Método principal para ejecutar la GUI (mantenido para compatibilidad)
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            // Configuraciones adicionales de UI
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("Panel.background", COLOR_FONDO);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Crear y mostrar la GUI
        SwingUtilities.invokeLater(() -> {
            new SistemaCentralGUI().setVisible(true);
        });
    }
}