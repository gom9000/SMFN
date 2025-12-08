package net.gommagomma.smfn.math.linearalgebra.complex;

import java.util.Arrays;
import java.util.Comparator;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.AbstractFieldMatrix;

/**
 * Rappresenta una matrice di numeri complessi immutabile.
 * Aderisce all'interfaccia Matrix<Complex, ComplexVector, ComplexMatrix>.
 */
public final class ComplexMatrix
extends AbstractFieldMatrix<Complex, ComplexVector, ComplexMatrix, ComplexMatrixFactory>
{    
	private static final ComplexMatrixFactory FACTORY_INSTANCE = ComplexMatrixFactory.getInstance();


    public ComplexMatrix(Complex[][] data) { super(data, FACTORY_INSTANCE); }
    ComplexMatrix(int rows, int cols) { super(rows, cols, FACTORY_INSTANCE); }


    @Override // SemiringMatrixElement impls
    protected Class<Complex> getScalarClass() {
        return Complex.class;
    }


    @Override  // SemiringMatrixElement impls
    public ComplexVector getRowVector(int row)
    {
    	if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }

        return factory.createVector(Arrays.copyOf(data[row], cols));
	}

	@Override  // SemiringMatrixElement impls
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


    @Override // AbstractFieldMatrix impls
    protected Comparator<Complex> getMagnitudeComparator() {
        return Comparator.comparingDouble(Complex::modulus); 
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
}
