package net.gommagomma.smfn.math.linearalgebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.linearalgebra.core.elements.MatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.VectorElement;

public interface MatrixSpace<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends MatrixElement<K, V, M>>
extends Space<M>
{
	Field<K> getScalarField();
	int getMatrixRows();
	int getMatrixColumns();
}