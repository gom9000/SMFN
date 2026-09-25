package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta la capacità di un elemento di essere elevato a una potenza intera.
 *
 * @param <E> il tipo concreto dell'elemento
 */
public interface Exponentiable<E extends AlgebraicElement<E>>
{
	/**
     * Eleva l'elemento alla potenza intera specificata.
     * 
     * @param exponent l'esponente intero
     * @return il risultato dell'elevamento a potenza
     */
    E power(int exponent);
}
