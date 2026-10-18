package net.gommagomma.smfn.demo.fractals;

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
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.analysis.fractals.MandelbrotFunction;

/**
 * L'insieme di Mandelbrot come superficie z = f(x, y).
 * I punti dell'insieme (che non fuggono mai) formano l'altopiano alla quota massima.
 * Il piano orizzontale e' il piano complesso: x e' la parte reale e y la parte immaginaria di c.
 * <p>
 * Interattivo: si ruota col mouse (vedi {@code SwingWindow.showInteractive}).
 */
public class MandelbrotSurfacePlot
{
    public static void main(String[] args)
    {
        // --- 1. Definizione della funzione matematica ---
        final int MAX_ITERATIONS = 50;
        MandelbrotFunction function = new MandelbrotFunction(MAX_ITERATIONS);

        // Altezza massima: log(1 + iterazioni) per un punto che non fugge mai
        final double MAX_HEIGHT = Math.log1p(MAX_ITERATIONS);

        // --- 2. Punto di vista iniziale ---
        int width = 800;
        int height = 600;

        // Piano complesso [-2, 1] x [-1.5, 1.5]; l'altezza va da 0 a MAX_HEIGHT
        Viewport3D viewport = new Viewport3D(-2, 1, -1.5, 1.5, 0, MAX_HEIGHT, width, height, 20.0, 40.0);

        // --- 3. Adattatori ---
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;
        Function<Natural, Double> heightAdapter = iterations -> Math.log1p(iterations.getValue());

        // Colore = altezza (blu in basso, rosso in alto): l'altopiano dell'insieme e' rosso
        ColorMapper<Natural> colorMapper = iterations -> {
            double t = Math.max(0.0, Math.min(1.0, Math.log1p(iterations.getValue()) / MAX_HEIGHT));
            return RgbColor.ofHsb((float) (0.66 * (1.0 - t)), 0.85f, 0.9f);
        };

        // --- 4. Finestra interattiva: trascina per ruotare, tasto destro per spostare, rotellina per lo zoom ---
        // La superficie si campiona una volta sola; ogni ridisegno cambia solo la proiezione.
        int cells = 200;
        var grid = SurfaceGrid.sample(viewport, function, domainAdapter, heightAdapter, cells, cells);
        SwingUtilities.invokeLater(() -> SwingWindow.showInteractive("SMFN Mandelbrot Surface: z = escape iterations", viewport, (renderer, view) -> {
        	SurfaceAxisPlotter.plotWalls(renderer, view, RgbColor.DARK_GRAY);
            SurfacePlotter.plotSurface(renderer, view, grid, colorMapper);
            SurfaceAxisPlotter.plotAxes(renderer, view, RgbColor.WHITE, true);
        }));
    }
}
