package net.gommagomma.smfn.math.algebra.core.structures.composite;

import net.gommagomma.smfn.math.algebra.core.elements.CompositeElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.AlgebraicStructure;

public interface CompositeStructure<K extends ScalarElement<K>, E extends CompositeElement<K, E>, S extends ScalarStructure<K>>
extends AlgebraicStructure<E>
{
	S getScalarStructure();

//	default boolean isExact() {
//        return getScalarStructure().isExact();
//    }
}
