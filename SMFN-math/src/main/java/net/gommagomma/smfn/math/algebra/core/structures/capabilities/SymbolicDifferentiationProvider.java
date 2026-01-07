package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface SymbolicDifferentiationProvider<E extends AlgebraicElement<E>>
{
    E derivative(E element);
}
