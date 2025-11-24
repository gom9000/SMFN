package net.gommagomma.smfn.math.linearalgebra.core;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;

public interface MatrixElement<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends MatrixElement<K, V, M>>
extends AlgebraicElement<M>
{
    int getRows();
    int getColumns();
    K get(int row, int col);

    // Operazioni base (Addizione/Sottrazione richiedono stesse dimensioni)
    M add(M other);
    M subtract(M other);
    
    // Moltiplicazione matrice-matrice (richiede compatibilità delle dimensioni)
    M multiply(M other); 
    
    // Moltiplicazione matrice-vettore
    V multiply(V vector);

    // Operazioni specifiche per l'algebra lineare su campo
    K determinant(); // Solo per matrici quadrate
    M inverse();     // Solo per matrici quadrate invertibili
    M transpose();

    V getRowVector(int row);
    V getColumnVector(int col);
}