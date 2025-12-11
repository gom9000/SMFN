package net.gommagomma.smfn.math.algebra.core.structures;


import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;


public interface Semiring<E extends SemiringElement<E>>
extends AdditiveMonoid<E>, MultiplicativeMonoid<E>, NumericFactory<E>
{
	@Override
    default E zero() { return additiveIdentity(); }

	@Override
    default E one() { return multiplicativeIdentity(); }
}
