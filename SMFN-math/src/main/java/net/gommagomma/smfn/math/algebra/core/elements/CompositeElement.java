package net.gommagomma.smfn.math.algebra.core.elements;

import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public interface CompositeElement<K extends ScalarElement<K>, E extends CompositeElement<K, E>> 
extends AlgebraicElement<E>
{
    ScalarStructure<K> getScalarStructure();
}
