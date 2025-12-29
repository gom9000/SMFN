package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.ExactElement;

public interface ExactStructure<E extends ExactElement<E>>
extends ScalarStructure<E>
{
	@Override
    default boolean isExact() { return true; }

    @Override
    default boolean areEqual(E a, E b) {
    	if (a == b) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }	
}
