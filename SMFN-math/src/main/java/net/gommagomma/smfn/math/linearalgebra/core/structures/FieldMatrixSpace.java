package net.gommagomma.smfn.math.linearalgebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.linearalgebra.core.elements.FieldMatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.VectorElement;


public interface FieldMatrixSpace<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>>
extends RingMatrixSpace<K, V, M>
{
	@Override
	Field<K> getScalarStructure();
}