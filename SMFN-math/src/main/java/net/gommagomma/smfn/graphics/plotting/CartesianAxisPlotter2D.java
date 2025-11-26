package net.gommagomma.smfn.graphics.plotting;

import net.gommagomma.smfn.graphics.core.Renderer2D;
import net.gommagomma.smfn.graphics.core.Viewport;
import java.awt.Color;

/**
 * Utility per plottare assi cartesiani 2D, griglia e, opzionalmente, etichette.
 */
public class CartesianAxisPlotter2D
{
    public static void plotAxes(Renderer2D renderer, Viewport viewport, Color color) {
        renderer.setColor(color);

        // Calcola la posizione in pixel dello zero matematico (0, 0)
        int xZeroPixel = viewport.convertMathXToPixelX(0.0);
        int yZeroPixel = viewport.convertMathYToPixelY(0.0);
        
        int width = renderer.getWidth();
        int height = renderer.getHeight();

        // Disegna l'asse X (orizzontale)
        // Se lo zero è visibile sullo schermo, disegna una linea orizzontale
        if (yZeroPixel >= 0 && yZeroPixel <= height) {
            renderer.drawLine(0, yZeroPixel, width, yZeroPixel);
        }
        
        // Disegna l'asse Y (verticale)
        // Se lo zero è visibile sullo schermo, disegna una linea verticale
        if (xZeroPixel >= 0 && xZeroPixel <= width) {
            renderer.drawLine(xZeroPixel, 0, xZeroPixel, height);
        }

        // NOTA: Implementare qui la logica per le tacche (tick marks) e le etichette 
        // richiederebbe il calcolo automatico dei "bei" intervalli numerici
        // (es. 0.5, 1.0, 1.5) all'interno del range della viewport.
    }
}
