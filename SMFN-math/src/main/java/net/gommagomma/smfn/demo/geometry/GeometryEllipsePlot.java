package net.gommagomma.smfn.demo.geometry;

import java.awt.Color;
import java.util.function.BiFunction;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer2D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter2D;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.geometry.Ellipse;
import net.gommagomma.smfn.math.geometry.Point;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

public class GeometryEllipsePlot {
    public static void main(String[] args) {
        Ellipse ellipseFunction = new Ellipse(new Point(0.0, 0.0), new Real(3.0), new Real(1.5));

        int width = 800;
        int height = 600;

        SwingRenderer2D renderer = new SwingRenderer2D(width, height);
        SwingWindow.show(renderer, "SMFN Ellipse Plot");

        Viewport viewport = new Viewport(-4.0, 4.0, -3.0, 3.0, width, height);

        VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(RealField.INSTANCE, 2);
        BiFunction<Double, Double, Vector<Real>> domainAdapter = (x, y) -> V2.of(new Real[] { new Real(x), new Real(y) });

        // value = (x/a)^2 + (y/b)^2 - 1: bordo in blu, interno/esterno distinti per sfumatura
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

        FunctionPlotter2D.plotFunction(renderer, viewport, ellipseFunction, domainAdapter, colorMapper);
        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);

        renderer.endDrawingAndFlush();
    }
}
