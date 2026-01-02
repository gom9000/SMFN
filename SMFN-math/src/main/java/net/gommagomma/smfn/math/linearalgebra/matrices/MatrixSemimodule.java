package net.gommagomma.smfn.math.linearalgebra.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Semimodule;
import net.gommagomma.smfn.math.algebra.numerics.Real;

import java.util.Objects;

public class MatrixSemimodule<K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
implements Semimodule<Matrix<K>, K, S>, ScalarStructure<Matrix<K>>
{
    protected final S scalarStructure;
    protected final int rows;
    protected final int cols;

    public MatrixSemimodule(S scalarStructure, int rows, int cols) {
        this.scalarStructure = Objects.requireNonNull(scalarStructure);
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public S getScalarStructure() {
        return scalarStructure;
    }

    @Override
    public String getName() {
        return "Matrix Semimodule (" + rows + "x" + cols + ") over " + scalarStructure.getName();
    }

    @Override
    public boolean contains(Matrix<K> m) {
        return m != null && m.getRows() == this.rows && m.getCols() == this.cols;
    }

    @Override
    public boolean areEqual(Matrix<K> a, Matrix<K> b) {
        if (!contains(a) || !contains(b)) return false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!scalarStructure.areEqual(a.get(i, j), b.get(i, j))) return false;
            }
        }
        return true;
    }

    @Override
    public Matrix<K> zero() {
        Matrix<K> zeroMatrix = new Matrix<>(rows, cols, scalarStructure);
        K zeroScalar = scalarStructure.zero();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                zeroMatrix.set(i, j, zeroScalar);
            }
        }
        return zeroMatrix;
    }

    @Override
    public Matrix<K> add(Matrix<K> a, Matrix<K> b) {
        validateDimensions(a);
        validateDimensions(b);
        
        Matrix<K> result = new Matrix<>(rows, cols, scalarStructure);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                K sum = scalarStructure.add(a.get(i, j), b.get(i, j));
                result.set(i, j, sum);
            }
        }
        return result;
    }

    @Override
    public Matrix<K> scale(K scalar, Matrix<K> m) {
        validateDimensions(m);
        
        Matrix<K> result = new Matrix<>(rows, cols, scalarStructure);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                K scaled = scalarStructure.multiply(scalar, m.get(i, j));
                result.set(i, j, scaled);
            }
        }
        return result;
    }

    protected void validateDimensions(Matrix<K> m) {
        if (!contains(m)) {
            throw new IllegalArgumentException("Matrix dimensions mismatch. Expected: " + rows + "x" + cols);
        }
    }

	@Override
	public Matrix<K> one() {
		throw new UnsupportedOperationException("L'identità non è definita in un Semimodulo.");
	}

	@Override
	public Matrix<K> multiply(Matrix<K> a, Matrix<K> b) {
		throw new UnsupportedOperationException("La moltiplicazione non è definita in un Semimodulo.");
	}

	@Override
    public Real magnitude(Matrix<K> element) {
        validateDimensions(element);
        double sumOfSquares = 0.0;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // calcolo della norma di Frobenius
                double m = scalarStructure.magnitude(element.get(i, j)).getValue();
                sumOfSquares += m * m;
            }
        }

        return net.gommagomma.smfn.math.algebra.structures.RealField.INSTANCE.of(Math.sqrt(sumOfSquares));
    }

	@Override
    public boolean isZero(Matrix<K> element) {
        if (!contains(element)) return false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!scalarStructure.isZero(element.get(i, j))) return false;
            }
        }
        return true;
    }

	@Override
    public boolean isExact() {
        return scalarStructure.isExact();
    }
}