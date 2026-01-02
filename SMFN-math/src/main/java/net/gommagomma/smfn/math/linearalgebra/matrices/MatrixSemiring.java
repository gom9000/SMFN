package net.gommagomma.smfn.math.linearalgebra.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Implementa la struttura di Semianello per matrici quadrate n x n.
 * Funziona con qualsiasi scalare che sia almeno un Semiring (es. Natural).
 */
public class MatrixSemiring<K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
extends MatrixSemimodule<K, S>
implements Semiring<Matrix<K>>
{
    private final int n;

    public MatrixSemiring(S scalarStructure, int n) {
        super(scalarStructure, n, n);
        this.n = n;
    }

    @Override
    public Matrix<K> one() {
        Matrix<K> identity = zero();
        K oneScalar = scalarStructure.one();
        for (int i = 0; i < n; i++) {
            identity.set(i, i, oneScalar);
        }
        return identity;
    }

    @Override
    public Matrix<K> multiply(Matrix<K> a, Matrix<K> b) {
        validateDimensions(a);
        validateDimensions(b);

        Matrix<K> result = new Matrix<>(n, n, scalarStructure);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                K sum = scalarStructure.zero();
                for (int k = 0; k < n; k++) {
                    // sum += a[i][k] * b[k][j]
                    K prod = scalarStructure.multiply(a.get(i, k), b.get(k, j));
                    sum = scalarStructure.add(sum, prod);
                }
                result.set(i, j, sum);
            }
        }
        return result;
    }

    @Override
    public boolean isOne(Matrix<K> m) {
        return areEqual(m, one());
    }
}