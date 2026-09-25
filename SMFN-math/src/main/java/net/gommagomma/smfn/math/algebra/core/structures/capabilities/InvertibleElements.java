package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta la capacità di una struttura di gestire elementi che ammettono un inverso 
 * rispetto a un'operazione, fornendo i metodi per calcolarlo e verificarne l'invertibilità.
 *
 * @param <E> il tipo degli elementi
 */
public interface InvertibleElements<E extends AlgebraicElement<E>>
{
	/**
     * Calcola l'inverso dell'elemento specificato.
     * 
     * @param e l'elemento da invertire
     * @return l'elemento inverso
     */
	E inverse(E e);

	/**
     * Verifica se l'elemento specificato è invertibile all'interno della struttura.
     * 
     * @param e l'elemento da verificare
     * @return true se l'elemento � invertibile, false altrimenti
     */
	boolean isInvertible(E e);
}
