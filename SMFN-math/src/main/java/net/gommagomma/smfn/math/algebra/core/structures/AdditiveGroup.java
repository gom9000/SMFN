package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta un gruppo additivo, estendendo il monoide additivo richiedendo 
 * che ogni elemento ammetta un opposto (inverso additivo).
 *
 * @param <E> il tipo degli elementi appartenenti alla struttura
 */
public interface AdditiveGroup<E extends AlgebraicElement<E>>
extends AdditiveMonoid<E>
{
	/**
     * Calcola l'opposto (inverso additivo) di un elemento.
     * 
     * @param e l'elemento di cui calcolare l'opposto
     */
	E negate(E e);

	/**
     * Esegue la sottrazione tra due elementi sfruttando l'inverso additivo.
     * 
     * @param a il minuendo
     * @param b il sottraendo
     * @return il risultato della sottrazione
     */
	default E subtract(E a, E b) {
        return add(a, negate(b));
    }
}
