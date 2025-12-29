package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface MultiplicativeGroup<E extends AlgebraicElement<E>>
extends CommutativeMultiplicativeMonoid<E>
{
	E inverse(E e);

	default E divide(E a, E b) {
        return multiply(a, inverse(b));
    }
}
