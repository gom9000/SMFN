package net.gommagomma.smfn.math.linearalgebra.real;

import java.util.Arrays;
import java.util.Comparator;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.elements.AbstractMatrix;


public final class RealMatrix 
extends AbstractMatrix<Real, RealVector, RealMatrix, RealMatrixFactory>
{
    private static final RealMatrixFactory FACTORY_INSTANCE = RealMatrixFactory.getInstance();


    /**
     * Costruttore principale per creare un RealMatrix da un array bidimensionale di componenti.
     * @param data L'array di componenti Real.
     */
    public RealMatrix(Real[][] data) {
        super(data, FACTORY_INSTANCE);
    }
    
    /**
     * Costruttore per creare una matrice di zeri di dimensioni specifiche.
     * Usato internamente per getZero(), identity(), ecc.
     */
    RealMatrix(int rows, int cols) {
    	super(rows, cols, FACTORY_INSTANCE);
    	for (int i = 0; i < rows; i++) {
    		Arrays.fill(this.data[i], Real.ZERO);
    	}
    }

    @Override
    protected Class<Real> getScalarClass() {
        return Real.class;
    }

    @Override
    protected Comparator<Real> getMagnitudeComparator() {
        return Comparator.naturalOrder(); 
    }


    // MatrixElement impls
    
    @Override
    public RealVector getRowVector(int row)
    {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }

        return factory.createVector(Arrays.copyOf(data[row], cols));
    }

    @Override
    public RealVector getColumnVector(int col)
    {
        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }

        Real[] columnData = new Real[rows];
        for (int i = 0; i < rows; i++) {
            columnData[i] = data[i][col];
        }

        return factory.createVector(columnData);
    }


    // Helper Methods

    /**
     * Helper statico per creare matrici da array di double primitivi.
     */
    public static RealMatrix fromDoubles(double[][] data)
    {
        int rows = data.length;
        int cols = data.length == 0 ? 0 : data[0].length;
        Real[][] realData = new Real[rows][cols];
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                 throw new IllegalArgumentException("All rows must have the same number of columns.");
            }
            for (int j = 0; j < cols; j++) {
                realData[i][j] = new Real(data[i][j]);
            }
        }

        return new RealMatrix(realData);
    }


    public static RealMatrix identity(int size)
    {
        if (size <= 0) {
            throw new IllegalArgumentException("Dimension must be positive.");
        }

        RealMatrix identityMatrix = new RealMatrix(size, size);
        for (int i = 0; i < size; i++) {
             identityMatrix.data[i][i] = Real.ONE;
        }

        return identityMatrix; 
    }


    // Java Standard impls
    
    /**
     * WARNING: This equals method uses epsilon comparisons via Real.isEqual,
     * violating the strict transitivity contract of Object.equals() in standard Java collections.
     */
    @Override
    public final boolean equals(Object other) {
        return (other instanceof RealMatrix) && isEqual((RealMatrix)other);
    }

    @Override
    public final int hashCode() {
        int result = java.util.Objects.hash(rows, cols);
        result = 31 * result + Arrays.deepHashCode(data);
        return result;
    }
}
