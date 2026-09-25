package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;


/**
 * Modellizza un problema ai limiti BVP (Boundary Value Problem) per equazioni differenziali
 * della forma V' = F(t, V) definite su un intervallo temporale chiuso [t_0, t_f]: V(t0), V(tf).
 *
 * @param <K> Il tipo dello scalare sottostante allo spazio dello stato
 * @param <V> Il tipo dello stato del sistema (elemento lineare vettoriale o matriciale)
 */
public interface BoundaryValueProblem<K extends ScalarElement<K>, V extends LinearElement<V, K>>
extends DifferentialEquationProblem<K, V>
{
	/**
     * Restituisce l'istante di tempo finale t_f che definisce il limite superiore
     * dell'intervallo di integrazione [t_0, t_f].
     *
     * @return L'istante temporale finale t_f
     */
    Real getEndTime();

    /**
     * Restituisce la condizione al contorno prescritta per lo stato del sistema
     * all'estremo iniziale dell'intervallo V(t_0).
     *
     * @return Lo stato vincolato al tempo iniziale t_0
     */
    V getBoundaryConditionAtStart();

    /**
     * Restituisce la condizione al contorno prescritta per lo stato del sistema
     * all'estremo finale dell'intervallo V(t_f).
     *
     * @return Lo stato vincolato al tempo finale t_f
     */
    V getBoundaryConditionAtEnd();
}
