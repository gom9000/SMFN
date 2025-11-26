package net.gommagomma.smfn.graphics.drivers.swing;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import net.gommagomma.smfn.graphics.core.Renderer1D;

public class SwingRenderer1D
extends Canvas
implements Renderer1D
{
	private static final long serialVersionUID = 1L;
	private final int width, height;
    private Color currentColor = Color.BLACK;
    private BufferStrategy bufferStrategy;

    public SwingRenderer1D(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
    }

    public void initBufferStrategy() {
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
    }

    @Override public void clear(Color color) {
        Graphics g = bufferStrategy.getDrawGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.dispose();
    }
    @Override public void setColor(Color color) { this.currentColor = color; }
    @Override public void drawLine(int x1, int y1, int x2, int y2) {
        Graphics g = bufferStrategy.getDrawGraphics();
        g.setColor(currentColor);
        g.drawLine(x1, y1, x2, y2);
        g.dispose();
    }
    @Override
    public void drawPoint(int x, int y) {
        Graphics g = bufferStrategy.getDrawGraphics();
        g.setColor(currentColor);
        // Disegna un punto disegnando un micro-rettangolo di 1x1 pixel
        g.fillRect(x, y, 1, 1); 
        g.dispose();
    }
    @Override
    public void drawText(String text, int x, int y) {
        Graphics g = bufferStrategy.getDrawGraphics();
        g.setColor(currentColor);
        g.drawString(text, x, y);
        g.dispose();
    }
    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }
    
    // Metodo per mostrare il contenuto disegnato
    public void flush() { bufferStrategy.show(); }

    @Override
    public void drawOverlayText(String text, int x, int y, Color color) {
        Graphics g = bufferStrategy.getDrawGraphics();
        g.setColor(color);
        // Impostiamo un font per l'HUD (Head-Up Display)
        g.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        g.drawString(text, x, y);
        g.dispose();
    }
}
