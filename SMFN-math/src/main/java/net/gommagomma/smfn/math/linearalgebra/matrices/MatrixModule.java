package net.gommagomma.smfn.math.linearalgebra.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public class MatrixModule<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>>
extends MatrixSemimodule<K, S>
implements Module<Matrix<K>, K, S>
{
    public MatrixModule(S scalarStructure, int rows, int cols) {
        super(scalarStructure, rows, cols);
    }

    @Override
    public String getName() {
        return "Matrix Module (" + rows + "x" + cols + ") over " + scalarStructure.getName();
    }


    @Override
    public Matrix<K> negate(Matrix<K> m) {
        validateDimensions(m);
        K[] resultData = (K[]) new ScalarElement[rows * cols];
        
        for (int i = 0; i < resultData.length; i++) {
            // Negazione scalare elemento per elemento
            resultData[i] = scalarStructure.negate(m.get(i / cols, i % cols));
        }
        return of(resultData);
    }


    @Override
    public Matrix<K> subtract(Matrix<K> a, Matrix<K> b) {
        validateDimensions(a);
        validateDimensions(b);
        
        K[] resultData = (K[]) new ScalarElement[rows * cols];
        for (int i = 0; i < resultData.length; i++) {
            resultData[i] = scalarStructure.subtract(
                a.get(i / cols, i % cols), 
                b.get(i / cols, i % cols)
            );
        }
        return of(resultData);
    }
}