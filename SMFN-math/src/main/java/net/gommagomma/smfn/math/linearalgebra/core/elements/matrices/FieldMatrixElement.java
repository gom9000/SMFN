package net.gommagomma.smfn.math.linearalgebra.core.elements.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;


public interface FieldMatrixElement<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>>
extends RingMatrixElement<K, V, M>
{
    K determinant();
    M inverse();
    M transpose();
}