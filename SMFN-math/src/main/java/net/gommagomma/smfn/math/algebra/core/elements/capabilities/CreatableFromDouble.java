package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;


public interface CreatableFromDouble<E extends AlgebraicElement<E>>
{
    E valueOf(double value);
}
