package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Parametri che definiscono le condizioni di arresto per un IterativeSolver.
 */
public final class ConvergenceParameters
{
    public final Real tolerance;
    public final int maxIterations;


    public ConvergenceParameters(Real tolerance, int maxIterations)
    {
    	if (Double.isNaN(tolerance.getValue()) || Double.isInfinite(tolerance.getValue())) {
            throw new IllegalArgumentException("Tolerance must be a finite, non-NaN value.");
        }
    	if (tolerance.compareTo(RealField.INSTANCE.zero()) <= 0) {
    	    throw new IllegalArgumentException("Tolerance must be positive.");
    	}
    	if (maxIterations <= 0) {
    	    throw new IllegalArgumentException("Max iterations must be positive.");
    	}
 
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }

	public Real getTolerance() {
		return tolerance;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

}
