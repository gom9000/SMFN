package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.ExactElement;
import net.gommagomma.smfn.math.algebra.core.structures.ScalarStructure;

public interface ExactStructure<K extends ExactElement<K>>
extends ScalarStructure<K>
{
	@Override
    default boolean isExact() { return true; }

    @Override
    default boolean areEqual(K a, K b) {
    	if (a == b) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }	
}
