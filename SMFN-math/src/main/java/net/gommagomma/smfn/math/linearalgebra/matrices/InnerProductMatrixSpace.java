package net.gommagomma.smfn.math.linearalgebra.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.metric.InnerProductSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

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
                // Prodotto interno hermitiano: <A,B> = sum conj(A_ij) * B_ij
                K conjA = conjugateIfPossible(a.get(i, j));
                K prod = scalarStructure.multiply(conjA, b.get(i, j));
                total = scalarStructure.add(total, prod);
            }
        }
        return total;
    }

    @Override
    public Real norm(Matrix<K> m) {
    	validateDimensions(m);
    	RealField R = RealField.INSTANCE;
        // Piu' stabile e diretto: somma dei quadrati delle magnitudo degli elementi
        Real sumOfSquares = R.zero();
        for (int i = 0; i < rows * cols; i++) {
            Real mag = scalarStructure.magnitude(m.get(i/cols, i%cols));
            sumOfSquares = R.add(sumOfSquares, R.multiply(mag, mag));
        }
        return sumOfSquares.sqrt();
    }
}
