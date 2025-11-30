package net.gommagomma.smfn.math.linearalgebra.natural;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.AbstractSemiringMatrix;


/**
 * Rappresenta una matrice i cui elementi sono numeri naturali (Natural).
 */
public final class NaturalMatrix 
extends AbstractSemiringMatrix<Natural, NaturalVector, NaturalMatrix, NaturalMatrixFactory>
{
    private static final NaturalMatrixFactory FACTORY_INSTANCE = NaturalMatrixFactory.getInstance();

    /**
     * Costruttore principale che accetta i dati e la factory.
     */
    public NaturalMatrix(Natural[][] data) {
        super(data, FACTORY_INSTANCE);
    }

    /**
     * Costruttore per creare una matrice di zeri di dimensioni specifiche.
     * Usato internamente per getZero(), identity(), ecc.
     */
    NaturalMatrix(int rows, int cols) {
    	super(rows, cols, FACTORY_INSTANCE);
    	for (int i = 0; i < rows; i++) {
    		Arrays.fill(this.data[i], Natural.ZERO);
    	}
    }

    /**
     * Restituisce la classe runtime dello scalare K (Natural).
     * Essenziale per la reflection nell'AbstractMatrix.
     */
    @Override
    protected Class<Natural> getScalarClass() {
        return Natural.class;
    }

    @Override
    public NaturalMatrix getZero() {
        return factory.createZeroMatrix(rows, cols);
    }

    @Override
    public NaturalVector getRowVector(int row)
    {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }

        return factory.createVector(Arrays.copyOf(data[row], cols));
    }

    @Override
    public NaturalVector getColumnVector(int col)
    {
        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }

        Natural[] columnData = new Natural[rows];
        for (int i = 0; i < rows; i++) {
            columnData[i] = data[i][col];
        }

        return factory.createVector(columnData);
    }
}
