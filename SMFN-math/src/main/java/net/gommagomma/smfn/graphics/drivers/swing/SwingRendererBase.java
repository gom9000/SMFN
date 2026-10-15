package net.gommagomma.smfn.graphics.drivers.swing;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;


public abstract class SwingRendererBase
extends Canvas
implements Renderer
{
    private static final long serialVersionUID = 1L;
    protected Color currentColor = Color.BLACK;
    protected transient BufferStrategy bufferStrategy;
    protected transient Graphics g;
    protected transient BufferedImage pixelBuffer;
    protected transient int[] pixelData;
    protected transient Graphics2D bufferGraphics;
    protected int currentRgb = 0xFF000000;


    protected SwingRendererBase(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setSize(width, height);
        initPixelBuffer(width, height);
    }

    private void initPixelBuffer(int width, int height) {
        this.pixelBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.pixelData = ((DataBufferInt) pixelBuffer.getRaster().getDataBuffer()).getData();
        this.bufferGraphics = pixelBuffer.createGraphics();
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
            g.drawImage(pixelBuffer, 0, 0, null);
            g.dispose();
            g = null;
        }
        if (bufferStrategy != null) {
            bufferStrategy.show();
        }
    }


    protected static Color toAwt(RgbColor color) {
        return new Color(color.r, color.g, color.b, color.a);
    }

    private static int toArgb(RgbColor color) {
        return (color.a << 24) | (color.r << 16) | (color.g << 8) | color.b;
    }


    // primitive di disegno (tutte sul buffer offscreen):

    @Override
    public void clear(RgbColor color) {
        bufferGraphics.setColor(toAwt(color));
        bufferGraphics.fillRect(0, 0, pixelBuffer.getWidth(), pixelBuffer.getHeight());
    }

    @Override
    public void setColor(RgbColor color) {
        this.currentColor = toAwt(color);
        this.currentRgb = toArgb(color);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        bufferGraphics.setColor(this.currentColor);
        bufferGraphics.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawPoint(int x, int y) {
        if (x < 0 || x >= pixelBuffer.getWidth() || y < 0 || y >= pixelBuffer.getHeight()) return;
        pixelData[y * pixelBuffer.getWidth() + x] = currentRgb;
    }

    @Override
    public void drawText(String text, int x, int y) {
        bufferGraphics.setColor(this.currentColor);
        bufferGraphics.drawString(text, x, y);
    }

    @Override
    public void drawOverlayText(String text, int x, int y, RgbColor color) {
        Color originalColor = bufferGraphics.getColor();
        Font originalFont = bufferGraphics.getFont();

        bufferGraphics.setColor(toAwt(color));
        bufferGraphics.setFont(new Font("Monospaced", Font.PLAIN, 12));
        bufferGraphics.drawString(text, x, y);

        bufferGraphics.setFont(originalFont);
        bufferGraphics.setColor(originalColor);
    }
}