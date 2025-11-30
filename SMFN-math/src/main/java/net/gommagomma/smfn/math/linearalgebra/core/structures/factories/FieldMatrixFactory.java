package net.gommagomma.smfn.math.linearalgebra.core.structures.factories;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.matrices.FieldMatrixElement;


public interface FieldMatrixFactory<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>>
extends RingMatrixFactory<K, V, M>
{
	//M createIdentityMatrix(int size);
}
