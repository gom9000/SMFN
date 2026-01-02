package net.gommagomma.smfn.math.linearalgebra.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.metric.InnerProductSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;

public class InnerProductMatrixSpace<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
extends MatrixSpace<K, S>
implements InnerProductSpace<Matrix<K>, K, S>
{
    public InnerProductMatrixSpace(S scalarStructure, int rows, int cols) {
        super(scalarStructure, rows, cols);
    }

    @Override
    public K innerProduct(Matrix<K> a, Matrix<K> b) {
        validateDimensions(a);
        validateDimensions(b);
        
        K total = scalarStructure.zero();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Il prodotto interno di Frobenius: tr(A^H * B) 
                // ovvero la somma di a_ij * b_ij
                K prod = scalarStructure.multiply(a.get(i, j), b.get(i, j));
                total = scalarStructure.add(total, prod);
            }
        }
        return total;
    }

    @Override
    public Real norm(Matrix<K> m) {
        // ||M|| = sqrt(innerProduct(m, m))
        K selfDot = innerProduct(m, m);
        return scalarStructure.magnitude(selfDot).sqrt();
    }
}