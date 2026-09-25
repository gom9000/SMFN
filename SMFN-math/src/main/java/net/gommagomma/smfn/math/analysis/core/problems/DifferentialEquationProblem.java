package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Modellizza un problema di equazione differenziale ordinaria del primo ordine della forma generale:
 * V' = F(t, V), dove V rappresenta lo stato del sistema e t indica la variabile temporale.
 * 
 * Questa interfaccia astrae la dinamica continua del sistema senza specificare le condizioni al contorno
 * o iniziali. Il tipo dello stato V è vincolato a LinearElement, consentendo di modellare equazioni
 * differenziali scalari, vettoriali o matriciali all'interno di uno spazio vettoriale o modulo generico
 * su un campo o anello scalare K.
 *
 * @param <K> Il tipo dello scalare sottostante allo spazio vettoriale dello stato
 * @param <V> Il tipo dello stato del sistema (elemento vettoriale o matriciale)
 */
public interface DifferentialEquationProblem<K extends ScalarElement<K>, V extends LinearElement<V, K>>
extends AnalysisProblem<V>
{
	/**
     * Calcola la derivata temporale dV/dt = F(t, V) per lo stato e l'istante specificati.
     *
     * @param currentState Lo stato corrente del sistema V(t)
     * @param currentTime L'istante di tempo t in cui valutare la dinamica
     * @return La variazione istantanea (derivata) dello stato dV/dt
     */
    V derivative(V currentState, Real currentTime);
}
