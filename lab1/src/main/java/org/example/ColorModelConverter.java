package org.example;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorModelConverter extends JFrame {
    private JSlider redSlider, greenSlider, blueSlider;
    private JTextField redField, greenField, blueField;

    private JSlider cyanSlider, magentaSlider, yellowSlider, blackSlider;
    private JTextField cyanField, magentaField, yellowField, blackField;

    private JSlider hueSlider, lightnessSlider, saturationSlider;
    private JTextField hueField, lightnessField, saturationField;

    private JButton colorPickerButton;
    private JPanel mainColorPreview;
    private boolean updating = false;

    private int red = 0, green = 0, blue = 0;
    private double cyan = 0, magenta = 0, yellow = 0, black = 0;
    private double hue = 0, lightness = 0, saturation = 0;

    public ColorModelConverter() {
        setTitle("Конвертер цветовых моделей: CMYK-RGB-HLS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeComponents();
        setupLayout();
        setupEventListeners();

        pack();
        setLocationRelativeTo(null);
        setSize(800, 600);
        updateAllFromRGB(0, 0, 0);
    }

    private void initializeComponents() {
        redSlider = createSlider(0, 255, 0);
        greenSlider = createSlider(0, 255, 0);
        blueSlider = createSlider(0, 255, 0);

        redField = createTextField("0");
        greenField = createTextField("0");
        blueField = createTextField("0");

        cyanSlider = createSlider(0, 100, 0);
        magentaSlider = createSlider(0, 100, 0);
        yellowSlider = createSlider(0, 100, 0);
        blackSlider = createSlider(0, 100, 0);

        cyanField = createTextField("0");
        magentaField = createTextField("0");
        yellowField = createTextField("0");
        blackField = createTextField("0");

        hueSlider = createSlider(0, 360, 0);
        lightnessSlider = createSlider(0, 100, 0);
        saturationSlider = createSlider(0, 100, 0);

        hueField = createTextField("0");
        lightnessField = createTextField("0");
        saturationField = createTextField("0");

        colorPickerButton = new JButton("Выбрать цвет из палитры");
        mainColorPreview = createColorPreviewPanel();
    }

    private JSlider createSlider(int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        return slider;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text, 5);
        field.setHorizontalAlignment(JTextField.CENTER);
        return field;
    }

    private JPanel createColorPreviewPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(100, 50));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return panel;
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel previewPanel = new JPanel(new FlowLayout());
        previewPanel.add(new JLabel("Основной цвет:"));
        previewPanel.add(mainColorPreview);
        previewPanel.add(colorPickerButton);

        JPanel rgbPanel = createColorModelPanel("RGB Модель",
                new String[]{"R:", "G:", "B:"},
                new JSlider[]{redSlider, greenSlider, blueSlider},
                new JTextField[]{redField, greenField, blueField});

        JPanel cmykPanel = createColorModelPanel("CMYK Модель (в процентах%)",
                new String[]{"C:", "M:", "Y:", "K:"},
                new JSlider[]{cyanSlider, magentaSlider, yellowSlider, blackSlider},
                new JTextField[]{cyanField, magentaField, yellowField, blackField});

        JPanel hlsPanel = createColorModelPanel("HLS Модель",
                new String[]{"H:", "L:", "S:"},
                new JSlider[]{hueSlider, lightnessSlider, saturationSlider},
                new JTextField[]{hueField, lightnessField, saturationField});

        mainPanel.add(previewPanel);
        mainPanel.add(rgbPanel);
        mainPanel.add(cmykPanel);
        mainPanel.add(hlsPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createColorModelPanel(String title, String[] labels,
                                         JSlider[] sliders, JTextField[] fields) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JPanel controlPanel = new JPanel(new GridLayout(labels.length, 3, 5, 5));

        for (int i = 0; i < labels.length; i++) {
            controlPanel.add(new JLabel(labels[i]));
            controlPanel.add(sliders[i]);
            controlPanel.add(fields[i]);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(controlPanel, BorderLayout.CENTER);

        panel.add(wrapper, BorderLayout.CENTER);
        return panel;
    }

    private void setupEventListeners() {
        setupSliderListeners(new JSlider[]{redSlider, greenSlider, blueSlider},
                new JTextField[]{redField, greenField, blueField},
                "RGB");

        setupFieldListeners(new JTextField[]{redField, greenField, blueField},
                new JSlider[]{redSlider, greenSlider, blueSlider},
                0, 255, "RGB");

        setupSliderListeners(new JSlider[]{cyanSlider, magentaSlider, yellowSlider, blackSlider},
                new JTextField[]{cyanField, magentaField, yellowField, blackField},
                "CMYK");

        setupFieldListeners(new JTextField[]{cyanField, magentaField, yellowField, blackField},
                new JSlider[]{cyanSlider, magentaSlider, yellowSlider, blackSlider},
                0, 100, "CMYK");

        setupSliderListeners(new JSlider[]{hueSlider, lightnessSlider, saturationSlider},
                new JTextField[]{hueField, lightnessField, saturationField},
                "HLS");

        setupFieldListeners(new JTextField[]{hueField, lightnessField, saturationField},
                new JSlider[]{hueSlider, lightnessSlider, saturationSlider},
                0, 100, "HLS");

        colorPickerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color initialColor = new Color(red, green, blue);
                Color selectedColor = JColorChooser.showDialog(
                        ColorModelConverter.this, "Выберите цвет", initialColor);

                if (selectedColor != null) {
                    updateAllFromRGB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue());
                }
            }
        });
    }

    private void setupSliderListeners(JSlider[] sliders, JTextField[] fields, String model) {
        for (int i = 0; i < sliders.length; i++) {
            final int index = i;
            sliders[i].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (!updating) {
                        updating = true;
                        int value = sliders[index].getValue();
                        fields[index].setText(String.valueOf(value));

                        switch (model) {
                            case "RGB":
                                updateFromRGB();
                                break;
                            case "CMYK":
                                updateFromCMYK();
                                break;
                            case "HLS":
                                updateFromHLS();
                                break;
                        }
                        updating = false;
                    }
                }
            });
        }
    }

    private void setupFieldListeners(JTextField[] fields, JSlider[] sliders,
                                     int min, int max, String model) {
        for (int i = 0; i < fields.length; i++) {
            final int index = i;
            fields[i].getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { updateField(); }
                @Override
                public void removeUpdate(DocumentEvent e) { updateField(); }
                @Override
                public void changedUpdate(DocumentEvent e) { updateField(); }

                private void updateField() {
                    if (!updating) {
                        updating = true;
                        try {
                            int value = Integer.parseInt(fields[index].getText());
                            value = Math.max(min, Math.min(max, value));
                            sliders[index].setValue(value);
                            fields[index].setText(String.valueOf(value));

                            switch (model) {
                                case "RGB":
                                    updateFromRGB();
                                    break;
                                case "CMYK":
                                    updateFromCMYK();
                                    break;
                                case "HLS":
                                    updateFromHLS();
                                    break;
                            }
                        } catch (NumberFormatException ex) {
                        }
                        updating = false;
                    }
                }
            });
        }
    }

    private void updateFromRGB() {
        red = redSlider.getValue();
        green = greenSlider.getValue();
        blue = blueSlider.getValue();

        double[] cmyk = rgbToCmyk(red, green, blue);
        cyan = cmyk[0]; magenta = cmyk[1]; yellow = cmyk[2]; black = cmyk[3];

        double[] hls = rgbToHls(red, green, blue);
        hue = hls[0]; lightness = hls[1]; saturation = hls[2];

        updateAllDisplays();
    }

    private void updateFromCMYK() {
        cyan = cyanSlider.getValue();
        magenta = magentaSlider.getValue();
        yellow = yellowSlider.getValue();
        black = blackSlider.getValue();

        int[] rgb = cmykToRgb(cyan, magenta, yellow, black);
        red = rgb[0]; green = rgb[1]; blue = rgb[2];

        double[] hls = rgbToHls(red, green, blue);
        hue = hls[0]; lightness = hls[1]; saturation = hls[2];

        updateAllDisplays();
    }

    private void updateFromHLS() {
        hue = hueSlider.getValue();
        lightness = lightnessSlider.getValue();
        saturation = saturationSlider.getValue();

        int[] rgb = hlsToRgb(hue, lightness, saturation);
        red = rgb[0]; green = rgb[1]; blue = rgb[2];

        double[] cmyk = rgbToCmyk(red, green, blue);
        cyan = cmyk[0]; magenta = cmyk[1]; yellow = cmyk[2]; black = cmyk[3];

        updateAllDisplays();
    }

    private void updateAllFromRGB(int r, int g, int b) {
        red = r; green = g; blue = b;

        double[] cmyk = rgbToCmyk(r, g, b);
        cyan = cmyk[0]; magenta = cmyk[1]; yellow = cmyk[2]; black = cmyk[3];

        double[] hls = rgbToHls(r, g, b);
        hue = hls[0]; lightness = hls[1]; saturation = hls[2];

        updateAllDisplays();
    }

    private void updateAllDisplays() {
        updating = true;

        redSlider.setValue(red);
        greenSlider.setValue(green);
        blueSlider.setValue(blue);
        redField.setText(String.valueOf(red));
        greenField.setText(String.valueOf(green));
        blueField.setText(String.valueOf(blue));

        cyanSlider.setValue((int)Math.round(cyan));
        magentaSlider.setValue((int)Math.round(magenta));
        yellowSlider.setValue((int)Math.round(yellow));
        blackSlider.setValue((int)Math.round(black));
        cyanField.setText(String.valueOf((int)Math.round(cyan)));
        magentaField.setText(String.valueOf((int)Math.round(magenta)));
        yellowField.setText(String.valueOf((int)Math.round(yellow)));
        blackField.setText(String.valueOf((int)Math.round(black)));

        hueSlider.setValue((int)Math.round(hue));
        lightnessSlider.setValue((int)Math.round(lightness));
        saturationSlider.setValue((int)Math.round(saturation));
        hueField.setText(String.valueOf((int)Math.round(hue)));
        lightnessField.setText(String.valueOf((int)Math.round(lightness)));
        saturationField.setText(String.valueOf((int)Math.round(saturation)));

        Color currentColor = new Color(red, green, blue);
        mainColorPreview.setBackground(currentColor);

        updating = false;
    }

    private double[] rgbToCmyk(int r, int g, int b) {
        double red = r / 255.0;
        double green = g / 255.0;
        double blue = b / 255.0;

        double k = 1 - Math.max(Math.max(red, green), blue);
        double c = (1 - red - k) / (1 - k);
        double m = (1 - green - k) / (1 - k);
        double y = (1 - blue - k) / (1 - k);

        if (Double.isNaN(c)) c = 0;
        if (Double.isNaN(m)) m = 0;
        if (Double.isNaN(y)) y = 0;

        return new double[]{c * 100, m * 100, y * 100, k * 100};
    }

    private int[] cmykToRgb(double c, double m, double y, double k) {
        c = c / 100.0;
        m = m / 100.0;
        y = y / 100.0;
        k = k / 100.0;

        int r = (int)Math.round(255 * (1 - c) * (1 - k));
        int g = (int)Math.round(255 * (1 - m) * (1 - k));
        int b = (int)Math.round(255 * (1 - y) * (1 - k));

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return new int[]{r, g, b};
    }

    private double[] rgbToHls(int r, int g, int b) {
        double red = r / 255.0;
        double green = g / 255.0;
        double blue = b / 255.0;

        double max = Math.max(Math.max(red, green), blue);
        double min = Math.min(Math.min(red, green), blue);

        double h, l, s;

        l = (max + min) / 2.0;

        if (max == min) {
            h = s = 0;
        } else {
            double d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);

            if (max == red) {
                h = (green - blue) / d + (green < blue ? 6 : 0);
            } else if (max == green) {
                h = (blue - red) / d + 2;
            } else {
                h = (red - green) / d + 4;
            }

            h /= 6;
        }

        return new double[]{h * 360, l * 100, s * 100};
    }

    private int[] hlsToRgb(double h, double l, double s) {
        h = h / 360.0;
        l = l / 100.0;
        s = s / 100.0;

        double r, g, b;

        if (s == 0) {
            r = g = b = l;
        } else {
            double q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            double p = 2 * l - q;

            r = hueToRgb(p, q, h + 1.0/3);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0/3);
        }

        return new int[]{
                (int)Math.round(r * 255),
                (int)Math.round(g * 255),
                (int)Math.round(b * 255)
        };
    }

    private double hueToRgb(double p, double q, double t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0/6) return p + (q - p) * 6 * t;
        if (t < 1.0/2) return q;
        if (t < 2.0/3) return p + (q - p) * (2.0/3 - t) * 6;
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ColorModelConverter().setVisible(true);
            }
        });
    }
}