package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Problema ai Valori Iniziali (IVP): DifferentialEquation + V(t0) = V0.
 */
public interface InitialValueProblem<K extends ScalarElement<K>, V extends LinearElement<V, K>>
extends DifferentialEquationProblem<K, V>
{
    V getInitialState();
    Real getStartTime();
}
