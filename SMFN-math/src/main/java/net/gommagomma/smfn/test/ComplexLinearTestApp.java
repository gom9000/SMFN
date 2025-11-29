package net.gommagomma.smfn.test;

import java.awt.Color;
import java.util.function.BiFunction;

import javax.swing.JFrame;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer2D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.analysis.functions.LinearFunction;

public class ComplexLinearTestApp
{
    public static void main(String[] args)
    {
        // --- 1. Definizione della funzione matematica (f(z) = (1 + 0.5i)z) ---
        Complex m = new Complex(1.0, 0.5);
        Complex q = new Complex(0.0, 0.0);
        LinearFunction<Complex> complexFunction = new LinearFunction<>(m, q);

        // --- 2. Setup del contesto grafico ---
        int width = 600;
        int height = 600; // Manteniamo ratio 1:1 per il piano complesso
        
        SwingRenderer2D renderer = new SwingRenderer2D(width, height);
        JFrame frame = new JFrame("SMFN Complex Linear Function Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy();

        // Definisci l'area matematica (Viewport): [-5, 5] x [-5, 5] sul piano complesso
        Viewport viewport = new Viewport(-5.0, 5.0, -5.0, 5.0, width, height);

        // --- 3. Definizione degli adattatori (Adapter Pattern) ---
        
        // Adattatore Dominio: Combina X (reale) e Y (immaginario) in un Complex input
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;

        // Mappa il Complex output a un java.awt.Color
        ColorMapper<Complex> colorMapper = new ColorMapper<>() {
            @Override
            public Color map(Complex c) {
            	// Mappa l'argomento (angolo) all'Hue (tonalità)
                double hue = (c.argument() + Math.PI) / (2 * Math.PI);
                hue = (hue < 0) ? hue + 1.0 : hue; // Normalizza tra 0 e 1

                // Mappa il modulo (distanza) alla Brightness (luminosità)
                // Normalizziamo il modulo entro un range visibile, es. max 10.0
                double brightness = Math.min(1.0, c.modulus() / 10.0); 
                
                // Usiamo HSBColor model (Hue, Saturation=1.0, Brightness)
                return Color.getHSBColor((float)hue, 1.0f, (float)brightness);
            }
        };

        renderer.startDrawing();

        // --- 4. Processo di rendering ---
        renderer.clear(Color.WHITE);
        
        FunctionPlotter2D.plotFunction(
            renderer, viewport, complexFunction, domainAdapter, colorMapper
        );

        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);

        renderer.endDrawingAndFlush();
    }
}
