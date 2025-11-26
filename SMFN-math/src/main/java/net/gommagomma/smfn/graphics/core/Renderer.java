package net.gommagomma.smfn.graphics.core;

import java.awt.Color;

/**
 * Interfaccia base per disegnare primitive grafiche su un target generico.
 */
public interface Renderer
{
    void clear(Color color);
    void setColor(Color color);
    void drawLine(int x1, int y1, int x2, int y2);
    void drawPoint(int x, int y);
    void drawText(String text, int x, int y);
    int getWidth();
    int getHeight();

    /**
     * Disegna del testo in un overlay, le coordinate sono in pixel e non risentono della Viewport.
     */
    void drawOverlayText(String text, int x, int y, Color color);
}
