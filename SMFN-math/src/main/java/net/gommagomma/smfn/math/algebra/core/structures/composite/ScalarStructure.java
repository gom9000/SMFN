package net.gommagomma.smfn.math.algebra.core.structures.composite;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numerics.Real;

public interface ScalarStructure<K extends ScalarElement<K>> 
extends Semiring<K>
{
	Real magnitude(K element);
	boolean isExact(); 
}
