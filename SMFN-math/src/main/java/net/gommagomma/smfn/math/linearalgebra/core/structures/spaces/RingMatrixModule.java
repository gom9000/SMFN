package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.RingMatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.ModuleElement;


public interface RingMatrixModule<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>>
extends SemiringMatrixSemimodule<K, V, M>
{
	@Override
	Ring<K> getScalarStructure();

	@Override
	Module<K, V> getVectorStructure();
}
