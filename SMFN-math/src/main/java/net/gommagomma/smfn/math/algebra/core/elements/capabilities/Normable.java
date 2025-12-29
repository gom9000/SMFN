package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

public interface Normable<N extends ScalarElement<N>>
{
    N norm();
}