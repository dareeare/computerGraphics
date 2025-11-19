package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class ImageProcessingApp {
    private JFrame frame;
    private JLabel originalLabel;
    private JLabel processedLabel;
    private BufferedImage originalImage;

    public ImageProcessingApp() {

        frame = new JFrame("Image Processing");
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton loadButton = new JButton("Load Image");
        JButton contrastButton = new JButton("Linear Contrast");
        JButton histogramButton = new JButton("Histogram Equalization");

        originalLabel = new JLabel();
        processedLabel = new JLabel();

        loadButton.addActionListener(new LoadImageAction());
        contrastButton.addActionListener(new LinearContrastAction());
        histogramButton.addActionListener(new HistogramEqualizationAction());

        frame.add(loadButton);
        frame.add(contrastButton);
        frame.add(histogramButton);
        frame.add(originalLabel);
        frame.add(processedLabel);

        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private class LoadImageAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    originalImage = ImageIO.read(file);
                    originalLabel.setIcon(new ImageIcon(originalImage));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class LinearContrastAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (originalImage != null) {
                BufferedImage contrastedImage = linearContrast(originalImage);
                processedLabel.setIcon(new ImageIcon(contrastedImage));
            }
        }

        BufferedImage linearContrast(BufferedImage image) {
            int min = 255;
            int max = 0;
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int colorRGB = image.getRGB(x, y);
                    int gray = (colorRGB >> 16) & 0xff;
                    if (gray < min) min = gray;
                    if (gray > max) max = gray;
                }
            }

            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int colorRGB = image.getRGB(x, y);
                    int gray = (colorRGB >> 16) & 0xff;

                    int newGray = (int) (((gray - min) / (double) (max - min)) * 255);
                    newGray = Math.max(0, Math.min(255, newGray));

                    int newColorRGB = (newGray << 16) | (newGray << 8) | newGray;
                    newImage.setRGB(x, y, newColorRGB);
                }
            }
            return newImage;
        }
    }

    class HistogramEqualizationAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (originalImage != null) {
                BufferedImage equalizedImage = histogramEqualization(originalImage);
                processedLabel.setIcon(new ImageIcon(equalizedImage));
            }
        }

        BufferedImage histogramEqualization(BufferedImage image) {
            int[] histogram = new int[256];
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int colorRGB = image.getRGB(x, y);
                    int gray = (colorRGB >> 16) & 0xff;
                    histogram[gray]++;
                }
            }

            int[] cumHistogram = new int[256];
            cumHistogram[0] = histogram[0];
            for (int i = 1; i < histogram.length; i++) {
                cumHistogram[i] = cumHistogram[i - 1] + histogram[i];
            }

            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            int totalPixels = image.getWidth() * image.getHeight();
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int colorRGB = image.getRGB(x, y);
                    int gray = (colorRGB >> 16) & 0xff;

                    int newGray = cumHistogram[gray] * 255 / totalPixels;
                    newGray = Math.max(0, Math.min(255, newGray));

                    int newColorRGB = (newGray << 16) | (newGray << 8) | newGray;
                    newImage.setRGB(x, y, newColorRGB);
                }
            }
            return newImage;
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageProcessingApp());
    }
}