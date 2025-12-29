package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

public interface CompositeStructure<K extends ScalarElement<K>, E extends AlgebraicElement<E>> 
extends AlgebraicStructure<E>
{
	ScalarStructure<K> getScalarStructure();

	default boolean isExact() {
        return getScalarStructure().isExact();
    }
}
