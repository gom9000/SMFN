package net.gommagomma.smfn.math.linearalgebra.core.elements.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;
import net.gommagomma.smfn.math.linearalgebra.core.operators.LinearMapping;


public interface SemiringMatrixElement<K extends SemiringElement<K>,V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>> 
extends SemiringElement<M>, LinearMapping<K, V, M>
{
    int getRows();
    int getColumns();
    K get(int row, int col);
    V getRowVector(int row);
    V getColumnVector(int col);

    M multiply(M other); 
    M multiplyByScalar(K scalar);
    M transpose();

    V multiply(V vector);	
}
