package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta un monoide moltiplicativo, caratterizzato da un'operazione binaria interna 
 * associativa e dall'esistenza di un elemento neutro moltiplicativo (unità).
 *
 * @param <E> il tipo degli elementi appartenenti alla struttura
 */
public interface MultiplicativeMonoid<E extends AlgebraicElement<E>>
extends AlgebraicStructure<E>
{
	/**
     * Restituisce l'elemento neutro rispetto alla moltiplicazione (l'unità).
     * 
     * @return l'elemento uno della struttura
     */
	E one();

	/**
     * Esegue l'operazione di moltiplicazione tra due elementi.
     * 
     * @param a il primo operando
     * @param b il secondo operando
     * @return il prodotto tra a e b
     */
    E multiply(E a, E b);

    /**
     * Verifica se un elemento coincide con l'unità della struttura.
     * 
     * @param e l'elemento da verificare
     * @return true se l'elemento è l'unità, false altrimenti
     */
    default boolean isOne(E e) {
        return areEqual(e, one());
    }
}
