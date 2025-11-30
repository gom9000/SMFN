package net.gommagomma.smfn.math.linearalgebra.core.elements;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.additive.AbelianGroupElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;

public interface MatrixElement<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends MatrixElement<K, V, M>>
extends AlgebraicElement<M>, AbelianGroupElement<M>
{
    int getRows();
    int getColumns();
    K get(int row, int col);
    V getRowVector(int row);
    V getColumnVector(int col);

    M multiply(M other); 
    M multiplyByScalar(K scalar);

    V multiply(V vector);

    // Operazioni specifiche per l'algebra lineare su campo
    K determinant();
    M inverse();
    M transpose();
}