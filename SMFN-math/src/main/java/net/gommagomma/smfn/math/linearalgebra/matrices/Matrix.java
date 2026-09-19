package net.gommagomma.smfn.math.linearalgebra.matrices;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.tensors.TensorElement;
import net.gommagomma.smfn.math.algebra.core.structures.composite.LinearStructure;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.analysis.core.functions.VectorFunction;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

/**
 * Una matrice m x n rappresenta un'applicazione lineare K^n -> K^m: non e'
 * un endomorfismo (dominio e codominio hanno dimensione diversa in generale),
 * quindi implementa Mapping<Vector<K>,Vector<K>> e non Operator/LinearOperator
 * -- compose()/power() presuppongono un endomorfismo e per una matrice
 * rettangolare non avrebbero senso dimensionale. SquareMatrix, che e' sempre
 * un endomorfismo, resta l'unica a implementare LinearOperator.
 */
public final class Matrix<K extends ScalarElement<K>> 
implements LinearElement<Matrix<K>, K>, TensorElement<Matrix<K>, K>, VectorFunction<K>
{
    private final K[] data;
    protected final LinearStructure<Matrix<K>, K, ?> matrixStructure;
    protected final ScalarStructure<K> scalarStructure;
    private final int rows;
    private final int cols;


    protected Matrix(LinearStructure<Matrix<K>, K, ?> matrixStructure, ScalarStructure<K> scalarStructure, int rows, int cols, K[] data) {
    	this.matrixStructure = matrixStructure;
    	this.scalarStructure = scalarStructure;
        this.rows = rows;
        this.cols = cols;
        this.data = data.clone();
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

    // --- Metodi specifici per Matrici ---
    public K get(int row, int col) {
        return data[row * cols + col];
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    @Override
    public ScalarStructure<K> getScalarStructure() {
        return scalarStructure;
    }

    /**
     * Applica la matrice a un vettore: y = A*x, con x di dimensione cols e
     * y di dimensione rows. E' questo metodo, non un'interfaccia a parte, che
     * rende Matrix un'applicazione lineare K^n -> K^m.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Vector<K> apply(Vector<K> v) {
        if (v.size() != cols) {
            throw new IllegalArgumentException("Dimensione del vettore incompatibile: attesa " + cols + ", ricevuta " + v.size());
        }

        K[] resultData = (K[]) new ScalarElement[rows];
        for (int i = 0; i < rows; i++) {
            K sum = scalarStructure.zero();
            for (int j = 0; j < cols; j++) {
                sum = scalarStructure.add(sum, scalarStructure.multiply(get(i, j), v.get(j)));
            }
            resultData[i] = sum;
        }

        VectorSemimodule<K, ScalarStructure<K>> codomain = new VectorSemimodule<>(scalarStructure, rows);
        return codomain.of(resultData);
    }

    @Override
    public Matrix<K> copy() {
    	return new Matrix<>(matrixStructure, scalarStructure, rows, cols, data.clone());
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

        return rows == other.rows && cols == other.cols && Arrays.equals(data, other.data);
    }

    @Override
    public int hashCode() {
        int result = java.util.Objects.hash(rows, cols);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    public LinearStructure<Matrix<K>, K, ?> getStructure() {
        return matrixStructure;
    }

    public K[] getData() {
        return data.clone();
    }
}