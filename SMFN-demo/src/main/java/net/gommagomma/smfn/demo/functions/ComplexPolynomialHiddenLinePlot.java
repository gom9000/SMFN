package net.gommagomma.smfn.demo.functions;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.SwingUtilities;

import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.graphics.drivers.swing.SwingWindow;
import net.gommagomma.smfn.graphics.plotting.SurfaceAxisPlotter;
import net.gommagomma.smfn.graphics.plotting.SurfaceGrid;
import net.gommagomma.smfn.graphics.plotting.SurfacePlotter;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialElementFactory;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.functionals.HornerEvaluator;
import net.gommagomma.smfn.math.analysis.functions.PolynomialFunction;

/**
 * Il modulo |p(z)| di p(z) = z^3 - 1 come reticolo con le linee nascoste.
 * <p>
 * Alternativa a {@code ComplexPolynomialSurfacePlot}: li' il reticolo e' trasparente e si vedono
 * anche le linee dietro i rilievi; qui ogni cella copre quelle piu' lontane, e la superficie si
 * legge come un solido.
 * <p>
 * Interattivo: si ruota col mouse (vedi {@code SwingWindow.showInteractive}).
 */
public class ComplexPolynomialHiddenLinePlot
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

        // --- 2. Punto di vista iniziale ---
        int width = 800;
        int height = 600;

        Viewport3D viewport = new Viewport3D(-1.1, 1.1, -1.1, 1.1, 0, 4.8, width, height, 20.0, 50.0);

        // --- 3. Adattatori ---
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;
        Function<Complex, Double> heightAdapter = value -> Math.hypot(value.getRe(), value.getIm());

        // --- 4. Finestra interattiva: si trascina per ruotare, tasto destro per spostare, rotellina per lo zoom ---
        // La superficie si campiona una volta sola; ogni ridisegno cambia solo la proiezione.
        int cells = 36;
        SurfaceGrid<Complex> grid = SurfaceGrid.sample(viewport, function, domainAdapter, heightAdapter, cells, cells);
        SwingUtilities.invokeLater(() -> SwingWindow.showInteractive("SMFN Hidden-line Wireframe: |z^3 - 1|", viewport, (renderer, view) -> {
            SurfacePlotter.plotHiddenLineWireframe(renderer, view, grid, RgbColor.CYAN, RgbColor.BLACK);
            SurfaceAxisPlotter.plotAxes(renderer, view, RgbColor.WHITE, true);
        }));
    }
}
