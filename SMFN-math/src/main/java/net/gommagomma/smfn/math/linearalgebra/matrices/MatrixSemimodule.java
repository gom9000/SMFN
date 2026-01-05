package net.gommagomma.smfn.math.linearalgebra.matrices;

import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.CompositeElementFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Semimodule;

public class MatrixSemimodule<K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
implements Semimodule<Matrix<K>, K, S>, CompositeElementFactory<Matrix<K>, K[]>
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
    public Matrix<K> of(K[] data) {
        return new Matrix<>(this, scalarStructure, rows, cols, data);
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
    	if (m == null) return false;
        if (m.getStructure() != this) {
            return m.getRows() == this.rows && m.getCols() == this.cols;
        }
        return true;
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
        K[] data = (K[]) new ScalarElement[rows * cols];
        K zeroScalar = scalarStructure.zero();
        for (int i = 0; i < data.length; i++) {
            data[i] = zeroScalar;
        }
        return of(data);
    }

    @Override
    public Matrix<K> add(Matrix<K> a, Matrix<K> b) {
        validateDimensions(a);
        validateDimensions(b);
        
        K[] resultData = (K[]) new ScalarElement[rows * cols];
        for (int i = 0; i < rows * cols; i++) {
            resultData[i] = scalarStructure.add(a.get(i / cols, i % cols), b.get(i / cols, i % cols));
        }
        return of(resultData);
    }

    @Override
    public Matrix<K> scale(K scalar, Matrix<K> m) {
        validateDimensions(m);
        K[] resultData = (K[]) new ScalarElement[rows * cols];
        for (int i = 0; i < rows * cols; i++) {
            resultData[i] = scalarStructure.multiply(scalar, m.get(i / cols, i % cols));
        }
        return of(resultData);
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


	public Matrix<K> transpose(Matrix<K> m) {
        validateDimensions(m);
        K[] transposedData = (K[]) new ScalarElement[rows * cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposedData[j * rows + i] = m.get(i, j);
            }
        }
        MatrixSemimodule<K, S> targetSpace = new MatrixSemimodule<>(scalarStructure, cols, rows);
        return targetSpace.of(transposedData);
    }
}