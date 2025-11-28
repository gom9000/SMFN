package net.gommagomma.smfn.math.core.linearalgebra.elements;

import net.gommagomma.smfn.math.core.algebra.AlgebraicElement;
import net.gommagomma.smfn.math.core.algebra.elements.additive.AbelianGroupElement;
import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;

public interface MatrixElement<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends MatrixElement<K, V, M>>
extends AlgebraicElement<M>, AbelianGroupElement<M>
{
    int getRows();
    int getColumns();
    K get(int row, int col);

    M multiply(M other); 
    M multiplyByScalar(K scalar);
    
    // Moltiplicazione matrice-vettore
    V multiply(V vector);

    // Operazioni specifiche per l'algebra lineare su campo
    K determinant(); // Solo per matrici quadrate
    M inverse();     // Solo per matrici quadrate invertibili
    M transpose();

    V getRowVector(int row);
    V getColumnVector(int col);
}