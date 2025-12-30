package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

public interface Integrable<T extends AlgebraicElement<T>, K extends ScalarElement<K>>
{
    T integrate(K c);
}
