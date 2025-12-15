package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numeric.Real;

/**
 * Parametri che definiscono le condizioni di arresto per un IterativeSolver.
 */
public final class ConvergenceParameters
{
    public final Real tolerance;
    public final int maxIterations;


    public ConvergenceParameters(Real tolerance, int maxIterations)
    {
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
