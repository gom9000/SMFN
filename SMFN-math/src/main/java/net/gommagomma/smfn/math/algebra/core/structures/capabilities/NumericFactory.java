package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

public interface NumericFactory<K extends ScalarElement<K>>
{
    K zero();
    K one();

    K of(double value);
    K of(long value);
    K of(int value);
}
