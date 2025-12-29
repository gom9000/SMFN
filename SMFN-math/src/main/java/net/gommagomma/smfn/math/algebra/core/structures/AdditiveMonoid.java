package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface AdditiveMonoid<E extends AlgebraicElement<E>>
extends AlgebraicStructure<E>
{
	E zero();
	E add(E a, E b);
	default boolean isZero(E e) {
        return areEqual(e, zero());
    }
}
