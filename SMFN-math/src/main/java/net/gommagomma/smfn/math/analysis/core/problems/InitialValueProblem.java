package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;

/**
 * Definisce un Problema ai Valori Iniziali (IVP): DifferentialEquation + condizione iniziale V(t0) = V0.
 * K: Tipo dello scalare.
 * V: Tipo dello stato.
 */
public interface InitialValueProblem<K extends FieldElement<K>, V extends VectorElement<K, V>> 
extends DifferentialEquationProblem<K, V>
{
    /**
     * Lo stato V0 al tempo iniziale t0.
     * @return Lo stato iniziale.
     */
    V getInitialState();

    /**
     * Il tempo iniziale t0.
     * @return Il tempo di partenza.
     */
    Real getStartTime();
}
