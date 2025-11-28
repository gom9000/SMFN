package net.gommagomma.smfn.math.linearalgebra.complex;

import java.util.Arrays;
import java.util.Comparator;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.linearalgebra.core.algorithms.GaussJordanElimination;
import net.gommagomma.smfn.math.linearalgebra.core.algorithms.GaussianElimination;
import net.gommagomma.smfn.math.linearalgebra.core.elements.AbstractMatrix;

/**
 * Rappresenta una matrice di numeri complessi immutabile.
 * Aderisce all'interfaccia Matrix<Complex, ComplexVector, ComplexMatrix>.
 */
public final class ComplexMatrix
extends AbstractMatrix<Complex, ComplexVector, ComplexMatrix, ComplexMatrixFactory>
{    
	private static final ComplexMatrixFactory FACTORY_INSTANCE = ComplexMatrixFactory.getInstance();


	/**
     * Costruttore principale per creare un ComplexMatrix da un array bidimensionale di componenti.
     * @param data L'array di componenti Complex.
     */
    public ComplexMatrix(Complex[][] data) {
        super(data, FACTORY_INSTANCE);
    }

    /**
     * Costruttore per creare una matrice di zeri di dimensioni specifiche.
     * Usato internamente per getZero(), identity(), ecc.
     */
    ComplexMatrix(int rows, int cols) {
        super(rows, cols, FACTORY_INSTANCE);
        for (int i = 0; i < rows; i++) {
             java.util.Arrays.fill(this.data[i], Complex.ZERO); 
         }
    }


    // MatrixElement impls

	@Override
    public Complex determinant()
	{
		if (rows != cols) {
            throw new IllegalStateException("Determinant can only be calculated for square matrices.");
        }

        Comparator<Complex> complexComparator = (c1, c2) -> Double.compare(c1.modulus(), c2.modulus());

        return GaussianElimination.determinant(this.data, Complex.ZERO, complexComparator);
    }

    @Override
    public ComplexMatrix inverse()
    {
    	if (rows != cols) {
    		throw new IllegalStateException("Inverse can only be calculated for square matrices.");
    	}

        Comparator<Complex> complexComparator = (c1, c2) -> Double.compare(c1.modulus(), c2.modulus());
        Complex[][] invertedData = GaussJordanElimination.inverse(this.data, Complex.ZERO, Complex.ONE, complexComparator);

        return new ComplexMatrix(invertedData);	
    }

    @Override
    public ComplexVector getRowVector(int row)
    {
    	if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }

        return factory.createVector(Arrays.copyOf(data[row], cols));
	}

	@Override
	public ComplexVector getColumnVector(int col)
	{
        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }

        Complex[] columnData = new Complex[rows];
        for (int i = 0; i < rows; i++) {
            columnData[i] = data[i][col];
        }

        return factory.createVector(columnData);
	}


	// Helper Methods

    public static ComplexMatrix identity(int size)
    {
        if (size <= 0) {
            throw new IllegalArgumentException("Dimension must be positive.");
        }

        ComplexMatrix identityMatrix = new ComplexMatrix(size, size);
        for (int i = 0; i < size; i++) {
             identityMatrix.data[i][i] = Complex.ONE;
        }

        return identityMatrix; 
    }


	// Specific Methods

    public ComplexMatrix conjugateTranspose()
    {
        Complex[][] resultData = new Complex[cols][rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				resultData[j][i] = this.data[i][j].conjugate();
			}
		}

		return new ComplexMatrix(resultData);
    }


   // Java Standard impls

    /**
     * WARNING: This equals method uses epsilon comparisons via Real.isEqual,
     * violating the strict transitivity contract of Object.equals() in standard Java collections.
     */
    @Override
    public final boolean equals(Object other) {
        return (other instanceof ComplexMatrix) && isEqual((ComplexMatrix)other);
    }

    @Override
    public final int hashCode() {
        int result = java.util.Objects.hash(rows, cols);
        result = 31 * result + Arrays.deepHashCode(data);
        return result;
    }

    @Override
    protected Class<Complex> getScalarClass() {
        return Complex.class;
    }
}
