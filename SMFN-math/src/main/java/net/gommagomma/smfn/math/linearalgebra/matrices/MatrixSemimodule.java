package net.gommagomma.smfn.math.linearalgebra.matrices;

import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Semimodule;

public class MatrixSemimodule<K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
implements Semimodule<Matrix<K>, K, S>
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
    public boolean isZero(Matrix<K> element) {
        if (!contains(element)) return false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!scalarStructure.isZero(element.get(i, j))) return false;
            }
        }
        return true;
    }
}