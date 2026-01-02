package net.gommagomma.smfn.math.linearalgebra.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Rappresenta l'Anello delle matrici quadrate n x n sopra un Anello K.
 * Unisce le capacità di MatrixModule (sottrazione) e MatrixSemiring (moltiplicazione).
 */
public class MatrixRing<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>>
extends MatrixModule<K, S>
implements Ring<Matrix<K>>
{
    private final MatrixSemiring<K, S> semiringDelegate;

    public MatrixRing(S scalarStructure, int n) {
        super(scalarStructure, n, n);
        // Deleghiamo la logica della moltiplicazione al semiring per non duplicare il codice
        this.semiringDelegate = new MatrixSemiring<>(scalarStructure, n);
    }

    @Override
    public Matrix<K> one() {
        return semiringDelegate.one();
    }

    @Override
    public Matrix<K> multiply(Matrix<K> a, Matrix<K> b) {
        return semiringDelegate.multiply(a, b);
    }

    @Override
    public boolean isOne(Matrix<K> element) {
        return semiringDelegate.isOne(element);
    }
    
    @Override
    public String getName() {
        return "Matrix Ring (" + rows + "x" + cols + ") over " + scalarStructure.getName();
    }
}