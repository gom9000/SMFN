package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

public interface SymbolicIntegrationProvider<E extends AlgebraicElement<E>, K extends ScalarElement<K>>
{
    E integrate(E element, K constant);
}
