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


/**
 * Implementazione di {@link Renderer} per Java Swing/AWT.
 */
public class SwingRenderer
extends Canvas
implements Renderer
{
    private static final long serialVersionUID = 1L;
    private Color currentColor = Color.BLACK;
    private transient BufferStrategy bufferStrategy;
    private transient Graphics g;
    private transient BufferedImage pixelBuffer;
    private transient int[] pixelData;
    private transient Graphics2D bufferGraphics;
    private int currentRgb = 0xFF000000;


    public SwingRenderer(int width, int height) {
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


    // Punto di conversione fra il colore neutro di graphics.core e java.awt.Color.
    private static Color toAwt(RgbColor color) {
        return new Color(color.r, color.g, color.b, color.a);
    }

    private static int toArgb(RgbColor color) {
        return (color.a << 24) | (color.r << 16) | (color.g << 8) | color.b;
    }


    // primitive di disegno (tutte sul buffer offscreen, mai sullo schermo direttamente):

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

    @Override
    public void fillPolygon(int[] xs, int[] ys) {
        if (xs == null || ys == null || xs.length != ys.length) {
            throw new IllegalArgumentException("Vertici non validi: xs="
                + (xs == null ? "null" : xs.length + " elementi") + ", ys="
                + (ys == null ? "null" : ys.length + " elementi") + " (servono due array non nulli di pari lunghezza)");
        }
        if (xs.length < 3) return; // poligono degenere: niente da riempire
        bufferGraphics.setColor(this.currentColor);
        bufferGraphics.fillPolygon(xs, ys, xs.length);
    }

    // Override dell'implementazione di default (che passa da fillPolygon): AWT ha il
    // riempimento di rettangoli nativo, piu' rapido di un poligono generico a quattro vertici.
    @Override
    public void fillRect(int x, int y, int width, int height) {
        bufferGraphics.setColor(this.currentColor);
        bufferGraphics.fillRect(x, y, width, height);
    }
}
