package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Modellizza un problema di Cauchy o ai valori iniziali IVP (Initial Value Problem) per equazioni
 * differenziali della forma V' = F(t, V) con condizione iniziale prescritta V(t_0) = V_0.
 *
 * @param <K> Il tipo dello scalare sottostante allo spazio dello stato
 * @param <V> Il tipo dello stato del sistema (elemento lineare vettoriale o matriciale)
 */
public interface InitialValueProblem<K extends ScalarElement<K>, V extends LinearElement<V, K>>
extends DifferentialEquationProblem<K, V>
{
	/**
     * Restituisce lo stato iniziale del sistema V_0 = V(t_0) prescritto all'istante di partenza.
     *
     * @return Lo stato iniziale del sistema
     */
    V getInitialState();

    /**
     * Restituisce l'istante temporale iniziale t_0 in cui è definito lo stato iniziale V_0.
     *
     * @return L'istante di tempo iniziale t_0
     */
    Real getStartTime();
}
