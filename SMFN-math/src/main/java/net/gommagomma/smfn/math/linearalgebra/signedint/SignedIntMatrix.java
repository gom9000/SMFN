package net.gommagomma.smfn.math.linearalgebra.signedint;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.AbstractRingMatrix;

/**
 * Rappresenta una matrice i cui elementi sono numeri interi relativi (SignedInt).
 */
public final class SignedIntMatrix
extends AbstractRingMatrix<SignedInt, SignedIntVector, SignedIntMatrix, SignedIntMatrixFactory>
{
    private static final SignedIntMatrixFactory FACTORY_INSTANCE = SignedIntMatrixFactory.getInstance();

    /**
     * Costruttore principale che accetta i dati e la factory.
     */
    public SignedIntMatrix(SignedInt[][] data) {
        super(data, FACTORY_INSTANCE);
    }

    /**
     * Costruttore per creare una matrice zero/vuota.
     */
    public SignedIntMatrix(int rows, int cols) {
        super(rows, cols, FACTORY_INSTANCE);
    }

    /**
     * Restituisce la classe runtime dello scalare K (SignedInt).
     * Essenziale per la reflection nell'AbstractMatrix.
     */
    @Override
    protected Class<SignedInt> getScalarClass() {
        return SignedInt.class;
    }

    @Override
    public SignedIntMatrix getZero() {
        // Delega la creazione della matrice zero alla factory
        return factory.createZeroMatrix(rows, cols);
    }

    @Override
    public SignedIntVector getRowVector(int row)
    {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }

        return factory.createVector(Arrays.copyOf(data[row], cols));
    }

    @Override
    public SignedIntVector getColumnVector(int col)
    {
        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }

        SignedInt[] columnData = new SignedInt[rows];
        for (int i = 0; i < rows; i++) {
            columnData[i] = data[i][col];
        }

        return factory.createVector(columnData);
    }
}
