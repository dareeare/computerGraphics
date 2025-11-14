package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class RasterGraphics extends JFrame {
    private CanvasPanel canvasPanel;
    private JComboBox<String> algorithmSelect;
    private JLabel statusLabel;
    private JSlider gridSizeSlider;
    private JButton drawButton;

    private final int CANVAS_SIZE = 500;

    public RasterGraphics() {
        setTitle("Растровые Алгоритмы с Изменяемой Сеткой");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeComponents();
        setupLayout();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        String[] algorithms = {"Пошаговый", "ЦДА", "Брезенхем (линии)", "Брезенхем (окружность)"};
        algorithmSelect = new JComboBox<>(algorithms);

        gridSizeSlider = new JSlider(20, 50, 20);
        gridSizeSlider.setMajorTickSpacing(5);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setPaintLabels(true);

        drawButton = new JButton("Нарисовать");

        statusLabel = new JLabel("Нажмите на полотно, чтобы выбрать начальную и конечную точки.");

        canvasPanel = new CanvasPanel(CANVAS_SIZE, gridSizeSlider, statusLabel, algorithmSelect);

        gridSizeSlider.addChangeListener(e -> canvasPanel.updateGridSize());
        drawButton.addActionListener(e -> canvasPanel.draw());
    }

    private void setupLayout() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Выбор алгоритма:"));
        controlPanel.add(algorithmSelect);
        controlPanel.add(new JLabel("Размер сетки:"));
        controlPanel.add(gridSizeSlider);
        controlPanel.add(drawButton);

        add(controlPanel, BorderLayout.NORTH);
        add(canvasPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    static class CanvasPanel extends JPanel {
        private int cellSize;
        private Integer startX, startY, endX, endY;
        private List<Point> gridPoints;
        private JSlider gridSizeSlider;
        private JLabel statusLabel;
        private JComboBox<String> algorithmSelect;

        public CanvasPanel(int size, JSlider gridSizeSlider, JLabel statusLabel, JComboBox<String> algorithmSelect) {
            this.gridSizeSlider = gridSizeSlider;
            this.statusLabel = statusLabel;
            this.algorithmSelect = algorithmSelect;

            setPreferredSize(new Dimension(size, size));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));

            cellSize = gridSizeSlider.getValue();
            gridPoints = new java.util.ArrayList<>();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX() / cellSize;
                    int y = e.getY() / cellSize;

                    if (startX == null) {
                        startX = x;
                        startY = y;
                        statusLabel.setText("Начальная точка выбрана: (" + startX + ", " + startY + "). Выберите конечную точку.");
                    } else {
                        endX = x;
                        endY = y;
                        statusLabel.setText("Конечная точка выбрана: (" + endX + ", " + endY + "). Нажмите \"Нарисовать\".");
                    }
                    repaint();
                }
            });
        }

        public void updateGridSize() {
            cellSize = gridSizeSlider.getValue();
            startX = startY = endX = endY = null;
            gridPoints.clear();
            statusLabel.setText("Нажмите на полотно, чтобы выбрать начальную и конечную точки.");
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGrid(g);

            // Рисуем точки
            if (startX != null) {
                drawPoint(g, startX, startY, Color.GREEN);
            }
            if (endX != null) {
                drawPoint(g, endX, endY, Color.RED);
            }

            // Рисуем алгоритм
            for (Point p : gridPoints) {
                g.setColor(Color.BLACK);
                g.fillRect(p.x * cellSize, p.y * cellSize, cellSize, cellSize);
            }
        }

        private void drawGrid(Graphics g) {
            g.setColor(Color.LIGHT_GRAY);
            int width = getWidth();
            int height = getHeight();

            // Вертикальные линии и подписи
            for (int x = 0; x < width; x += cellSize) {
                g.drawLine(x, 0, x, height);
                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(x / cellSize), x + 2, 12);
                g.setColor(Color.LIGHT_GRAY);
            }

            // Горизонтальные линии и подписи
            for (int y = 0; y < height; y += cellSize) {
                g.drawLine(0, y, width, y);
                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(y / cellSize), 2, y + 12);
                g.setColor(Color.LIGHT_GRAY);
            }
        }

        private void drawPoint(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
        }

        public void draw() {
            if (startX == null || startY == null || endX == null || endY == null) {
                statusLabel.setText("Выберите начальную и конечную точки.");
                return;
            }

            gridPoints.clear();
            String algorithm = (String) algorithmSelect.getSelectedItem();

            switch (algorithm) {
                case "Пошаговый":
                    gridPoints = Algorithms.stepAlgorithm(startX, startY, endX, endY);
                    break;
                case "ЦДА":
                    gridPoints = Algorithms.ddaAlgorithm(startX, startY, endX, endY);
                    break;
                case "Брезенхем (линии)":
                    gridPoints = Algorithms.bresenhamLine(startX, startY, endX, endY);
                    break;
                case "Брезенхем (окружность)":
                    int radius = (int) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
                    gridPoints = Algorithms.bresenhamCircle(startX, startY, radius);
                    break;
            }

            startX = startY = endX = endY = null;
            statusLabel.setText("Нажмите на сетку, чтобы выбрать начальную и конечную точки.");
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RasterGraphics();
        });
    }
}