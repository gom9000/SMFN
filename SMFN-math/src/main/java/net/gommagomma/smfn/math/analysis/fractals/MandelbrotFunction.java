package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.FixedPointProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;

/**
 * Rappresenta la funzione matematica dell'insieme di Mandelbrot.
 * Funzione complessa che assegna a ciascun punto del piano complesso c il corrispondente tempo di fuga (numero di iterazioni)
 * associato all'insieme di Mandelbrot z_{k+1} = z_k^2 + c a partire dall'origine z_0 = 0.
 */
public class MandelbrotFunction
implements Mapping<Complex, Natural>
{
	private static final double DIVERGENCE_RADIUS_SQ = 4.0;
    private final ComplexField C = ComplexField.INSTANCE;
    private final RealField R = RealField.INSTANCE;
    private final EscapeTimeSolver solver = new EscapeTimeSolver();

    private ConvergenceParameters cachedParams;
    private final ConvergenceCriteria divergenceTest;

    public MandelbrotFunction(int maxIterations) {
        this.divergenceTest = (measure, params, iteration) -> measure.getValue() > DIVERGENCE_RADIUS_SQ;
        setMaxIterations(maxIterations);
    }

    public void setMaxIterations(int maxIterations) {
        this.cachedParams = new ConvergenceParameters(R.one(), maxIterations);
    }

    @Override
    public Natural apply(Complex c) {
        // z_0 e' sempre l'origine 0 + 0i, il punto del piano c varia per ogni chiamata
        FixedPointProblem<Complex> problem = current -> C.add(C.multiply(current, current), c);
        return solver.solve(problem, C.zero(), divergenceTest, cachedParams, null);
    }
}
