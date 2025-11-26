package net.gommagomma.smfn.graphics.drivers.swing;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import net.gommagomma.smfn.graphics.core.Renderer2D;

public class SwingRenderer2D
extends Canvas
implements Renderer2D
{
    private final int width, height;
    private Color currentColor = Color.BLACK;
    private BufferStrategy bufferStrategy;

    public SwingRenderer2D(int width, int height) {
        this.width = width; this.height = height;
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
    @Override public void drawPoint(int x, int y) { /* ... */ }
    @Override public void drawText(String text, int x, int y) { /* ... */ }

    @Override
    public void fillRect(int x, int y, int w, int h) {
        Graphics g = bufferStrategy.getDrawGraphics();
        g.setColor(currentColor);
        g.fillRect(x, y, w, h);
        g.dispose();
    }

    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }
    public void flush() { if (bufferStrategy != null) bufferStrategy.show(); }

    @Override
    public void drawOverlayText(String text, int x, int y, Color color) {
        Graphics g = bufferStrategy.getDrawGraphics();
        g.setColor(color);
        // Impostiamo un font più piccolo e pulito per l'HUD
        g.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        g.drawString(text, x, y);
        g.dispose();
    }
}
