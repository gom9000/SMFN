package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

public interface Normable<K extends ScalarElement<K>>
{
    K norm();
}