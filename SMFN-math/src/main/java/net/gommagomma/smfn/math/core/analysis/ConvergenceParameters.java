package net.gommagomma.smfn.math.core.analysis;

import net.gommagomma.smfn.math.core.algebra.numeric.Real;


public class ConvergenceParameters
{
    public final Real tolerance;
    public final int maxIterations;


    public ConvergenceParameters(Real tolerance, int maxIterations)
    {
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }
}
