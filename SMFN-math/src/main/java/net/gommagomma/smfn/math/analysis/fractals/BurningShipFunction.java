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
    private final EscapeTimeSolver solver = new EscapeTimeSolver();

    private ConvergenceParameters cachedParams;
    private final ConvergenceCriteria divergenceTest;

    public BurningShipFunction(int maxIterations) {
        this.divergenceTest = (measure, params, iteration) -> measure.getValue() > DIVERGENCE_RADIUS_SQ;
        setMaxIterations(maxIterations);
    }

    public void setMaxIterations(int maxIterations) {
        this.cachedParams = new ConvergenceParameters(R.one(), maxIterations);
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

        return solver.solve(problem, C.zero(), divergenceTest, cachedParams, null);
    }
}