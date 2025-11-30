package net.gommagomma.smfn.math.linearalgebra.core.elements;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;


public interface MatrixFactory<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>>
{
    M createMatrix(K[][] data);
    M createZeroMatrix(int rows, int cols);
    V createVector(K[] data);
    K getZeroScalar();
    K getOneScalar();
}
