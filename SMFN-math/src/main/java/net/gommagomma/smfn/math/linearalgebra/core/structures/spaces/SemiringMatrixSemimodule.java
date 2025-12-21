package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.SemiringMatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;
import net.gommagomma.smfn.math.linearalgebra.core.factories.MatrixElementFactory;


public interface SemiringMatrixSemimodule<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>>
extends LinearSpace<K, M>, MatrixElementFactory<K, V, M>, DimensionalStructure<SemiringMatrixSemimodule<K, V, M>>
{
	Semimodule<K, V> getVectorStructure();
	int getMatrixRows();
	int getMatrixColumns();
}
