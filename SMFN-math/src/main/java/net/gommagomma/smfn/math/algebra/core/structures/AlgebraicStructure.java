package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface AlgebraicStructure<E extends AlgebraicElement<E>>
{
    String getName();
    boolean contains(E e);
    boolean areEqual(E a, E b);
}
