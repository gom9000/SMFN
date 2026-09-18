package net.gommagomma.smfn.demo;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.function.Function;

import javax.swing.JFrame;

import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer1D;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter1D;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.functions.LinearFunction;

public class RealLinearPlot
{
    public static void main(String[] args)
    {
        // --- 1. Definizione della funzione matematica (y = mx + q) ---
        RealField R = RealField.INSTANCE;
        LinearFunction<Real> linearFunction1 = new LinearFunction<>(R, new Real(0.5), new Real(0.0));
        LinearFunction<Real> linearFunction2 = new LinearFunction<>(R, new Real(2.0), new Real(5.0));
        Mapping<Real, Real> linearFunction = linearFunction1.compose(linearFunction2);

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;

        SwingRenderer1D renderer = new SwingRenderer1D(width, height);

        JFrame frame = new JFrame("SMFN Linear Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.pack();
        frame.setVisible(true);
        renderer.initBufferStrategy();

        Viewport viewport = new Viewport(-10.0, 10.0, -10.0, 20.0, width, height);

        // --- 3. Adattatori double <-> Real ---
        Function<Double, Real> domainAdapter = Real::new;
        Function<Real, Double> codomainAdapter = Real::getValue;

        // --- 4. Rendering ---
        renderer.startDrawing();
        renderer.clear(Color.BLACK);
        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);

        renderer.setColor(Color.BLUE);
        FunctionPlotter1D.plotFunction(renderer, viewport, linearFunction, domainAdapter, codomainAdapter);
        renderer.setColor(Color.GREEN);
        FunctionPlotter1D.plotFunction(renderer, viewport, linearFunction1, domainAdapter, codomainAdapter);
        renderer.setColor(Color.RED);
        FunctionPlotter1D.plotFunction(renderer, viewport, linearFunction2, domainAdapter, codomainAdapter);

        double rangeX = viewport.maxX - viewport.minX;
        double rangeY = viewport.maxY - viewport.minY;

        DecimalFormat DF = new DecimalFormat("0.000E0");
        String infoText1 = "Range X: [" + DF.format(viewport.minX) + ", " + DF.format(viewport.maxX) + "] (Ampiezza: " + DF.format(rangeX) + ")";
        String infoText2 = "Range Y: [" + DF.format(viewport.minY) + ", " + DF.format(viewport.maxY) + "] (Ampiezza: " + DF.format(rangeY) + ")";
        String infoText3 = "Zoom: " + DF.format(3.0 / rangeX) + "x";

        renderer.drawOverlayText(infoText1, 10, 20, Color.RED);
        renderer.drawOverlayText(infoText2, 10, 35, Color.RED);
        renderer.drawOverlayText(infoText3, 10, 50, Color.RED);

        renderer.endDrawingAndFlush();
    }
}
