package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta la capacità di un elemento di calcolarne la radice quadrata.
 *
 * @param <E> il tipo concreto dell'elemento
 */
public interface Sqrtable<E extends AlgebraicElement<E>>
{
	/**
     * Calcola la radice quadrata dell'elemento.
     * 
     * @return un nuovo elemento che rappresenta la radice quadrata
     */
    E sqrt();
}