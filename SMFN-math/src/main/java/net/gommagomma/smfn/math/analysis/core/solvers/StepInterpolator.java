package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;

/**
 * Fornisce un'interpolazione tra due stati consecutivi calcolati da un solutore ODE.
 * T: Lo stato del sistema (es. VectorElement).
 */
public interface StepInterpolator<T extends AlgebraicElement<T>>
{    
    /**
     * Restituisce lo stato del sistema al tempo specificato (tra t_current e t_next).
     * @param time Il tempo intermedio.
     * @return Lo stato T al tempo 'time'.
     */
    T getInterpolatedState(Real time);
}
