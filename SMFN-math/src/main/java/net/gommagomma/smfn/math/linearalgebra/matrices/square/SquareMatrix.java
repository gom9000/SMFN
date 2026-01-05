package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.tensors.TensorElement;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;

public final class SquareMatrix<K extends ScalarElement<K>> 
implements ScalarElement<SquareMatrix<K>>, TensorElement<SquareMatrix<K>, K>
{
	private final Matrix<K> internalMatrix;
    private final ScalarStructure<SquareMatrix<K>> structure;

    protected SquareMatrix(ScalarStructure<SquareMatrix<K>> structure, Matrix<K> matrix) {
        if (matrix.getRows() != matrix.getCols()) {
            throw new IllegalArgumentException("La matrice interna deve essere quadrata.");
        }
        this.internalMatrix = matrix;
        this.structure = structure;
    }


    @Override
    public ScalarStructure<SquareMatrix<K>> getStructure() {
        return structure;
    }

    public K get(int r, int c) { return internalMatrix.get(r, c); }
    public int getRows() { return internalMatrix.getRows(); }
    public int getCols() { return internalMatrix.getCols(); }
    public int getN() { return internalMatrix.getRows(); }
    public K[] getData() { return internalMatrix.getData(); }

    @Override
    public SquareMatrix<K> copy() {
        // Essendo immutabile, possiamo restituire this o una nuova istanza con la stessa internalMatrix
        return new SquareMatrix<>(structure, internalMatrix);
    }

    public Matrix<K> asMatrix() {
        return internalMatrix;
    }


    @Override
    public int rank() { return 2; }

    @Override
    public int[] getShape() { return new int[]{getN(), getN()}; }

    @Override
    public long size() { return (long) getN() * getN(); }

    @Override
    public K get(int... indices) {
    	if (indices.length != 2) {
            throw new IllegalArgumentException("SquareMatrix richiede esattamente 2 indici.");
        }
        return get(indices[0], indices[1]);
    }

    @Override
    public String toString() {
        // Sostituiamo il prefisso "Matrix" con "SquareMatrix" per chiarezza
        return internalMatrix.toString().replaceFirst("Matrix", "SquareMatrix");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SquareMatrix)) return false;
        SquareMatrix<?> that = (SquareMatrix<?>) o;
        return internalMatrix.equals(that.internalMatrix);
    }

    @Override
    public int hashCode() {
        return internalMatrix.hashCode();
    }

    /**
     * Metodo di convenienza per la traccia, tipico delle matrici quadrate.
     */
    public K trace() {
        K total = internalMatrix.getScalarStructure().zero();
        for (int i = 0; i < getN(); i++) {
            total = internalMatrix.getScalarStructure().add(total, get(i, i));
        }
        return total;
    }
}
