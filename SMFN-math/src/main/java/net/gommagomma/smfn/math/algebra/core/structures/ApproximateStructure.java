package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.ApproximateElement;

public interface ApproximateStructure<E extends ApproximateElement<E>>
extends ScalarStructure<E>
{
    @Override
    default boolean isExact() { return false; }

    double epsilon();
}
