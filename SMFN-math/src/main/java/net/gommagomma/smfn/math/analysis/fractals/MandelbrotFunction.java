package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.FixedPointProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;

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
    private final NaturalSemiring N = NaturalSemiring.INSTANCE;
    private final EscapeTimeSolver solver = new EscapeTimeSolver();

    // Distanza euclidea su Complex: d(z1, z2) = |z1 - z2|. Serve al solutore per riportare
    // la distanza dell'ultimo passo nel SolverResult (non guida il criterio di arresto).
    private final MetricSpace<Complex> complexMetricSpace = (z1, z2) -> R.of(Math.sqrt(C.subtract(z1, z2).modulusSquared()));

    private StoppingParameters cachedParams;
    private final StoppingCriteria divergenceTest;

    public MandelbrotFunction(int maxIterations) {
        this.divergenceTest = (measure, params, iteration) -> measure.getValue() > DIVERGENCE_RADIUS_SQ;
        setMaxIterations(maxIterations);
    }

    public void setMaxIterations(int maxIterations) {
        this.cachedParams = new StoppingParameters(R.one(), maxIterations);
    }

    @Override
    public Natural apply(Complex c) {
        // z_0 e' sempre l'origine 0 + 0i, il punto del piano c varia per ogni chiamata
        FixedPointProblem<Complex> problem = current -> C.add(C.multiply(current, current), c);
        SolverResult<Complex> result = solver.solve(problem, C.zero(), divergenceTest, cachedParams, complexMetricSpace);
        return N.of(result.getIterationsExecuted());
    }
}
