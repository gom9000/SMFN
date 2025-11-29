package net.gommagomma.smfn.math.analysis.solvers.core;

import net.gommagomma.smfn.math.algebra.numeric.Real;


public class IntegrationParameters
{
    public final Real fixedStepSize; 
    public final ConvergenceParameters convergenceParams;

    public IntegrationParameters(Real fixedStepSize) {
        this.fixedStepSize = fixedStepSize;
        this.convergenceParams = null;
    }

    public IntegrationParameters(ConvergenceParameters convergenceParams) {
        this.fixedStepSize = null;
        this.convergenceParams = convergenceParams;
    }
}
