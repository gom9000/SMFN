package net.gommagomma.smfn.math.linearalgebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.linearalgebra.core.elements.SemimoduleElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.SemiringMatrixElement;


public interface SemiringMatrixSpace<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>>
extends Space<M>
{
	Semiring<K> getScalarStructure();
	int getMatrixRows();
	int getMatrixColumns();
}
