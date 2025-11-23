
package net.gommagomma.smfn.math.analysis.core;

/**
 * Interfaccia generica per un risolutore numerico che gestisce sistemi iterativi.
 * 
 * @param <T> Il tipo di stato del sistema.
 * @param <R> Il tipo di risultato restituito dal solutore (es. un numero intero, un booleano, un valore finale).
 */
public interface Solver<T, R>
{
    /**
     * Esegue il processo iterativo.
     * @param initial Il punto di partenza (stato iniziale).
     * @param system Il sistema che definisce l'evoluzione (z_n+1 = f(z_n)).
     * @param test Il test che definisce lo stop (es. divergenza, precisione raggiunta).
     * @param maxIterations Il numero massimo di iterazioni.
     * @return Il risultato R (es. il numero di iterazioni o lo stato finale).
     */
    R solve(T initial, IterativeSystem<T> system, ConvergenceTest<T> test, int maxIterations);
}
