package net.gommagomma.smfn.demo;

import java.awt.Color;
import java.util.function.BiFunction;

import javax.swing.JFrame;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer2D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.geometry.Line;
import net.gommagomma.smfn.math.geometry.Point;

public class GeometryLinePlot {
    public static void main(String[] args) {
        // --- 1. Definizione della funzione matematica ---
        Line f = new Line(new Point(0.0, -1), new Real(2.5), new Real(5));

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;

        SwingRenderer2D renderer = new SwingRenderer2D(width, height);
        JFrame frame = new JFrame("SMFN Circle Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy();

        Viewport viewport = new Viewport(-5, 5, -5, 5, width, height);

        BiFunction<Double, Double, Point> domainAdapter = (x, y) -> new Point(new Real(x), new Real(y));

        // value = dist(P,C) - r: bordo in blu, interno/esterno distinti per sfumatura
        ColorMapper<Real> colorMapper = new ColorMapper<>() {
            @Override
            public Color map(Real value) {
                double v = value.getValue();
                if (Math.abs(v) < 0.02) {
                    return Color.CYAN; // bordo
                } else if (v < 0) {
                    return new Color(20, 20, 60); // interno: blu scuro
                } else {
                    return Color.BLACK; // esterno
                }
            }
        };

        renderer.startDrawing();

        FunctionPlotter2D.plotFunction(renderer, viewport, f, domainAdapter, colorMapper);
        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);

        renderer.endDrawingAndFlush();
    }
}
