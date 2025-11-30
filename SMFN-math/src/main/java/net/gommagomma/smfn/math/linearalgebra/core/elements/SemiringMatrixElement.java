package net.gommagomma.smfn.math.linearalgebra.core.elements;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.additive.CommutativeMonoidElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;


public interface SemiringMatrixElement<K extends SemiringElement<K>,V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>> 
extends AlgebraicElement<M>, CommutativeMonoidElement<M>
{
    int getRows();
    int getColumns();
    K get(int row, int col);
    V getRowVector(int row);
    V getColumnVector(int col);

    M multiply(M other); 
    M multiplyByScalar(K scalar);

    V multiply(V vector);	
}
