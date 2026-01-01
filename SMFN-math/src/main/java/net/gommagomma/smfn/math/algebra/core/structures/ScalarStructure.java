package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.ExactStructure;

public interface ScalarStructure<K extends ScalarElement<K>> 
extends Semiring<K>
{
	default boolean isExact() {
        return this instanceof ExactStructure; 
    }
}
