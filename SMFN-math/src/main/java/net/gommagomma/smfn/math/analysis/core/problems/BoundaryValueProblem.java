package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Problema ai Valori al Contorno (BVP): DifferentialEquation + V(t0), V(tf).
 */
public interface BoundaryValueProblem<K extends ScalarElement<K>, V extends LinearElement<V, K>>
extends DifferentialEquationProblem<K, V>
{
    Real getEndTime();
    V getBoundaryConditionAtStart();
    V getBoundaryConditionAtEnd();
}
