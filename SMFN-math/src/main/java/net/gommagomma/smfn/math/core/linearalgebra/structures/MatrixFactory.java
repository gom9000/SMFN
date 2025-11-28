package net.gommagomma.smfn.math.core.linearalgebra.structures;

import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.linearalgebra.elements.MatrixElement;
import net.gommagomma.smfn.math.core.linearalgebra.elements.VectorElement;


public interface MatrixFactory<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends MatrixElement<K, V, M>>
{
    M createMatrix(K[][] data);
    M createZeroMatrix(int rows, int cols);
    V createVector(K[] data);
    K getZeroScalar();
    K getOneScalar();
}
