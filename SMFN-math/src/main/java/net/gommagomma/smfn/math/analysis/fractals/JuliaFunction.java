// net.gommagomma.smfn.math.analysis.fractals.JuliaFunction.java
package net.gommagomma.smfn.math.analysis.fractals;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.FixedPointProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;

/**
 * Rappresenta la funzione matematica per un set di Julia specifico (definito da una costante C).
 */
public class JuliaFunction
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

    private final Complex constantC;
    private ConvergenceParameters cachedParams;
    private final ConvergenceCriteria divergenceTest;

    public JuliaFunction(Complex constantC, int maxIterations) {
        this.constantC = constantC;
        this.divergenceTest = (measure, params, iteration) -> measure.getValue() > DIVERGENCE_RADIUS_SQ;
        setMaxIterations(maxIterations);
    }

    public void setMaxIterations(int maxIterations) {
        this.cachedParams = new ConvergenceParameters(R.one(), maxIterations);
    }

    @Override
    public Natural apply(Complex z0) {
        // z_0 varia per ogni pixel, constantC e' fissa
        FixedPointProblem<Complex> problem = current -> C.add(C.multiply(current, current), constantC);
        SolverResult<Complex> result = solver.solve(problem, z0, divergenceTest, cachedParams, complexMetricSpace);
        return N.of(result.getIterationsExecuted());
    }
}
