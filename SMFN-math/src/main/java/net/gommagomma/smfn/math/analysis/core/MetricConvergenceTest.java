
package net.gommagomma.smfn.math.analysis.core;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.structures.MetricSpace;

/**
 * Definisce i criteri per testare la convergenza di un processo iterativo all'interno di uno Spazio Metrico.
 * Il test si basa sulla distanza (metrica) tra iterazioni successive.
 * 
 * Il tipo T deve appartenere a uno Spazio Metrico.
 */
public interface MetricConvergenceTest<T extends AlgebraicElement<T>>
{
    /**
     * Verifica se il processo iterativo è convergente.
     * 
     * @param current L'iterazione corrente.
     * @param previous L'iterazione precedente (può essere null per la prima iterazione).
     * @param tolerance La tolleranza (epsilon) desiderata per il test.
     * @param iteration Il numero di iterazioni correnti effettuate.
     * @param space Lo Spazio Metrico a cui appartengono current e previous.
     * @return true se la convergenza è raggiunta o se le iterazioni massime sono state superate.
     */
    boolean isConverged(T current, T previous, Real tolerance, int iteration, MetricSpace<T> space);


    /**
     * Restituisce il numero massimo di iterazioni consentite.
     * @return Il limite intero.
     */
    int getMaxIterations();
}
