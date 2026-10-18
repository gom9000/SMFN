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
 * Il modulo |p(z)| del polinomio p(z) = z^3 - 1 sul piano complesso, come reticolo (wireframe).
 * <p>
 * Le tre radici cubiche dell'unita' sono i punti in cui la superficie tocca quota zero: tre
 * imbuti, con una sella fra ciascuna coppia, e tutt'intorno un bacino che sale. E' lo stesso
 * polinomio del {@code NewtonFractalPlot}: li' si vede il bacino di attrazione di ciascuna radice
 * come colore, qui si vedono le radici stesse come buchi della superficie.
 * <p>
 * Il polinomio e' una {@code Mapping<Complex, Complex>}; l'altezza e' il suo modulo, scelto dal
 * {@code heightAdapter}. Il reticolo e' trasparente: si usa questo plotter, non le facce piene,
 * perche' la forma si legge dalle linee e non dal colore.
 * <p>
 * Interattivo: si ruota col mouse (vedi {@code SwingWindow.showInteractive}).
 */
public class ComplexPolynomialSurfacePlot
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

        // Piano complesso [-1.1, 1.1]^2: contiene le tre radici (a distanza 1 dall'origine). Al
        // centro |p(0)| = 1 e' un colle; fuori dalle radici la superficie sale come |z|^3 fino a
        // circa 4.8 agli angoli, che e' il limite del riquadro in z.
        Viewport3D viewport = new Viewport3D(-1.1, 1.1, -1.1, 1.1, 0, 4.8, width, height, 20.0, 50.0);

        // --- 3. Adattatori ---
        BiFunction<Double, Double, Complex> domainAdapter = Complex::new;
        Function<Complex, Double> heightAdapter = value -> Math.hypot(value.getRe(), value.getIm());

        // --- 4. Finestra interattiva: si trascina per ruotare, tasto destro per spostare, rotellina per lo zoom ---
        // La superficie si campiona una volta sola; ogni ridisegno cambia solo la proiezione.
        int cells = 36;
        SurfaceGrid<Complex> grid = SurfaceGrid.sample(viewport, function, domainAdapter, heightAdapter, cells, cells);
        SwingUtilities.invokeLater(() -> SwingWindow.showInteractive("SMFN Complex Polynomial Surface: |z^3 - 1|", viewport, (renderer, view) -> {
            SurfacePlotter.plotWireframe(renderer, view, grid, RgbColor.CYAN);
            SurfaceAxisPlotter.plotAxes(renderer, view, RgbColor.WHITE, true);
        }));
    }
}
