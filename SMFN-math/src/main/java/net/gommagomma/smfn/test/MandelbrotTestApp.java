package net.gommagomma.smfn.test;

import java.awt.Color;
import java.util.function.BiFunction;

import javax.swing.JFrame;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer2D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter2D;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.fractals.MandelbrotFunction;

public class MandelbrotTestApp {
    public static void main(String[] args) {
        // --- 1. Definizione della funzione matematica (Mandelbrot Set) ---
        final int MAX_ITERATIONS = 100;
        MandelbrotFunction mandelbrotFunction = new MandelbrotFunction(MAX_ITERATIONS);

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;
        
        SwingRenderer2D renderer = new SwingRenderer2D(width, height);
        JFrame frame = new JFrame("SMFN Mandelbrot Set Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy();

        // Definisci l'area matematica (Viewport): [-2.0, 1.0] x [-1.5, 1.5] 
        // L'area classica che contiene l'intero set
        Viewport viewport = new Viewport(-2, 1, -1.5, 1.5, width, height);

        // --- 3. Definizione degli adattatori (Adapter Pattern) ---
        
        // Adattatore Dominio: Combina X (reale) e Y (immaginario) in un Complex input 'c'
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;

        ColorMapper<Real> colorMapper = new ColorMapper<>() {
            @Override
            public Color map(Real r) {
            	double value = r.getValue();
                if (value == 0) return Color.BLACK; 
                if (value >= MAX_ITERATIONS) return Color.BLACK; 
                float hue = (float) (value / MAX_ITERATIONS);
                hue = (float) Math.sqrt(hue); 
                float brightness = hue; 
                return Color.getHSBColor(0.6f, 1.0f, brightness);
            }
        };

        // --- 4. Processo di rendering ---
        // Pulisci lo sfondo prima di disegnare (anche se il plotter coprirà tutto)
        renderer.clear(Color.WHITE);
        
        // Usa il FunctionPlotter2D per disegnare il frattale
        FunctionPlotter2D.plotFunction(
            renderer, viewport, mandelbrotFunction, domainAdapter, colorMapper
        );

        CartesianAxisPlotter2D.plotAxes(renderer, viewport, Color.DARK_GRAY);

        // Mostra il risultato a schermo
        renderer.flush();
    }
}
