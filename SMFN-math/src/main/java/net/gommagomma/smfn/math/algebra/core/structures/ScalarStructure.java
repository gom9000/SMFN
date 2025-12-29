package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

public interface ScalarStructure<E extends ScalarElement<E>> 
extends AlgebraicStructure<E>, NumericFactory<E>
{
	default boolean isExact() {
        return this instanceof ExactStructure; 
    }
}
