package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Capacità di un elemento di fornire il proprio coniugato.
 *
 * @param <E> il tipo concreto dell'elemento
 */
public interface Conjugable<E extends AlgebraicElement<E>>
{
	E conjugate();
}
