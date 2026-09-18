package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta un monoide additivo, caratterizzato da un'operazione binaria interna associativa 
 * (addizione) e dall'esistenza di un elemento neutro additivo (lo zero).
 *
 * @param <E> il tipo degli elementi appartenenti alla struttura
 */
public interface AdditiveMonoid<E extends AlgebraicElement<E>>
extends AlgebraicStructure<E>
{
	/**
     * Restituisce l'elemento neutro rispetto all'addizione (lo zero algebrico).
     * 
     * @return l'elemento zero della struttura
     */
	E zero();

	/**
     * Esegue l'operazione di addizione tra due elementi.
     * 
     * @param a il primo addendo
     * @param b il secondo addendo
     * @return la somma tra a e b
     */
	E add(E a, E b);

	/**
     * Verifica se un elemento coincide con l'elemento zero della struttura.
     * 
     * @param e l'elemento da verificare
     * @return true se l'elemento è lo zero, false altrimenti
     */
	default boolean isZero(E e) {
        return areEqual(e, zero());
    }
}
