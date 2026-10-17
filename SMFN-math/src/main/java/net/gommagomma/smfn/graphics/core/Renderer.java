package net.gommagomma.smfn.graphics.core;

/**
 * Superficie di disegno: la capacita' di tracciare primitive su un piano di pixel.
 */
public interface Renderer
{
    void startDrawing();
    void endDrawingAndFlush();

    int getWidth();
    int getHeight();

    /** Riempie l'intera superficie con il colore dato. */
    void clear(RgbColor color);

    /** Imposta il colore usato dalle primitive successive. */
    void setColor(RgbColor color);

    void drawLine(int x1, int y1, int x2, int y2);
    void drawPoint(int x, int y);
    void drawText(String text, int x, int y);

    /** Testo in coordinate pixel fisse con colore proprio, che non modifica il colore corrente. */
    void drawOverlayText(String text, int x, int y, RgbColor color);


    void fillPolygon(int[] xs, int[] ys);
    default void fillRect(int x, int y, int width, int height)
    {
        if (width <= 0 || height <= 0) return;
        fillPolygon(new int[] { x, x + width, x + width, x },
                    new int[] { y, y, y + height, y + height });
    }
}
