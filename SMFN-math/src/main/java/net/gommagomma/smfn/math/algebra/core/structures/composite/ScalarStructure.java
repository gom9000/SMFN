package net.gommagomma.smfn.math.algebra.core.structures.composite;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Rappresenta una struttura algebrica scalare (che estende un semianello), 
 * fornendo operazioni e proprietà specifiche per i singoli valori scalari.
 *
 * @param <K> il tipo degli elementi scalari
 */
public interface ScalarStructure<K extends ScalarElement<K>> 
extends Semiring<K>
{
	/**
     * Calcola la magnitudine o valore reale associato a un elemento scalare.
     * 
     * @param element l'elemento scalare
     * @return un'istanza di Real che rappresenta la magnitudine
     */
	Real magnitude(K element);

	/**
     * Indica se questa struttura scalare � basata su un'aritmetica esatta 
     * oppure approssimata.
     * 
     * @return true se la struttura � esatta, false altrimenti
     */
	boolean isExact(); 

	/**
	 * Indica se un elemento e' trascurabile rispetto a una scala di riferimento.
	 *
	 * @param element l'elemento da valutare
	 * @param scale l'ordine di grandezza di riferimento, non negativo
	 */
	default boolean isNegligible(K element, Real scale) {
		return isZero(element);
	}

	/**
	 * Come areEqual(a, b), ma con la tolleranza proporzionale a una scala esplicita.
	 *
	 * @param scale l'ordine di grandezza di riferimento, non negativo
	 */
	default boolean areEqual(K a, K b, Real scale) {
		return areEqual(a, b);
	}
}
