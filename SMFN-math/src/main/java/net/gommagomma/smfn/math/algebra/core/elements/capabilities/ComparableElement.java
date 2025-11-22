package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

public interface ComparableElement<E extends ComparableElement<E>>
extends AlgebraicElement<E>, Comparable<E>
{
    default boolean isLessThan(E other) {
        return compareTo(other) < 0;
    }
    default boolean isGreaterThan(E other) {
        return compareTo(other) > 0;
    }
}
