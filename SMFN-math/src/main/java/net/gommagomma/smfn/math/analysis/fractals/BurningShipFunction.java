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
 * Funzione complessa che assegna a ciascun punto del piano c il tempo di fuga
 * associato alla mappa non analitica del Burning Ship:
 * <pre>
 * z_{k+1} = (|Re(z_k)| + i * |Im(z_k)|)^2 + c
 * </pre>
 * a partire dall'origine z_0 = 0.
 */
public class BurningShipFunction
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

    public BurningShipFunction(int maxIterations) {
        this.divergenceTest = (measure, params, iteration) -> measure.getValue() > DIVERGENCE_RADIUS_SQ;
        setMaxIterations(maxIterations);
    }

    public void setMaxIterations(int maxIterations) {
        this.cachedParams = new StoppingParameters(R.one(), maxIterations);
    }

    @Override
    public Natural apply(Complex c) {
        FixedPointProblem<Complex> problem = current -> {
            // Applica il valore assoluto ai componenti reale ed immaginario
            double absRe = Math.abs(current.getRe());
            double absIm = Math.abs(current.getIm());
            Complex absZ = C.of(absRe, absIm);

            // z_{k+1} = absZ^2 + c
            return C.add(C.multiply(absZ, absZ), c);
        };

        SolverResult<Complex> result = solver.solve(problem, C.zero(), divergenceTest, cachedParams, complexMetricSpace);
        return N.of(result.getIterationsExecuted());
    }
}