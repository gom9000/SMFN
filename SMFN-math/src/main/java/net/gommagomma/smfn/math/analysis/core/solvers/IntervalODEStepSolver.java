package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentialEquationProblem;

/**
 * Contratto per solutori di equazioni differenziali ordinarie (ODE) basati su integrazione a passi discreti.
 * 
 * Questa interfaccia definisce la primitiva fondamentale per gli algoritmi di integrazione temporale
 * monostep o multistep (es. Eulero, Runge-Kutta). Esprime la capacita' di avanzare lo stato del sistema V
 * da un istante t ad un istante t + dt calcolando il singolo incremento discreto.
 * 
 * Operando su uno spazio algebrico di tipo Module, il metodo di passo sfrutta le operazioni
 * di combinazione lineare (somma tra elementi di stato e prodotto per uno scalare) fornite dal modulo
 * per aggiornare lo stato senza vincolare l'algoritmo ad una specifica rappresentazione vettoriale o matriciale.
 *
 * @param <K> Il tipo dello scalare appartenente all'anello sottostante
 * @param <V> Il tipo dello stato del sistema (elemento lineare vettoriale o matriciale)
 * @param <S> La struttura algebrica di anello e struttura scalare associata a K
 */
public interface IntervalODEStepSolver<K extends ScalarElement<K>, V extends LinearElement<V, K>, S extends Ring<K> & ScalarStructure<K>>
extends IntervalSolver<K, V, S>
{
	/**
     * Calcola lo stato successivo V(t + dt) eseguendo un singolo passo di integrazione numerica.
     *
     * @param system Il problema differenziale che definisce la dinamica dV/dt = F(t, V)
     * @param currentState Lo stato attuale del sistema V(t) all'inizio del passo
     * @param currentTime L'istante di tempo corrente t
     * @param deltaTime L'ampiezza dell'intervallo di integrazione dt (passo temporale)
     * @param space Il modulo algebrico che fornisce le operazioni di spazio vettoriale/modulo su V e K
     * @return Il nuovo stato del sistema V(t + dt) calcolato al termine del passo
     */
    V step(DifferentialEquationProblem<K, V> system, V currentState, Real currentTime, Real deltaTime, Module<V, K, S> space);
}
