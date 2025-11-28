package net.gommagomma.smfn.math.core.linearalgebra.structures;

import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.algebra.structures.Field;
import net.gommagomma.smfn.math.core.linearalgebra.elements.MatrixElement;
import net.gommagomma.smfn.math.core.linearalgebra.elements.VectorElement;

public interface MatrixSpace<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends MatrixElement<K, V, M>>
extends Space<M>
{
	Field<K> getScalarField();
	int getMatrixRows();
	int getMatrixColumns();
}