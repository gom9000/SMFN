package net.gommagomma.smfn.demo.fractals;

import net.gommagomma.smfn.graphics.core.RgbColor;
import java.text.DecimalFormat;
import java.util.function.BiFunction;

import javax.swing.SwingUtilities;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.ColorFieldPlotter;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.analysis.fractals.MandelbrotFunction;


/**
 * Mandelbrot interattivo: si sposta trascinando, si zooma con la rotellina o con un rettangolo
 * (Maiusc/Ctrl + trascinamento). Le iterazioni crescono con lo zoom. La demo descrive soltanto la
 * scena: finestra, mouse e ridisegno li gestisce {@link SwingWindow#showInteractive}.
 */
public class MandelbrotIterativePlot
{
    private static final DecimalFormat DF = new DecimalFormat("0.000E0"); // Formattazione scientifica

    // --- Parametri per l'Iterazione Dinamica ---
    private static final int BASE_ITERATIONS = 100;   // Iterazioni minime
    private static final double SCALE_FACTOR = 300.0; // Fattore di tuning per lo zoom
    private static final double INITIAL_WIDTH = 3.0;  // Ampiezza iniziale: [-2, 1]

    private final MandelbrotFunction mandelbrotFunction = new MandelbrotFunction(BASE_ITERATIONS);
    private final BiFunction<Double, Double, Complex> domainAdapter = Complex::new;
    private int currentMaxIterations = BASE_ITERATIONS;
    private final ColorMapper<Natural> colorMapper = r -> {
        double value = r.getValue();
        if (value == 0 || value >= currentMaxIterations) return RgbColor.BLACK;
        float hue = (float) Math.sqrt(value / currentMaxIterations);   // radice: piu' contrasto vicino al bordo
        return RgbColor.ofHsb(0.6f, 1.0f, hue);
    };

    private void drawScene(Renderer renderer, Viewport2D viewport)
    {
        double rangeX = viewport.maxX - viewport.minX;
        double rangeY = viewport.maxY - viewport.minY;

        // Iterazioni: I_base + log2(ampiezza iniziale / ampiezza) * C
        double logFactor = (rangeX > 0) ? Math.log(INITIAL_WIDTH / rangeX) / Math.log(2.0) : 0;
        currentMaxIterations = (int) Math.max(BASE_ITERATIONS, BASE_ITERATIONS + logFactor * SCALE_FACTOR);
        mandelbrotFunction.setMaxIterations(currentMaxIterations);

        ColorFieldPlotter.plotFunctionParallel(renderer, viewport, mandelbrotFunction, domainAdapter, colorMapper);
        CartesianAxisPlotter.plotAxes(renderer, viewport, RgbColor.DARK_GRAY, true);

        renderer.drawOverlayText("Range X: [" + DF.format(viewport.minX) + ", " + DF.format(viewport.maxX) + "] (Ampiezza: " + DF.format(rangeX) + ")", 10, 20, RgbColor.RED);
        renderer.drawOverlayText("Range Y: [" + DF.format(viewport.minY) + ", " + DF.format(viewport.maxY) + "] (Ampiezza: " + DF.format(rangeY) + ")", 10, 35, RgbColor.RED);
        renderer.drawOverlayText("Zoom: " + DF.format(INITIAL_WIDTH / rangeX) + "x", 10, 50, RgbColor.RED);
        renderer.drawOverlayText("Max Iterations: " + currentMaxIterations, 10, 65, RgbColor.RED);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            MandelbrotIterativePlot plot = new MandelbrotIterativePlot();
            SwingWindow.showInteractive("SMFN Mandelbrot Set Interactive Plot",
                new Viewport2D(-2.0, 1.0, -1.5, 1.5, 1600, 1200), plot::drawScene);
        });
    }
}
