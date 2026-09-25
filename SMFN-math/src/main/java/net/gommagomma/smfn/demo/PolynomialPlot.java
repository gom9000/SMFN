package net.gommagomma.smfn.demo;

import java.awt.Color;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.Viewport;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer1D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.FunctionPlotter1D;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialElementFactory;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.functionals.HornerEvaluator;
import net.gommagomma.smfn.math.analysis.functions.PolynomialFunction;

public class PolynomialPlot
{
    public static void main(String[] args)
    {
        // --- 1. Definizione del Contesto e del Polinomio P(x) = 1.5x^3 + 3.0x^2 - 5.0x - 2.0 ---
        RealField R = RealField.INSTANCE;
        Real[] coeffs = { new Real(-2.0), new Real(-5.0), new Real(3.0), new Real(1.5) };

        Polynomial<Real> cubicPolynomial = PolynomialElementFactory.of(R, coeffs);
        System.out.println("Polinomio da plottare: P(x) = " + cubicPolynomial);

        // Polynomial e' un valore puro, non una Mapping: per plottarlo serve
        // avvolgerlo in una PolynomialFunction con una strategia di valutazione
        HornerEvaluator<Real, RealField, PolynomialFunction<Real, RealField>> evaluator = new HornerEvaluator<>(R);
        PolynomialFunction<Real, RealField> polynomialFunction = new PolynomialFunction<>(cubicPolynomial, R, evaluator);

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;

        SwingRenderer1D renderer = new SwingRenderer1D(width, height);
        SwingWindow.show(renderer, "SMFN Polynomial Example: P(x) = 1.5x^3 + 3x^2 - 5x - 2");

        Viewport viewport = new Viewport(-3.5, 3.5, -8.5, 8.5, width, height);

        // --- 3. Adattatori double <-> Real ---
        Function<Double, Real> domainAdapter = Real::new;
        Function<Real, Double> codomainAdapter = Real::getValue;

        // --- 4. Rendering ---
        renderer.startDrawing();
        renderer.clear(Color.BLACK);
        CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);

        renderer.setColor(Color.BLUE);
        FunctionPlotter1D.plotFunction(renderer, viewport, polynomialFunction, domainAdapter, codomainAdapter);

        renderer.endDrawingAndFlush();
    }
}
