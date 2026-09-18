package net.gommagomma.smfn.math.algebra.core.structures.metric;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Rappresenta uno spazio metrico, ovvero un insieme dotato di una funzione di distanza 
 * che soddisfa gli assiomi di non negatività, simmetria e disuguaglianza triangolare.
 *
 * @param <E> il tipo degli elementi appartenenti allo spazio
 */
public interface MetricSpace<E>
{
	/**
     * Calcola la distanza metrica tra due elementi dello spazio.
     * 
     * @param a il primo elemento
     * @param b il secondo elemento
     * @return un'istanza di {@link Real} che rappresenta la distanza tra a e b
     */
    Real distance(E a, E b);
}
