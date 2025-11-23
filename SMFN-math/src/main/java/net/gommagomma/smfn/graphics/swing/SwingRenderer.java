package net.gommagomma.smfn.graphics.swing;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.gommagomma.smfn.graphics.core.Renderer;

/**
 * Implementazione di Renderer che utilizza Java Swing per visualizzare
 * il risultato del rendering in un JFrame.
 */
public class SwingRenderer
implements Renderer
{
    private final int width;
    private final int height;
    private final BufferedImage image;
    private final JFrame frame;
    private final JLabel label;

    public SwingRenderer(int width, int height)
    {
        this.width = width;
        this.height = height;
        // Creiamo un'immagine di tipo RGB standard (TYPE_INT_RGB)
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Configurazione della finestra Swing
        this.frame = new JFrame("Mandelbrot Explorer");
        this.label = new JLabel(new ImageIcon(this.image));
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.add(label);
        frame.setResizable(false);
    }

    @Override
    public void setPixel(int x, int y, int rgbColor)
    {
        // Il metodo setRGB di BufferedImage è efficiente per impostare i singoli pixel
        if (x >= 0 && x < width && y >= 0 && y < height) {
            this.image.setRGB(x, y, rgbColor);
        }
    }

    @Override
    public void display()
    {
        // Rende visibile la finestra e ridisegna il contenuto (l'immagine completa)
        SwingUtilities.invokeLater(() -> {
            frame.pack(); // Adatta la finestra alla dimensione dell'immagine
            frame.setLocationRelativeTo(null); // Centra la finestra
            frame.setVisible(true);
            label.repaint(); // Assicura che l'immagine venga disegnata
        });
    }
}
