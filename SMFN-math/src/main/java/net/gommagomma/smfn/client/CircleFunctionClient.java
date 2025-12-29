package net.gommagomma.smfn.client;

import java.awt.Color;
import java.util.function.BiFunction;

import javax.swing.JFrame;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer2D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.geometry.Circle;
import net.gommagomma.smfn.math.geometry.Point;

public class CircleFunctionClient {
    public static void main(String[] args) {
        // --- 1. Definizione della funzione matematica
        Circle circleFunction = new Circle(new Point(new Real(0), new Real(0)), new Real(2.5));

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;
        
        SwingRenderer2D renderer = new SwingRenderer2D(width, height);
        JFrame frame = new JFrame("SMFN Circle Set Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy();

        // Definisci l'area matematica (Viewport): [-2.0, 1.0] x [-1.5, 1.5] 
        // L'area classica che contiene l'intero set
        Viewport viewport = new Viewport(-5, 5, -5, 5, width, height);

        // Adattatore Dominio: Combina X e Y in un point 2D
        BiFunction<Double, Double, Point> domainAdapter = (x, y) -> new Point(new Real(x), new Real(y));

        ColorMapper<Real> colorMapper = new ColorMapper<>() {
            @Override
            public Color map(Real value) {
                // value è |z|^2 - R^2
                if (Math.abs(value.getValue()) < 0.01) { // Se vicino a zero (il bordo)
                    return Color.BLUE;
                } else if (value.getValue() < 0) {
                    return Color.BLACK; // Interno
                } else {
                    return Color.BLACK; // Esterno
                }
            }
        };

        renderer.startDrawing();

        FunctionPlotter2D.plotFunction(
            renderer, viewport, circleFunction, domainAdapter, colorMapper
        );

        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);

        renderer.endDrawingAndFlush();
    }
}
