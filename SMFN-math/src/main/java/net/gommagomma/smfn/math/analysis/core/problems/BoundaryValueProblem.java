package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;

/**
 * Definisce un Problema ai Valori al Contorno (BVP): DifferentialEquation + condizioni V(t0) e V(tf).
 */
public interface BoundaryValueProblem<K extends FieldElement<K>, V extends VectorElement<K, V>> 
extends DifferentialEquationProblem<K, V>
{
    K getEndTime();
    V getBoundaryConditionAtStart();
    V getBoundaryConditionAtEnd();
}
