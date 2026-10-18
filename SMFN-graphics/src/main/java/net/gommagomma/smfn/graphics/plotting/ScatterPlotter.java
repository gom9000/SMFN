package net.gommagomma.smfn.graphics.plotting;

import java.util.List;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.math.geometry.Point;


/**
 * Utility per plottare un insieme discreto di punti (scatter plot) su un Renderer. Un punto con
 * una coordinata non finita (NaN o infinito) non viene disegnato.
 */
public class ScatterPlotter
{
	private ScatterPlotter() {}


    /**
     * Disegna una lista di punti usando il renderer e la viewport forniti.
     * @param renderer Il renderer da usare.
     * @param viewport La viewport per la conversione delle coordinate.
     * @param points La lista dei punti matematici da disegnare.
     * @param color Il colore con cui disegnare i punti.
     */
    public static void plotPoints(Renderer renderer, Viewport2D viewport, List<Point> points, RgbColor color)
    {
        renderer.setColor(color);

        for (Point p : points)
        {
            // Estrai le coordinate double dal Point matematico
            double mathX = p.getX().getValue();
            double mathY = p.getY().getValue();

            // Un punto non finito non ha posizione sullo schermo: si salta
            if (!Double.isFinite(mathX) || !Double.isFinite(mathY)) continue;

            // Converti in coordinate pixel
            int pixelX = viewport.convertMathXToPixelX(mathX);
            int pixelY = viewport.convertMathYToPixelY(mathY);

            // Disegna il punto (utilizza drawPoint() o fillRect(x, y, 2, 2))
            renderer.drawPoint(pixelX, pixelY);
        }
    }
}
