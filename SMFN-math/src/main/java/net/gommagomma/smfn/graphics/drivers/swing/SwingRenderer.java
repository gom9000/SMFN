package net.gommagomma.smfn.graphics.drivers.swing;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.drivers.image.ImageRenderer;


/**
 * Implementazione di {@link Renderer} per Java Swing/AWT: una finestra che mostra il contenuto di
 * un {@link ImageRenderer}.
 */
public class SwingRenderer
extends Canvas
implements Renderer
{
    private static final long serialVersionUID = 1L;

    private final transient ImageRenderer surface;
    private transient BufferStrategy bufferStrategy;
    private transient Graphics g; // Graphics dello schermo: usato solo per il blit finale in endDrawingAndFlush().


    public SwingRenderer(int width, int height) {
        this.surface = new ImageRenderer(width, height);
        setPreferredSize(new Dimension(width, height));
        setSize(width, height);
    }

    /** Antialiasing di linee e testo (vedi {@link ImageRenderer#setAntialiasing}); spento di default. */
    public void setAntialiasing(boolean enabled) { surface.setAntialiasing(enabled); }

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
            // Un solo blit dell'intera immagine sullo schermo, non migliaia/milioni di chiamate di disegno dirette.
            g.drawImage(surface.getImage(), 0, 0, null);
            g.dispose();
            g = null;
        }
        if (bufferStrategy != null) {
            bufferStrategy.show();
        }
    }


    @Override
    public void paint(Graphics graphics) {
        graphics.drawImage(surface.getImage(), 0, 0, null);
    }

    // Il comportamento di default (cancellare lo sfondo e poi dipingere) farebbe sfarfallare.
    @Override
    public void update(Graphics graphics) {
        paint(graphics);
    }


    // primitive di disegno: tutte sull'immagine in memoria, mai sullo schermo direttamente.

    // getWidth()/getHeight() sono quelli di Canvas, che coincidono con le dimensioni dell'immagine.

    @Override public void clear(RgbColor color) { surface.clear(color); }
    @Override public void setColor(RgbColor color) { surface.setColor(color); }
    @Override public void drawLine(int x1, int y1, int x2, int y2) { surface.drawLine(x1, y1, x2, y2); }
    @Override public void drawPoint(int x, int y) { surface.drawPoint(x, y); }
    @Override public void drawText(String text, int x, int y) { surface.drawText(text, x, y); }
    @Override public void drawOverlayText(String text, int x, int y, RgbColor color) { surface.drawOverlayText(text, x, y, color); }
    @Override public void fillPolygon(int[] xs, int[] ys) { surface.fillPolygon(xs, ys); }
    @Override public void fillRect(int x, int y, int width, int height) { surface.fillRect(x, y, width, height); }
}
