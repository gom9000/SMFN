package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

public interface Differentiable<T extends AlgebraicElement<T>>
{
    T derivative();
}
