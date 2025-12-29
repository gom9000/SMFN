package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface Orderable<E extends AlgebraicElement<E>>
extends Comparable<E>
{
    default boolean isLessThan(E other) {
        return compareTo(other) < 0;
    }

    default boolean isGreaterThan(E other) {
        return compareTo(other) > 0;
    }

    default boolean isLessThanOrEqual(E other) {
        return !isGreaterThan(other);
    }

    default boolean isGreaterThanOrEqual(E other) {
        return !isLessThan(other);
    }
}
