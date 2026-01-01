package net.gommagomma.smfn.math.algebra.core.elements;

import net.gommagomma.smfn.math.algebra.core.structures.ScalarStructure;

public interface ScalarElement<K extends ScalarElement<K>>
extends AlgebraicElement<K>
{
	ScalarStructure<K> getStructure();
}
