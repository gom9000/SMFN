package net.gommagomma.smfn.math.algebra.core.structures;


import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;


public interface MultiplicativeMonoid<E extends AlgebraicElement<E>>
extends AlgebraicStructure<E>
{
	E one();
    E multiply(E a, E b);
    default boolean isOne(E e) {
        return areEqual(e, one());
    }
}
