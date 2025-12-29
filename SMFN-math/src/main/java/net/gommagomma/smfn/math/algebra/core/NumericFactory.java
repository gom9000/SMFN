package net.gommagomma.smfn.math.algebra.core;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface NumericFactory<E extends AlgebraicElement<E>>
{
    E zero();
    E one();

    E of(double value);
    E of(long value);
    E of(int value);
}
