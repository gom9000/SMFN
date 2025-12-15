package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;

/**
 * Definisce la dinamica di un sistema, ovvero la derivata V' = F(t, V).
 * K: Tipo dello scalare (es. Real per il tempo e i coefficienti).
 * V: Tipo dello stato (es. VectorElement).
 */
public interface DifferentialEquationProblem<K extends FieldElement<K, ?>, V extends VectorElement<K, V>> 
extends AnalysisProblem<V>
{
	/**
     * Calcola la derivata dy/dt = f(t, y) dato lo stato corrente e il tempo corrente.
     * @param currentState Lo stato attuale del sistema, y.
     * @param currentTime Il tempo attuale, t.
     * @return Il tasso di variazione dello stato, dy/dt.
     */
    V derivative(V currentState, Real currentTime);
}
