package net.gommagomma.smfn.demo.functions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.SpacePlotter;
import net.gommagomma.smfn.graphics.plotting.SurfaceAxisPlotter;
import net.gommagomma.smfn.math.geometry.Point;

/**
 * Il nodo a trifoglio, una curva chiusa nello spazio, che si ruota con il mouse come una superficie:
 * <pre>
 *   x = sin t + 2 sin 2t,   y = cos t - 2 cos 2t,   z = -sin 3t        (t in [0, 2 pi])
 * </pre>
 * Una curva non nasconde se stessa: i tratti dietro si vedono attraverso quelli davanti. Per questo
 * conta il movimento, che fa capire quale passa sopra e quale sotto. I punti rossi sono i tre
 * incroci del nodo visti dall'alto (a t = pi/3 + k * 2 pi/3 la curva e' alla quota minima o massima).
 * <p>
 * Pareti e assi danno il riferimento: le pareti stanno dietro, gli assi con le tacche davanti.
 */
public class SpaceCurveInteractivePlot
{
    private static Point trefoil(double t)
    {
        return new Point(Math.sin(t) + 2.0 * Math.sin(2.0 * t), Math.cos(t) - 2.0 * Math.cos(2.0 * t), -Math.sin(3.0 * t));
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(SpaceCurveInteractivePlot::run);
    }

    private static void run()
    {
        Viewport3D initial = new Viewport3D(-3.5, 3.5, -3.5, 3.5, -1.5, 1.5, 800, 600, 30.0, 35.0);

        List<Point> extremes = new ArrayList<>();
        for (int k = 0; k < 6; k++) {
            extremes.add(trefoil(Math.PI / 6.0 + k * Math.PI / 3.0));
        }

        SwingWindow.showInteractive("SMFN Space Curve: nodo a trifoglio (trascina per ruotare, tasto destro per spostare, rotellina per lo zoom)",
            initial, (renderer, view) -> {
                SurfaceAxisPlotter.plotWalls(renderer, view, RgbColor.DARK_GRAY);
                SpacePlotter.plotCurve(renderer, view, SpaceCurveInteractivePlot::trefoil, 0.0, 2.0 * Math.PI, 400, RgbColor.CYAN);
                SpacePlotter.plotPoints(renderer, view, extremes, RgbColor.RED);
                SurfaceAxisPlotter.plotAxes(renderer, view, RgbColor.WHITE, true);
            });
    }
}
