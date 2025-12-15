package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numeric.Real;

/**
 * Parametri che definiscono le condizioni di avanzamento per un IntervalSolver.
 */
public class IntegrationParameters
{
    // Per Solutori a Passo Fisso (e guess iniziale per adattivi)
    public final Real fixedStepSize; 
    
    // Per Solutori a Passo Variabile (Embedded)
    public final Real tolerance;   // La tolleranza di errore locale richiesta (tau)
    public final Real maxStepSize; // Il passo massimo consentito (h_max)
    public final Real minStepSize; // Il passo minimo consentito (h_min)

    // Costruttore per Passo Fisso
    public IntegrationParameters(Real fixedStepSize) {
        this(fixedStepSize, null, null, null);
    }
    
    // Costruttore Completo
    public IntegrationParameters(Real fixedStepSize, Real tolerance, Real maxStepSize, Real minStepSize) {
        this.fixedStepSize = fixedStepSize;
        this.tolerance = tolerance;
        this.maxStepSize = maxStepSize;
        this.minStepSize = minStepSize;
    }
}
