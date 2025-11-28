
package net.gommagomma.smfn.math.analysis.core;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.structures.MetricSpace;

/**
 * Interfaccia per un risolutore numerico che gestisce sistemi iterativi specializzato per Spazi Metrici.
 * Garantisce formalmente l'uso della distanza e della tolleranza.
 * 
 * @param <T> Il tipo di stato del sistema.
 * @param <R> Il tipo di risultato restituito dal solutore (es. un numero intero, un booleano, un valore finale).
 */
public interface MetricSolver<T extends AlgebraicElement<T>, R>
{
	/**
     * Esegue il processo iterativo.
     * 
     * @param initial Il punto di partenza (stato iniziale).
     * @param system Il sistema che definisce l'evoluzione (z_n+1 = f(z_n)).
     * @param test Il test che definisce lo stop (es. divergenza, precisione raggiunta).
     * @param tolerance La tolleranza di convergenza desiderata.
     * @param space Lo Spazio Metrico in cui si svolge l'iterazione.
     * @return Il risultato R (es. il numero di iterazioni o lo stato finale).
     */
    R solve(T initial, IterativeSystem<T> system, MetricConvergenceTest<T> test, Real tolerance, MetricSpace<T> space);
}
