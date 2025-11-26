package net.gommagomma.smfn.graphics.core;


/**
 * Interfaccia specifica per il rendering 2D (heatmap, frattali, superfici di colore).
 */
public interface Renderer2D
extends Renderer
{
    /**
     * Disegna un rettangolo pieno con il colore corrente.
     */
    void fillRect(int x, int y, int width, int height);
}
