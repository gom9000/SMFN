package net.gommagomma.smfn.demo.functions;

import java.awt.Color;
import java.util.function.BiFunction;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer2D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.functions.LinearFunction;

public class ComplexLinearPlot
{
    public static void main(String[] args)
    {
        // --- 1. Definizione della funzione matematica (f(z) = (1 + 0.5i)z) ---
        ComplexField C = ComplexField.INSTANCE;
        Complex m = new Complex(1.0, 0.5);
        Complex q = new Complex(0.0, 0.0);
        LinearFunction<Complex> complexFunction = new LinearFunction<>(C, m, q);

        // --- 2. Setup del contesto grafico ---
        int width = 600;
        int height = 600;

        SwingRenderer2D renderer = new SwingRenderer2D(width, height);
        SwingWindow.show(renderer, "SMFN Complex Linear Function Plot");

        Viewport viewport = new Viewport(-5.0, 5.0, -5.0, 5.0, width, height);

        // --- 3. Adattatori ---
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;

        ColorMapper<Complex> colorMapper = new ColorMapper<>() {
            @Override
            public Color map(Complex c) {
                double hue = (c.argument() + Math.PI) / (2 * Math.PI);
                hue = (hue < 0) ? hue + 1.0 : hue;

                double brightness = Math.min(1.0, c.modulus() / 10.0);

                return Color.getHSBColor((float) hue, 1.0f, (float) brightness);
            }
        };

        renderer.startDrawing();
        renderer.clear(Color.WHITE);

        FunctionPlotter2D.plotFunction(renderer, viewport, complexFunction, domainAdapter, colorMapper);
        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);

        renderer.endDrawingAndFlush();
    }
}
