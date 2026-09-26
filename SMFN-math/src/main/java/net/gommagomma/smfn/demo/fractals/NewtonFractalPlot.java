package net.gommagomma.smfn.demo.fractals;

import java.awt.Color;
import java.util.function.BiFunction;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer2D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.analysis.fractals.NewtonFractalFunction;

public class NewtonFractalPlot {
    public static void main(String[] args) {
        // Definizione della funzione per p(z) = z^3 - 1
        final int MAX_ITERATIONS = 50;
        NewtonFractalFunction function = NewtonFractalFunction.forCubicMinusOne(MAX_ITERATIONS);

        // Setup del contesto grafico
        int width = 800;
        int height = 800;
        SwingRenderer2D renderer = new SwingRenderer2D(width, height);
        SwingWindow.show(renderer, "SMFN Newton Fractal Set Plot (z^3 - 1 = 0)");
        Viewport viewport = new Viewport(-2.0, 2.0, -2.0, 2.0, width, height);

        // Definisce la mappa colore basata sulle iterazioni necessarie alla convergenza
        ColorMapper<Natural> colorMapper = new ColorMapper<>() {
            @Override
            public Color map(Natural r) {
                double value = r.getValue();
                
                // Punti che non convergono entro MAX_ITERATIONS (punti di confine/singolarita')
                if (value >= MAX_ITERATIONS) {
                    return Color.BLACK;
                }
                
                // Mappatura ciclo-sfumatura HSB basata sul numero di iterazioni
                // Le regioni piatte attorno alle radici convergono rapidamente (pochi passi), 
                // mentre i confini frattali richiedono molte piu' iterazioni.
                float hue = (float) (0.55f + 0.45f * (value / MAX_ITERATIONS));
                float brightness = (float) (1.0 - (value / MAX_ITERATIONS));
                
                return Color.getHSBColor(hue, 0.85f, brightness);
            }
        };

        // Adattatore Dominio: Combina X (reale) e Y (immaginario) nel numero complesso 'z0'
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;

        // Rendering
        renderer.startDrawing();
        renderer.clear(Color.WHITE);
        FunctionPlotter2D.plotFunction(renderer, viewport, function, domainAdapter, colorMapper);
        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);
        renderer.endDrawingAndFlush();
    }
}