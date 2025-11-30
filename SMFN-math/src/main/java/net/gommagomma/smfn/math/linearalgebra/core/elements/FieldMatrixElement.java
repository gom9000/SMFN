package net.gommagomma.smfn.math.linearalgebra.core.elements;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;


public interface FieldMatrixElement<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>>
extends RingMatrixElement<K, V, M>
{
    K determinant();
    M inverse();
    M transpose();
}