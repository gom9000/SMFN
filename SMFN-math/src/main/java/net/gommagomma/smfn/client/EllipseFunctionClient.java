package net.gommagomma.smfn.client;

import java.awt.Color;
import java.util.function.BiFunction;

import javax.swing.JFrame;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer2D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.geometry.Ellipse;
import net.gommagomma.smfn.math.geometry.Point;

public class EllipseFunctionClient {
    public static void main(String[] args) {
        Ellipse ellipseFunction = new Ellipse(new Point(new Real(0.0), new Real(0.0)), new Real(3.0), new Real(1.5)); 

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;
        
        SwingRenderer2D renderer = new SwingRenderer2D(width, height);
        JFrame frame = new JFrame("SMFN Ellipse Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy();

        Viewport viewport = new Viewport(-4.0, 4.0, -3.0, 3.0, width, height); 

        BiFunction<Double, Double, Point> domainAdapter = (x, y) -> new Point(new Real(x), new Real(y));

        ColorMapper<Real> colorMapper = new ColorMapper<>() {
            @Override
            public Color map(Real value) {
                // value è (x^2/a^2) + (y^2/b^2) - 1
                if (Math.abs(value.getValue()) < 0.01) {
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
            renderer, viewport, ellipseFunction::eval, domainAdapter, colorMapper
        );
        
        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);

        renderer.endDrawingAndFlush();
    }
}
