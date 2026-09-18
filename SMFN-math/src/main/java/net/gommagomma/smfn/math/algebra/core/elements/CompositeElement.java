package net.gommagomma.smfn.math.algebra.core.elements;

import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Rappresenta un elemento composto (strutturato), costituito da una collezione di scalari 
 * organizzati in una forma multidimensionale (come vettori, matrici o tensori).
 *
 * @param <K> il tipo degli elementi scalari costitutivi
 * @param <E> il tipo concreto dell'elemento composto
 */
public interface CompositeElement<K extends ScalarElement<K>, E extends CompositeElement<K, E>> 
extends AlgebraicElement<E>
{
	/**
     * Restituisce la struttura scalare associata agli elementi che compongono questa struttura.
     * 
     * @return la struttura scalare di supporto
     */
    ScalarStructure<K> getScalarStructure();
}
