package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface AdditiveGroup<E extends AlgebraicElement<E>>
extends AdditiveMonoid<E>
{
	E negate(E e);

	default E subtract(E a, E b) {
        return add(a, negate(b));
    }
}
