package net.gommagomma.smfn.graphics.drivers.swing;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import net.gommagomma.smfn.graphics.core.Renderer1D;

/**
 * Implementazione concreta di Renderer1D usando Java Swing/AWT Canvas 
 * e BufferStrategy per un rendering efficiente.
 */
public class SwingRenderer1D
extends Canvas
implements Renderer1D
{
	private static final long serialVersionUID = 1L;
	private final int width, height;
    private Color currentColor = Color.BLACK;
    private BufferStrategy bufferStrategy;
    private Graphics g;

    public SwingRenderer1D(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
    }

    /**
     * Inizializza la strategia di buffering dopo che il Canvas è stato aggiunto a un Frame ed è visibile.
     */
    public void initBufferStrategy() {
        // Creiamo una strategia con 2 buffer (double buffering) per evitare flickering
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
    }
    
    // --- Implementazione Interfaccia Renderer (Ciclo di Disegno) ---

    @Override
    public void startDrawing() {
        if (bufferStrategy == null) return;
        this.g = bufferStrategy.getDrawGraphics();
    }

    @Override
    public void endDrawingAndFlush() {
        if (g != null) {
            g.dispose();
            g = null;
        }
        if (bufferStrategy != null) {
            bufferStrategy.show();
        }
    }

    // --- Implementazione Interfaccia Renderer (Primitive di Disegno) ---

    @Override
    public void clear(Color color) {
        if (g == null) return;
        g.setColor(color);
        g.fillRect(0, 0, width, height);
    }

    @Override
    public void setColor(Color color) { 
        this.currentColor = color; 
        if (g != null) {
            g.setColor(color); 
        }
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        if (g == null) return;
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawPoint(int x, int y) {
        if (g == null) return;
        // Disegna un punto disegnando un micro-rettangolo di 1x1 pixel
        g.fillRect(x, y, 1, 1); 
    }

    @Override
    public void drawText(String text, int x, int y) {
        if (g == null) return;
        g.drawString(text, x, y);
    }
    
    @Override
    public void drawOverlayText(String text, int x, int y, Color color) {
        if (g == null) return;
        // Salva il colore e il font originali prima di cambiarli
        Color originalColor = g.getColor();
        Font originalFont = g.getFont();
        
        g.setColor(color);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        g.drawString(text, x, y);
        
        // Ripristina i valori originali
        g.setFont(originalFont);
        g.setColor(originalColor); 
    }

    @Override
    public int getWidth() { 
        return width; 
    }

    @Override
    public int getHeight() { 
        return height; 
    }
}
