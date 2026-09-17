package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Capacit di un elemento di fornire il proprio coniugato. Per i numeri
 * complessi corrisponde alla coniugazione usuale; in generale rappresenta
 * un'involuzione (conjugate(conjugate(e)) == e) usata, ad esempio, per
 * definire prodotti interni hermitiani su spazi costruiti su K.
 */
public interface Conjugable<E extends AlgebraicElement<E>>
{
	E conjugate();
}
