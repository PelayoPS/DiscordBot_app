package com.discordbot;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackendGUI extends JFrame {
    // Constantes de diseño
    private static final Color BACKGROUND_COLOR = new Color(32, 33, 36);
    private static final Color PANEL_BACKGROUND = new Color(41, 42, 45);
    private static final Color BORDER_COLOR = new Color(60, 61, 64);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font CONSOLE_FONT = new Font("Consolas", Font.PLAIN, 13);
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 650;
    private static final int BUTTON_WIDTH = 140;
    private static final int BUTTON_HEIGHT = 40;
    private static final int PADDING = 25;

    private JButton startButton;
    private JButton restartButton;
    private JButton stopButton;
    private JTextPane consoleOutput;
    private StyledDocument doc;
    private boolean isRunning = false;
    private ConfigurableApplicationContext applicationContext;
    private PrintStream originalOut;
    private PrintStream originalErr;
    private JLabel statusLabel;

    // Colores ANSI mejorados
    private static final Color ANSI_BLACK = new Color(0, 0, 0);
    private static final Color ANSI_RED = new Color(239, 68, 68);
    private static final Color ANSI_GREEN = new Color(34, 197, 94);
    private static final Color ANSI_YELLOW = new Color(234, 179, 8);
    private static final Color ANSI_BLUE = new Color(59, 130, 246);
    private static final Color ANSI_MAGENTA = new Color(168, 85, 247);
    private static final Color ANSI_CYAN = new Color(6, 182, 212);
    private static final Color ANSI_WHITE = new Color(229, 231, 235);
    private static final Color ANSI_BRIGHT_BLACK = new Color(107, 114, 128);
    private static final Color ANSI_BRIGHT_RED = new Color(248, 113, 113);
    private static final Color ANSI_BRIGHT_GREEN = new Color(74, 222, 128);
    private static final Color ANSI_BRIGHT_YELLOW = new Color(250, 204, 21);
    private static final Color ANSI_BRIGHT_BLUE = new Color(96, 165, 250);
    private static final Color ANSI_BRIGHT_MAGENTA = new Color(192, 132, 252);
    private static final Color ANSI_BRIGHT_CYAN = new Color(34, 211, 238);
    private static final Color ANSI_BRIGHT_WHITE = new Color(255, 255, 255);

    public BackendGUI() {
        // Configurar la ventana
        setTitle("Discord Bot Control Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(WINDOW_WIDTH, 600));
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Crear panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        // Panel vertical para label de estado y botones
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setOpaque(true);

        // Label de estado
        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusLabel.setPreferredSize(new Dimension(400, 45));
        updateStatusLabel();
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(statusLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Panel de botones con estilo moderno
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Crear botones con estilo moderno
        startButton = createStyledButton("Arrancar", new Color(34, 197, 94));
        restartButton = createStyledButton("Reiniciar", new Color(59, 130, 246));
        stopButton = createStyledButton("Apagar", new Color(239, 68, 68));

        // Agregar botones al panel
        buttonPanel.add(startButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(stopButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(buttonPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Crear área de consola con soporte para colores
        consoleOutput = new JTextPane();
        doc = consoleOutput.getStyledDocument();
        consoleOutput.setEditable(false);
        consoleOutput.setFont(new Font("Consolas", Font.PLAIN, 15));
        consoleOutput.setBackground(new Color(30, 31, 34));
        consoleOutput.setForeground(ANSI_WHITE);
        consoleOutput.setMargin(new Insets(10, 15, 10, 15));

        // Agregar scroll pane para la consola con estilo moderno
        JScrollPane consoleScroll = new JScrollPane(consoleOutput);
        consoleScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1),
                        "Consola",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        TITLE_FONT,
                        ANSI_WHITE),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        consoleScroll.setBackground(PANEL_BACKGROUND);
        consoleScroll.getViewport().setBackground(new Color(30, 31, 34));
        consoleScroll.setMinimumSize(new Dimension(800, 400));

        // Personalizar la barra de desplazamiento
        JScrollBar scrollBar = consoleScroll.getVerticalScrollBar();
        scrollBar.setBackground(PANEL_BACKGROUND);
        scrollBar.setForeground(BORDER_COLOR);
        scrollBar.setPreferredSize(new Dimension(12, 0));

        // Agregar componentes al panel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(consoleScroll, BorderLayout.CENTER);

        // Agregar panel a la ventana
        add(mainPanel);

        // Configurar redirección de la salida estándar y de error
        setupConsoleRedirect();

        // Configurar acciones de los botones
        startButton.addActionListener(e -> {
            if (!isRunning) {
                startBackend();
            }
        });

        restartButton.addActionListener(e -> {
            if (isRunning) {
                restartBackend();
            }
        });

        stopButton.addActionListener(e -> {
            if (isRunning) {
                stopBackend();
            }
        });

        // Inicializar estado de los botones
        updateButtonStates();

        // Manejar el cierre de la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (isRunning) {
                    stopBackend();
                }
                System.setOut(originalOut);
                System.setErr(originalErr);
            }
        });
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1, true),
                BorderFactory.createEmptyBorder(8, 25, 8, 25)));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setText("<html><span style='letter-spacing:2px;'>" + text + "</span></html>");

        // Efecto hover moderno
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }

            public void mousePressed(MouseEvent e) {
                button.setBackground(color.darker().darker());
            }

            public void mouseReleased(MouseEvent e) {
                button.setBackground(color.darker());
            }
        });

        return button;
    }

    private void setupConsoleRedirect() {
        originalOut = System.out;
        originalErr = System.err;

        OutputStream out = new OutputStream() {
            private StringBuilder buffer = new StringBuilder();
            private static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[([0-9;]*)m");
            private Style currentStyle = getStyleForAnsiCode("");

            @Override
            public void write(int b) {
                buffer.append((char) b);
                if (b == '\n') {
                    processLine(buffer.toString());
                    buffer.setLength(0);
                }
            }

            private void processLine(String line) {
                try {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            int lastEnd = 0;
                            Matcher matcher = ANSI_PATTERN.matcher(line);
                            Style style = currentStyle;
                            while (matcher.find()) {
                                if (matcher.start() > lastEnd) {
                                    String plainText = line.substring(lastEnd, matcher.start());
                                    doc.insertString(doc.getLength(), plainText, style);
                                }
                                String ansiCode = matcher.group(1);
                                style = getStyleForAnsiCode(ansiCode);
                                currentStyle = style;
                                lastEnd = matcher.end();
                            }
                            if (lastEnd < line.length()) {
                                String remainingText = line.substring(lastEnd);
                                doc.insertString(doc.getLength(), remainingText, style);
                            }
                            consoleOutput.setCaretPosition(doc.getLength());
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private Style getStyleForAnsiCode(String ansiCode) {
        Style style = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style newStyle = StyleContext.getDefaultStyleContext().addStyle(null, style);

        String[] codes = ansiCode.split(";");
        for (String code : codes) {
            switch (code) {
                case "0": // Reset
                    StyleConstants.setForeground(newStyle, ANSI_WHITE);
                    break;
                case "30": // Black
                    StyleConstants.setForeground(newStyle, ANSI_BLACK);
                    break;
                case "31": // Red
                    StyleConstants.setForeground(newStyle, ANSI_RED);
                    break;
                case "32": // Green
                    StyleConstants.setForeground(newStyle, ANSI_GREEN);
                    break;
                case "33": // Yellow
                    StyleConstants.setForeground(newStyle, ANSI_YELLOW);
                    break;
                case "34": // Blue
                    StyleConstants.setForeground(newStyle, ANSI_BLUE);
                    break;
                case "35": // Magenta
                    StyleConstants.setForeground(newStyle, ANSI_MAGENTA);
                    break;
                case "36": // Cyan
                    StyleConstants.setForeground(newStyle, ANSI_CYAN);
                    break;
                case "37": // White
                    StyleConstants.setForeground(newStyle, ANSI_WHITE);
                    break;
                case "90": // Bright Black
                    StyleConstants.setForeground(newStyle, ANSI_BRIGHT_BLACK);
                    break;
                case "91": // Bright Red
                    StyleConstants.setForeground(newStyle, ANSI_BRIGHT_RED);
                    break;
                case "92": // Bright Green
                    StyleConstants.setForeground(newStyle, ANSI_BRIGHT_GREEN);
                    break;
                case "93": // Bright Yellow
                    StyleConstants.setForeground(newStyle, ANSI_BRIGHT_YELLOW);
                    break;
                case "94": // Bright Blue
                    StyleConstants.setForeground(newStyle, ANSI_BRIGHT_BLUE);
                    break;
                case "95": // Bright Magenta
                    StyleConstants.setForeground(newStyle, ANSI_BRIGHT_MAGENTA);
                    break;
                case "96": // Bright Cyan
                    StyleConstants.setForeground(newStyle, ANSI_BRIGHT_CYAN);
                    break;
                case "97": // Bright White
                    StyleConstants.setForeground(newStyle, ANSI_BRIGHT_WHITE);
                    break;
            }
        }
        return newStyle;
    }

    private void startBackend() {
        try {
            applicationContext = SpringApplication.run(DiscordBotApplication.class);
            isRunning = true;
            updateButtonStates();
            updateStatusLabel();
            appendToConsole("Backend iniciado correctamente\n");
        } catch (Exception e) {
            appendToConsole("Error al iniciar el backend: " + e.getMessage() + "\n");
        }
    }

    private void restartBackend() {
        stopBackend();
        startBackend();
    }

    private void stopBackend() {
        if (applicationContext != null) {
            applicationContext.close();
            isRunning = false;
            updateButtonStates();
            updateStatusLabel();
            // Limpiar la consola
            try {
                doc.remove(0, doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            appendToConsole("Backend detenido correctamente\n");
        }
    }

    private void updateButtonStates() {
        startButton.setEnabled(!isRunning);
        restartButton.setEnabled(isRunning);
        stopButton.setEnabled(isRunning);
    }

    private void updateStatusLabel() {
        if (isRunning) {
            statusLabel.setText("● En ejecución");
            statusLabel.setBackground(new Color(34, 197, 94));
            statusLabel.setForeground(Color.BLACK);
        } else {
            statusLabel.setText("● Detenido");
            statusLabel.setBackground(new Color(239, 68, 68));
            statusLabel.setForeground(Color.BLACK);
        }
    }

    private void appendToConsole(String text) {
        try {
            doc.insertString(doc.getLength(), text, null);
            consoleOutput.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Establecer Look and Feel moderno
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ejecutar la GUI en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            BackendGUI gui = new BackendGUI();
            gui.setVisible(true);
        });
    }
}