package net.gommagomma.smfn.math.linearalgebra.core;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;

public interface MatrixSpace<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends Matrix<K, V, M>>
extends Space<M>
{
	Field<K> getScalarField();
	int getMatrixRows();
	int getMatrixColumns();
}