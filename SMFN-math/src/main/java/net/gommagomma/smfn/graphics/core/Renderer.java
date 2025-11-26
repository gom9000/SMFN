package net.gommagomma.smfn.graphics.core;

import java.awt.Color;

/**
 * Interfaccia base per disegnare primitive grafiche su un target generico.
 */
public interface Renderer
{
	void startDrawing();
    void endDrawingAndFlush();

    int getWidth();
    int getHeight();

    void clear(Color color);
    void setColor(Color color);
    void drawLine(int x1, int y1, int x2, int y2);
    void drawPoint(int x, int y);
    void drawText(String text, int x, int y);
    void drawOverlayText(String text, int x, int y, Color color);
}
