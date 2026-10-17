package net.gommagomma.smfn.demo.functions;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.SurfaceAxisPlotter;
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

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;

        SwingRenderer renderer = new SwingRenderer(width, height);
        SwingWindow.show(renderer, "SMFN Surface Example: z = sin(r) / r");

        double range = 12.0;
        Viewport3D viewport = new Viewport3D(-range, range, -range, range, -0.3, 1.0, width, height, 35.0, 40.0);

        // --- 3. Adattatori ---
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;
        Function<Real, Double> heightAdapter = Real::getValue;
        ColorMapper<Real> colorMapper = height3d -> {
            double t = Math.max(0.0, Math.min(1.0, (height3d.getValue() + 0.3) / 1.3));
            return RgbColor.ofHsb((float) (0.66 - 0.66 * t), 0.85f, 0.95f); // blu (basso) -> rosso (alto)
        };

        // --- 4. Rendering ---
        int cells = 30;
        renderer.startDrawing();
        renderer.clear(RgbColor.BLACK);
        //SurfacePlotter.plotSurface(renderer, viewport, sinc, domainAdapter, heightAdapter, colorMapper, cells, cells);
        SurfacePlotter.plotWireframe(renderer, viewport, sinc, domainAdapter, heightAdapter, RgbColor.GREEN, cells, cells);
        SurfaceAxisPlotter.plotAxes(renderer, viewport, RgbColor.WHITE, true); // dopo: gli assi stanno sul bordo vicino
        renderer.endDrawingAndFlush();
    }
}
