package net.gommagomma.smfn.demo.geometry;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.SwingUtilities;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.SurfaceAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.SurfaceGrid;
import net.gommagomma.smfn.graphics.plotting.SurfacePlotter;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.geometry.Circle;
import net.gommagomma.smfn.math.geometry.Point;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

/**
 * Il cerchio come superficie z = f(x, y), accanto alla versione 2D {@link GeometryCirclePlot}.
 * <p>
 * La funzione del cerchio e' la distanza dal centro meno il raggio: come superficie e' un cono
 * (con il vertice in basso, nel centro). Il cerchio stesso e' il suo insieme di livello z = 0,
 * evidenziato in ciano: nella versione 2D e' il bordo, qui e' la sezione a quota zero.
 * <p>
 * Interattivo: si ruota col mouse (vedi {@code SwingWindow.showInteractive}).
 */
public class GeometricConePlot
{
    public static void main(String[] args)
    {
        // --- 1. Definizione della funzione matematica ---
        Circle f = new Circle(new Point(0.0, 0.0), new Real(2.5));

        // --- 2. Punto di vista iniziale ---
        int width = 800;
        int height = 600;

        // Riquadro (x, y, z) e punto di vista (azimut, elevazione in gradi)
        Viewport3D viewport = new Viewport3D(-4, 4, -4, 4, -2.5, 3.2, width, height, 35.0, 35.0);

        // --- 3. Adattatori ---
        VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(RealField.INSTANCE, 2);
        BiFunction<Double, Double, Vector<Real>> domainAdapter = (x, y) -> V2.of(new Real[] { new Real(x), new Real(y) });
        Function<Real, Double> heightAdapter = Real::getValue;

        // Colore = altezza (blu in basso, rosso in alto); l'insieme di livello 0, cioe' la
        // curva che la versione 2D disegna, e' evidenziato in ciano.
        ColorMapper<Real> colorMapper = value -> {
            double v = value.getValue();
            if (Math.abs(v) < 0.1) return RgbColor.CYAN;
            double t = Math.max(0.0, Math.min(1.0, (v - (-2.5)) / (5.7)));
            return RgbColor.ofHsb((float) (0.66 * (1.0 - t)), 0.85f, 0.9f);
        };

        // --- 4. Finestra interattiva: trascina per ruotare, tasto destro per spostare, rotellina per lo zoom ---
        // La superficie si campiona una volta sola; ogni ridisegno cambia solo la proiezione.
        int cells = 60;
        var grid = SurfaceGrid.sample(viewport, f, domainAdapter, heightAdapter, cells, cells);
        SwingUtilities.invokeLater(() -> SwingWindow.showInteractive("SMFN Circle Surface: z = dist(P, C) - r", viewport, (renderer, view) -> {
            SurfacePlotter.plotSurface(renderer, view, grid, colorMapper);
            SurfacePlotter.plotWireframe(renderer, view, grid, RgbColor.GREEN);
            SurfaceAxisPlotter.plotAxes(renderer, view, RgbColor.WHITE, true);
        }));
    }
}
