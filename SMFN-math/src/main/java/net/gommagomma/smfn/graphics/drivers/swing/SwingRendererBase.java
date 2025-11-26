package net.gommagomma.smfn.graphics.drivers.swing;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import net.gommagomma.smfn.graphics.core.Renderer;


public abstract class SwingRendererBase
extends Canvas
implements Renderer
{
    private static final long serialVersionUID = 1L;
    protected Color currentColor = Color.BLACK;
    protected transient BufferStrategy bufferStrategy;
    protected transient Graphics g;


    protected SwingRendererBase(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setSize(width, height);
    }

    public void initBufferStrategy() {
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
    }

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


    // primitive di disegno:

    @Override
    public void clear(Color color) {
        if (g == null) return;
        g.setColor(color);
        g.fillRect(0, 0, super.getWidth(), super.getHeight());
    }

    @Override
    public void setColor(Color color) { 
        this.currentColor = color; 
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        if (g == null) return;
        g.setColor(this.currentColor);
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawPoint(int x, int y) {
        if (g == null) return;
        g.setColor(this.currentColor);
        g.fillRect(x, y, 1, 1); 
    }

    @Override
    public void drawText(String text, int x, int y) {
        if (g == null) return;
        g.setColor(this.currentColor);
        g.drawString(text, x, y);
    }

    @Override
    public void drawOverlayText(String text, int x, int y, Color color) {
        if (g == null) return;
        Color originalColor = g.getColor();
        Font originalFont = g.getFont();
        
        g.setColor(color);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        g.drawString(text, x, y);
        
        g.setFont(originalFont);
        g.setColor(originalColor); 
    }
}
