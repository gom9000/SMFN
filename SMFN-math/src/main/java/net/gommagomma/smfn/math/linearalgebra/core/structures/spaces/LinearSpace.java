package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.AlgebraicStructure;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

public interface LinearSpace<K extends SemiringElement<K>, V extends AlgebraicElement<V>>
extends AlgebraicStructure<V>
{
	Semiring<K> getScalarStructure();
}
