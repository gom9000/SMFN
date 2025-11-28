package net.gommagomma.smfn.math.core.algebra.elements.capabilities;

import net.gommagomma.smfn.math.core.algebra.AlgebraicElement;

public interface ComparableElement<E extends ComparableElement<E>>
extends AlgebraicElement<E>, Comparable<E>
{
    default boolean isLessThan(E other) {
        return compareTo(other) < 0;
    }
    default boolean isGreaterThan(E other) {
        return compareTo(other) > 0;
    }
    double modulus();
}
