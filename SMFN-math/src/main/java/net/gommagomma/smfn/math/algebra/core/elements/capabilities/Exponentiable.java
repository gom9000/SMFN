package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface Exponentiable<E extends AlgebraicElement<E>>
{
    E power(int exponent);
}
