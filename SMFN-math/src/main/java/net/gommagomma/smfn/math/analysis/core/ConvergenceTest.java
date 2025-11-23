
package net.gommagomma.smfn.math.analysis.core;

/**
 * Definisce un criterio per verificare la convergenza o la divergenza di un sistema iterativo.
 * 
 * @param <T> Il tipo di stato del sistema.
 */
public interface ConvergenceTest<T>
{
    /**
     * Esegue il test sullo stato corrente del sistema.
     * @param current Lo stato corrente.
     * @return true se il criterio di stop (convergenza o divergenza) è soddisfatto, false altrimenti.
     */
    boolean isConverged(T current);
}
