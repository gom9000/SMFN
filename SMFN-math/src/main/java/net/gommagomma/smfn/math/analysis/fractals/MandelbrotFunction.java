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
 * Accetta un punto complesso 'c' e restituisce il numero di iterazioni prima della divergenza come SignedInt.
 */
public class MandelbrotFunction
implements Mapping<Complex, Natural>
{
	private final ComplexField C = ComplexField.INSTANCE;
    private final RealField R = RealField.INSTANCE;
    private final MandelbrotSolver solver = new MandelbrotSolver();

    private int maxIterations;
    private ConvergenceParameters cachedParams;
    private final ConvergenceCriteria divergenceTest;


    public MandelbrotFunction(int maxIterations) {
        this.maxIterations = maxIterations;
        this.divergenceTest = (measure, params, iteration) -> measure.getValue() > 4.0;
        this.cachedParams = new ConvergenceParameters(R.zero(), maxIterations);
    }


    public void setMaxIterations(int maxIterations) {
    	this.maxIterations = maxIterations;
    	this.cachedParams = new ConvergenceParameters(R.zero(), maxIterations);
    }


    @Override
    public Natural apply(Complex c) {
    	FixedPointProblem<Complex> problem = current -> C.add(C.multiply(current, current), c);
        return solver.solve(problem, C.zero(), divergenceTest, cachedParams, null);
    }
}
