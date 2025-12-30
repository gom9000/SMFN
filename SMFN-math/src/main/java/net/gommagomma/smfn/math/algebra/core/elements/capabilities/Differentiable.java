package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface Differentiable<E extends AlgebraicElement<E>>
{
    E derive();
}
