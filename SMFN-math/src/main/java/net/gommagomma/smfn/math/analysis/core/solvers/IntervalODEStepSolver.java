package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentialEquationProblem;

public interface IntervalODEStepSolver<K extends ScalarElement<K>, V extends LinearElement<V, K>, S extends Ring<K> & ScalarStructure<K>>
extends IntervalSolver<K, V, S>
{
    /**
     * Esegue un singolo passo di integrazione temporale.
     * @param system Il problema ODE (fornisce la derivata dy/dt).
     * @param currentState Lo stato y(t) corrente.
     * @param currentTime Il tempo t corrente.
     * @param deltaTime La dimensione del passo h.
     * @param space La struttura che sa sommare e scalare gli stati V.
     * @return Lo stato y(t + h) approssimato.
     */
    V step(DifferentialEquationProblem<K, V> system, V currentState, Real currentTime, Real deltaTime, Module<V, K, S> space);
}
