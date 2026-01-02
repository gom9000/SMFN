package net.gommagomma.smfn.math.linearalgebra.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.tensors.TensorElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public final class Matrix<K extends ScalarElement<K>> 
implements LinearElement<Matrix<K>, K>, TensorElement<Matrix<K>, K>, ScalarElement<Matrix<K>>
{
    private final K[] data;
    private final int rows;
    private final int cols;
    private final ScalarStructure<K> scalarStructure;

    @SuppressWarnings("unchecked")
    public Matrix(int rows, int cols, ScalarStructure<K> scalarStructure) {
        this.rows = rows;
        this.cols = cols;
        this.scalarStructure = scalarStructure;
        // Mapping lineare: rows * cols
        this.data = (K[]) new ScalarElement[rows * cols];
    }

    // --- TensorElement Implementation ---
    @Override
    public int rank() { return 2; }

    @Override
    public int[] getShape() { return new int[]{rows, cols}; }

    @Override
    public long size() { return (long) rows * cols; }

    @Override
    public K get(int... indices) {
        return get(indices[0], indices[1]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ScalarStructure<Matrix<K>> getStructure() {
        // 1. Caso Matrice Quadrata -> La struttura naturale è un MatrixRing
        if (rows == cols) {
            if (scalarStructure instanceof Ring) {
                return (ScalarStructure<Matrix<K>>) new MatrixRing<>(
                    (Ring<K> & ScalarStructure<K>) scalarStructure, 
                    rows
                );
            }
            // Se è un Semiring (es. Naturali), restituiamo un MatrixSemiring
            return (ScalarStructure<Matrix<K>>) new MatrixSemiring<>(scalarStructure, rows);
        }

        // 2. Caso Matrice Rettangolare -> Non è un Anello (non puoi moltiplicare A*A)
        // Restituiamo il MatrixModule che definisce almeno la somma e lo scaling
        if (scalarStructure instanceof Ring) {
            return (ScalarStructure<Matrix<K>>) new MatrixModule<>(
                (Ring<K> & ScalarStructure<K>) scalarStructure, 
                rows, 
                cols
            );
        }
        
        return (ScalarStructure<Matrix<K>>) new MatrixSemimodule<>(scalarStructure, rows, cols);
    }

    // --- Metodi specifici per Matrici ---
    public K get(int row, int col) {
        return data[row * cols + col];
    }

    public void set(int row, int col, K value) {
        data[row * cols + col] = value;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    @Override
    public ScalarStructure<K> getScalarStructure() {
        return scalarStructure;
    }

    @Override
    public Matrix<K> copy() {
        Matrix<K> copy = new Matrix<>(rows, cols, scalarStructure);
        System.arraycopy(this.data, 0, copy.data, 0, data.length);
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Matrix " + rows + "x" + cols + " [\n");
        for (int i = 0; i < rows; i++) {
            sb.append("  [");
            for (int j = 0; j < cols; j++) {
                sb.append(get(i, j)).append(j == cols - 1 ? "" : ", ");
            }
            sb.append("]\n");
        }
        return sb.append("]").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Matrix)) return false;
        Matrix<?> other = (Matrix<?>) obj;

        if (this.rows != other.rows || this.cols != other.cols) return false;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Usa .equals() degli elementi K, non ==
                if (!this.get(i, j).equals(other.get(i, j))) return false;
            }
        }
        return true;
    }

    public Matrix<K> transpose() {
        Matrix<K> result = new Matrix<>(cols, rows, scalarStructure);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.set(j, i, this.get(i, j));
            }
        }
        return result;
    }
}