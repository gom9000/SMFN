package net.gommagomma.smfn.demo.geometry;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.SurfaceAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.SurfacePlotter;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.geometry.Ellipse;
import net.gommagomma.smfn.math.geometry.Point;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

/**
 * L'ellisse come superficie z = f(x, y), accanto alla versione 2D {@link GeometryEllipsePlot}.
 * <p>
 * La funzione dell'ellisse e' (x/a)^2 + (y/b)^2 - 1: come superficie e' un paraboloide ellittico
 * (circolare se a = b). L'ellisse e' il suo insieme di livello z = 0, evidenziato in ciano.
 */
public class GeometricParaboloidPlot
{
    public static void main(String[] args)
    {
        // --- 1. Definizione della funzione matematica ---
        Ellipse f = new Ellipse(new Point(0.0, 0.0), new Real(3.0), new Real(1.5));

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;

        SwingRenderer renderer = new SwingRenderer(width, height);
        SwingWindow.show(renderer, "SMFN Ellipse Surface: z = (x/a)^2 + (y/b)^2 - 1");

        // Riquadro (x, y, z) e punto di vista (azimut, elevazione in gradi)
        Viewport3D viewport = new Viewport3D(-4, 4, -3, 3, -1, 5, width, height, 35.0, 35.0);

        // --- 3. Adattatori ---
        VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(RealField.INSTANCE, 2);
        BiFunction<Double, Double, Vector<Real>> domainAdapter = (x, y) -> V2.of(new Real[] { new Real(x), new Real(y) });
        Function<Real, Double> heightAdapter = Real::getValue;

        // Colore = altezza (blu in basso, rosso in alto); l'insieme di livello 0, cioe' la
        // curva che la versione 2D disegna, e' evidenziato in ciano.
        ColorMapper<Real> colorMapper = value -> {
            double v = value.getValue();
            if (Math.abs(v) < 0.1) return RgbColor.CYAN;
            double t = Math.max(0.0, Math.min(1.0, (v - (-1)) / (6)));
            return RgbColor.ofHsb((float) (0.66 * (1.0 - t)), 0.85f, 0.9f);
        };

        // --- 4. Rendering ---
        int cells = 60;
        renderer.startDrawing();
        renderer.clear(RgbColor.BLACK);
        SurfacePlotter.plotSurface(renderer, viewport, f, domainAdapter, heightAdapter, colorMapper, cells, cells);
        SurfaceAxisPlotter.plotAxes(renderer, viewport, RgbColor.WHITE, true);
        renderer.endDrawingAndFlush();
    }
}
