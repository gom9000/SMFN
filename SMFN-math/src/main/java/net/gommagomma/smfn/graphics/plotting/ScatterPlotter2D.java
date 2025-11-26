package net.gommagomma.smfn.graphics.plotting;

import net.gommagomma.smfn.graphics.core.Renderer1D; // Usiamo Renderer1D che estende Renderer
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.math.geometry.Point;

import java.awt.Color;
import java.util.List;

/**
 * Utility per plottare un insieme discreto di punti (scatter plot) su un Renderer.
 */
public class ScatterPlotter2D
{
    /**
     * Disegna una lista di punti usando il renderer e la viewport forniti.
     * @param renderer Il renderer da usare.
     * @param viewport La viewport per la conversione delle coordinate.
     * @param points La lista dei punti matematici da disegnare.
     * @param pointColor Il colore con cui disegnare i punti.
     */
    public static void plotPoints(Renderer1D renderer, Viewport viewport, List<Point> points, Color pointColor)
    {
        renderer.setColor(pointColor);
        
        for (Point p : points) {
            // Estrai le coordinate double dal Point matematico
            double mathX = p.getX().getValue(); // Assumendo getValue() esista in Real
            double mathY = p.getY().getValue();
            
            // Converti in coordinate pixel
            int pixelX = viewport.convertMathXToPixelX(mathX);
            int pixelY = viewport.convertMathYToPixelY(mathY);
            
            // Disegna il punto (utilizza drawPoint() o fillRect(x, y, 2, 2))
            renderer.drawPoint(pixelX, pixelY);
        }
    }
}
