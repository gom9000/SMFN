package net.gommagomma.smfn.math.algebra.core.elements.multiplicative;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

public interface MultiplicativeMonoidElement<E extends MultiplicativeMonoidElement<E>>
extends AlgebraicElement<E>
{
	E multiply(E other);
	E getOne();
	default boolean isOne() { return isMathematicallyEqualTo(getOne()); }
}
