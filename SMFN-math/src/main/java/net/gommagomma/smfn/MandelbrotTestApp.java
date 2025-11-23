package net.gommagomma.smfn;

import java.awt.Color;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.CoordinateMapper;
import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.plotting.ElementPlaneViewport;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter;
import net.gommagomma.smfn.graphics.plotting.LinearComplexCoordinateMapper;
import net.gommagomma.smfn.graphics.swing.SwingRenderer;
import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.fractals.MandelbrotFunction;

public class MandelbrotTestApp
{
    public static void main(String[] args)
    {
        final int width = 1024;
        final int height = 768;
        final int maxIterations = 100;

        System.out.println("Avvio rendering dell'insieme di Mandelbrot...");

        // --- 1. Configurazione della Logica di Calcolo
        MandelbrotFunction mandelbrotFn = new MandelbrotFunction(maxIterations);

        // --- 2. Configurazione del piano complesso che vogliamo visualizzare
        double minX = -2.0;
        double maxX = 1.0;
        double minY = -1.5;
        double maxY = 1.5;

        // --- 3. Configurazione del mappatore di coordinate
        CoordinateMapper<Complex> coordinateMapper = new LinearComplexCoordinateMapper();

        // --- 4. Configurazione della viewport
        Viewport<Complex> viewport = new ElementPlaneViewport<>(
            width, height,
            minX, maxX, minY, maxY,
            coordinateMapper
        );

        // --- 5. Configurazione del mappatore di colori
        //ColorMapper colorMapper = new LinearColorMapper(/* parametri colori */);
        ColorMapper colorMapper = new ColorMapper() {
            @Override
            public int toRGB(double value) {
                if (value == 0) return Color.BLACK.getRGB(); 
                if (value >= maxIterations) return Color.BLACK.getRGB(); 
                float hue = (float) (value / maxIterations);
                hue = (float) Math.sqrt(hue); 
                float brightness = hue; 
                return Color.HSBtoRGB(0.6f, 1.0f, brightness);
            }
        };
        
        // --- 6. Configurazione del Renderer
        Renderer swingRenderer = new SwingRenderer(width, height);

        // --- 7. Configurazione del Plotter
        FunctionPlotter<Complex, Real> plotter = new FunctionPlotter<>();

        // --- 8. Configurazione dell'adattatore del risultato 'Real' nel 'double' necessario
        Function<Real, Double> realToDoubleConverter = new Function<Real, Double>() {
            @Override public Double apply(Real r) { return r.getValue(); }
        };

        // --- 9. Esecuzione del Plotting
        long startTime = System.currentTimeMillis();
        plotter.plot(
            mandelbrotFn,
            viewport,
            colorMapper,
            swingRenderer,
            realToDoubleConverter
        );
        long endTime = System.currentTimeMillis();

        System.out.println("Rendering completato in " + (endTime - startTime) + " ms.");

        // --- 10. Visualizzazione del Risultato
        swingRenderer.display(); 
    }
}
