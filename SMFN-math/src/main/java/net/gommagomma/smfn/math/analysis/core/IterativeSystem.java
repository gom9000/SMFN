
package net.gommagomma.smfn.math.analysis.core;

/**
 * Rappresenta un sistema che evolve iterativamente.
 * Utile per metodi numerici o sistemi dinamici.
 * 
 * @param <T> Il tipo di stato del sistema (es. Complex, Double, Vector).
 */
public interface IterativeSystem<T>
{
    /**
     * Calcola lo stato successivo del sistema dato lo stato corrente.
     * @param current Lo stato corrente del sistema.
     * @return Lo stato calcolato per l'iterazione successiva.
     */
    T nextIteration(T current);
}
