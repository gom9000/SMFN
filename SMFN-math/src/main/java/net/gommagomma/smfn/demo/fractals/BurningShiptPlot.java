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
import net.gommagomma.smfn.math.analysis.fractals.BurningShipFunction;

public class BurningShiptPlot {
    public static void main(String[] args) {
        // Definizione della funzione matematica
        final int MAX_ITERATIONS = 100;
        BurningShipFunction function = new BurningShipFunction(MAX_ITERATIONS);

        // Setup del contesto grafico
        int width = 800;
        int height = 600;
        SwingRenderer2D renderer = new SwingRenderer2D(width, height);
        SwingWindow.show(renderer, "SMFN BurningShip Set Plot");
        Viewport viewport = new Viewport(-2, 1.0, -2.2, 1.5, width, height);

        // Definisce la mappa colore del risultato del solver
        ColorMapper<Natural> colorMapper = new ColorMapper<>() {
            @Override
            public Color map(Natural r) {
            	double value = r.getValue();
                if (value == 0) return Color.BLACK; 
                if (value >= MAX_ITERATIONS) return Color.BLACK; 
                float hue = (float) (value / MAX_ITERATIONS);
                hue = (float) Math.sqrt(hue); 
                float brightness = hue; 
                return Color.getHSBColor(0.6f, 1.0f, brightness);
            }
        };

        // Adattatore Dominio: Combina X (reale) e Y (immaginario) in un Complex input 'c'
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;

        // Rendering
        renderer.startDrawing();
        renderer.clear(Color.WHITE);
        FunctionPlotter2D.plotFunction(renderer, viewport, function, domainAdapter, colorMapper);
        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);
        renderer.endDrawingAndFlush();
    }
}
