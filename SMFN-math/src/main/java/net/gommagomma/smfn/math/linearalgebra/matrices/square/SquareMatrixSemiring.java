package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.CompositeElementFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSemimodule;

/**
 * Implementa la struttura di Semianello per matrici quadrate n x n.
 * Funziona con qualsiasi scalare che sia almeno un Semiring (es. Natural).
 */
public class SquareMatrixSemiring<K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
implements Semiring<SquareMatrix<K>>, ScalarStructure<SquareMatrix<K>>, CompositeElementFactory<SquareMatrix<K>, K[]>
{
	protected final S scalarStructure;
    protected final int n;
    private final MatrixSemimodule<K, S> matrixDelegate;

    public SquareMatrixSemiring(S scalarStructure, int n) {
    	this.scalarStructure = scalarStructure;
        this.n = n;
        this.matrixDelegate = new MatrixSemimodule<>(scalarStructure, n, n);
    }

    @Override
    public SquareMatrix<K> of(K[] data) {
    	Matrix<K> internal = matrixDelegate.of(data);
        return new SquareMatrix<>(this, internal);
    }

    @Override
    public SquareMatrix<K> zero() {
        return of(matrixDelegate.zero().getData());
    }

    @Override
    public SquareMatrix<K> one() {
    	K[] data = (K[]) new ScalarElement[n * n];
        K zero = scalarStructure.zero();
        K one = scalarStructure.one();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                data[i * n + j] = (i == j) ? one : zero;
            }
        }
        return of(data);
    }

    @Override
    public SquareMatrix<K> multiply(SquareMatrix<K> a, SquareMatrix<K> b) {
    	if (a.getN() != n || b.getN() != n) {
            throw new IllegalArgumentException("Dimensioni incompatibili con questa struttura " + n + "x" + n);
        }

        K[] resultData = (K[]) new ScalarElement[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                K sum = scalarStructure.zero();
                for (int k = 0; k < n; k++) {
                    K prod = scalarStructure.multiply(a.get(i, k), b.get(k, j));
                    sum = scalarStructure.add(sum, prod);
                }
                resultData[i * n + j] = sum;
            }
        }
        return of(resultData);
    }

    @Override
    public SquareMatrix<K> add(SquareMatrix<K> a, SquareMatrix<K> b) {
    	Matrix<K> result = matrixDelegate.add(a.asMatrix(), b.asMatrix());
        return of(result.getData());
    }

    @Override
    public boolean isOne(SquareMatrix<K> m) {
        return areEqual(m, one());
    }

	@Override
    public boolean isExact() {
        return scalarStructure.isExact();
    }

	@Override
    public Real magnitude(SquareMatrix<K> element) {
        double sumOfSquares = 0.0;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // calcolo della norma di Frobenius
                double m = scalarStructure.magnitude(element.get(i, j)).getValue();
                sumOfSquares += m * m;
            }
        }

        return RealField.INSTANCE.of(Math.sqrt(sumOfSquares));
    }

	@Override
    public boolean areEqual(SquareMatrix<K> a, SquareMatrix<K> b) {
        return a.asMatrix().equals(b.asMatrix());
    }
	
	@Override
    public String getName() {
        return "Square Matrix Semiring (" + n + "x" + n + ") over " + scalarStructure.getName();
    }

    public SquareMatrix<K> transpose(SquareMatrix<K> m) {
    	if (m.getN() != n) {
            throw new IllegalArgumentException("Dimensione matrice errata.");
        }

        K[] transposedData = (K[]) new ScalarElement[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                transposedData[j * n + i] = m.get(i, j);
            }
        }
        return of(transposedData);
    }

    public S getScalarStructure() {
        return scalarStructure;
    }

	@Override
	public boolean contains(SquareMatrix<K> m) {
		return m != null && m.getStructure() == this;
	}
}