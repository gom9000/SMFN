package net.gommagomma.smfn.demo.functions;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingRenderer;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.CartesianAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.LevelCurvePlotter;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialElementFactory;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.functionals.HornerEvaluator;
import net.gommagomma.smfn.math.analysis.functions.PolynomialFunction;

/**
 * Le curve di livello di |p(z)|, con p(z) = z^3 - 1, sul piano complesso, viste dall'alto.
 * <p>
 * E' la versione piana di {@code ComplexPolynomialSurfaceLevelCurvesPlot}: le linee a |p(z)| costante
 * come su una mappa topografica. Le tre radici (a distanza 1 dall'origine) sono il centro delle
 * curve chiuse piu' piccole, e le curve a forma di otto fra una coppia di radici passano per la
 * sella che le separa.
 */
public class ComplexPolynomialLevelCurvesPlot
{
    public static void main(String[] args)
    {
        // --- 1. Definizione della funzione matematica: p(z) = z^3 - 1 ---
        ComplexField C = ComplexField.INSTANCE;
        Polynomial<Complex> cubic = PolynomialElementFactory.of(C,
            new Complex(-1.0), new Complex(0.0), new Complex(0.0), new Complex(1.0)); // termine noto per primo
        System.out.println("Polinomio da plottare: p(z) = " + cubic);

        HornerEvaluator<Complex, ComplexField, PolynomialFunction<Complex, ComplexField>> evaluator = new HornerEvaluator<>(C);
        PolynomialFunction<Complex, ComplexField> function = new PolynomialFunction<>(cubic, C, evaluator);

        // --- 2. Setup del contesto grafico ---
        int width = 800;
        int height = 600;

        SwingRenderer renderer = new SwingRenderer(width, height);
        SwingWindow.show(renderer, "SMFN Level Curves: |z^3 - 1|");

        Viewport2D viewport = new Viewport2D(-1.6, 1.6, -1.2, 1.2, width, height);

        // --- 3. Adattatori ---
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;
        Function<Complex, Double> heightAdapter = value -> Math.hypot(value.getRe(), value.getIm());

        // --- 4. Rendering ---
        int cells = 400;
        double[] levels = LevelCurvePlotter.evenLevels(0.0, 4.8, 24);
        renderer.startDrawing();
        renderer.clear(RgbColor.BLACK);
        CartesianAxisPlotter.plotAxes(renderer, viewport, RgbColor.DARK_GRAY, true);
        LevelCurvePlotter.plotLevelCurves(renderer, viewport, function, domainAdapter, heightAdapter, levels,
                                    level -> RgbColor.ofHsb((float) (0.65 * (1.0 - level / 4.8)), 1.0f, 1.0f), cells, cells);
        renderer.endDrawingAndFlush();
    }
}
