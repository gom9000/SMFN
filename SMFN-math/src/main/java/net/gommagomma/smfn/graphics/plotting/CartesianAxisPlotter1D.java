package net.gommagomma.smfn.graphics.plotting;

import net.gommagomma.smfn.graphics.core.Renderer1D; // Import corretto
import net.gommagomma.smfn.graphics.core.Viewport;
import java.awt.Color;

/**
 * Utility per plottare gli assi cartesiani su un Renderer1D.
 */
public class CartesianAxisPlotter1D
{
    public static void plotAxes(Renderer1D renderer, Viewport viewport, Color color) {
        // ... (Logica di implementazione per disegnare linee e tacche) ...
        int xZeroPixel = viewport.convertMathXToPixelX(0.0);
        int yZeroPixel = viewport.convertMathYToPixelY(0.0);
        
        renderer.setColor(color);

        renderer.drawLine(0, yZeroPixel, renderer.getWidth(), yZeroPixel); // Asse X
        renderer.drawLine(xZeroPixel, 0, xZeroPixel, renderer.getHeight()); // Asse Y
    }
}
