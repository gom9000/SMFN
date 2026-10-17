package net.gommagomma.smfn.demo.fractals;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.SurfaceAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.SurfacePlotter;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.analysis.fractals.MandelbrotFunction;

/**
 * L'insieme di Mandelbrot come superficie z = f(x, y).
 * I punti dell'insieme (che non fuggono mai) formano l'altopiano alla quota massima.
 * Il piano orizzontale e' il piano complesso: x e' la parte reale e y la parte immaginaria di c.
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

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;

        SwingRenderer renderer = new SwingRenderer(width, height);
        SwingWindow.show(renderer, "SMFN Mandelbrot Surface: z = escape iterations");

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

        // --- 4. Rendering ---
        // Una griglia fitta: l'interesse e' nel bordo dell'insieme, dove i gradini si infittiscono
        int cells = 200;
        renderer.startDrawing();
        renderer.clear(RgbColor.BLACK);
        SurfacePlotter.plotSurface(renderer, viewport, function, domainAdapter, heightAdapter, colorMapper, cells, cells);
        SurfaceAxisPlotter.plotAxes(renderer, viewport, RgbColor.WHITE, true);
        renderer.endDrawingAndFlush();
    }
}
