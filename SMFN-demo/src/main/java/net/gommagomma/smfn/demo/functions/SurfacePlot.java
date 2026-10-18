package net.gommagomma.smfn.demo.functions;

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
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Superficie z = sin(r) / r, con r = sqrt(x^2 + y^2): facce piene colorate dall'altezza, con gli assi.
 * <p>
 * Il dominio e' un punto del piano, qui un {@code Complex} costruito da (x, y); la funzione
 * restituisce un {@code Real} che e' l'altezza. L'adattatore del dominio e quello dell'altezza
 * sono gli unici punti in cui il modello matematico incontra il plotter.
 * <p>
 * Interattivo: si ruota col mouse (vedi {@code SwingWindow.showInteractive}).
 */
public class SurfacePlot
{
    public static void main(String[] args)
    {
        // --- 1. Modello matematico: z = sin(r) / r (prolungata con 1 nell'origine) ---
        Mapping<Complex, Real> sinc = point -> {
            double r = Math.hypot(point.getRe(), point.getIm());
            return new Real(r == 0.0 ? 1.0 : Math.sin(r) / r);
        };

        // --- 2. Punto di vista iniziale ---
        int width = 800;
        int height = 600;

        double range = 12.0;
        Viewport3D viewport = new Viewport3D(-range, range, -range, range, -0.3, 1.0, width, height, 35.0, 40.0);

        // --- 3. Adattatori ---
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;
        Function<Real, Double> heightAdapter = Real::getValue;
        ColorMapper<Real> colorMapper = height3d -> {
            double t = Math.max(0.0, Math.min(1.0, (height3d.getValue() + 0.3) / 1.3));
            return RgbColor.ofHsb((float) (0.66 - 0.66 * t), 0.85f, 0.95f); // blu (basso) -> rosso (alto)
        };

        // --- 4. Finestra interattiva: trascina per ruotare, tasto destro per spostare, rotellina per lo zoom ---
        // La superficie si campiona una volta sola; ogni ridisegno cambia solo la proiezione.
        int cells = 30;
        var grid = SurfaceGrid.sample(viewport, sinc, domainAdapter, heightAdapter, cells, cells);
        SwingUtilities.invokeLater(() -> SwingWindow.showInteractive("SMFN Surface Example: z = sin(r) / r", viewport, (renderer, view) -> {
            SurfacePlotter.plotSurface(renderer, view, grid, colorMapper);
            SurfacePlotter.plotWireframe(renderer, view, grid, RgbColor.GREEN);
            SurfaceAxisPlotter.plotAxes(renderer, view, RgbColor.WHITE, true);
        }));
    }
}
