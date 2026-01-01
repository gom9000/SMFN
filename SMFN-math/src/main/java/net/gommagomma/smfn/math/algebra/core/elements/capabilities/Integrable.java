package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

public interface Integrable<E extends AlgebraicElement<E>, K extends ScalarElement<K>>
{
    E integrate(K c);
}
