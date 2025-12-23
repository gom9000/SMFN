package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentialEquationProblem;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;


public interface IntervalODEStepSolver<K extends FieldElement<K>, T extends VectorElement<K, T>>
extends IntervalSolver<K, T>
{
	/**
     * Esegue un singolo passo di integrazione temporale utilizzando l'algoritmo specifico.
     *  @param system Il problema ODE (fornisce la derivata dy/dt).
     * @param currentState Lo stato y(t) corrente.
     * @param currentTime Il tempo t corrente.
     * @param deltaTime La dimensione del passo h.
     * @return Lo stato y(t + h) approssimato.
     */
	T step(DifferentialEquationProblem<K, T> system, T currentState, Real currentTime, Real deltaTime);
}
