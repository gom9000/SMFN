package net.gommagomma.smfn.client;

import java.awt.Color;
import java.util.function.BiFunction;

import javax.swing.JFrame;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer2D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.analysis.fractals.JuliaFunction;

public class JuliaFunctionClient {
    public static void main(String[] args) {
        // --- 1. Definizione della funzione matematica 
        final int MAX_ITERATIONS = 100;
        //final Complex constantC = new Complex(-0.11031, -0.67037);
        //final Complex constantC = new Complex(-1.25, 0);
        //final Complex constantC = new Complex(-0.74543, 0.11301);
        //final Complex constantC = new Complex(-0.194, 0.6557);
        //final Complex constantC = new Complex(-0.11, 0.6557);
        //final Complex constantC = new Complex(0, 1);
        //final Complex constantC = new Complex(0.31, 0.04);
        final Complex constantC = new Complex(0.27334, 0.00742);
        //final Complex constantC = new Complex(-0.481762, -0.531657);
        //final Complex constantC = new Complex(-0.39054, -0.58679);
        //final Complex constantC = new Complex(-0.15652, -1.03225);
        //final Complex constantC = new Complex(-0.123, 0.745); // Douady's Rabbit
        //final Complex constantC = new Complex(-0.8, 0.156); // Sierpinski Gasket
        //final Complex constantC = new Complex(-0.5, 0.5); // Seahorse/Dendrite
        //final Complex constantC = new Complex(0.285, 0.01); // cross
        JuliaFunction juliaFunction = new JuliaFunction(constantC, MAX_ITERATIONS);

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;
        
        SwingRenderer2D renderer = new SwingRenderer2D(width, height);
        JFrame frame = new JFrame("SMFN Julia Set (c = " + constantC + ") Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy();

        // Definisci l'area matematica (Viewport): [-2.0, 1.0] x [-1.5, 1.5] 
        // L'area classica che contiene l'intero set
        Viewport viewport = new Viewport(-1.5, 1.5, -1.5, 1.5, width, height);

        // --- 3. Definizione degli adattatori (Adapter Pattern) ---
        
        // Adattatore Dominio: Combina X (reale) e Y (immaginario) in un Complex input 'c'
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;

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

        // --- 4. Processo di rendering ---
        renderer.startDrawing();

        // Pulisci lo sfondo prima di disegnare (anche se il plotter coprirà tutto)
        renderer.clear(Color.WHITE);
        
        // Usa il FunctionPlotter2D per disegnare il frattale
        FunctionPlotter2D.plotFunction(
            renderer, viewport, juliaFunction, domainAdapter, colorMapper
        );

        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);

        // Mostra il risultato a schermo
        renderer.endDrawingAndFlush();
    }
}
