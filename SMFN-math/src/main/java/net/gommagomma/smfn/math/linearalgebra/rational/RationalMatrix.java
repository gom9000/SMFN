package net.gommagomma.smfn.math.linearalgebra.rational;

import java.util.Arrays;
import java.util.Comparator;

import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.AbstractFieldMatrix;

/**
 * Rappresenta una matrice di numeri razionali immutabile, fornendo precisione esatta.
 * Aderisce all'interfaccia Matrix<Rational, RationalVector, RationalMatrix>.
 */
public final class RationalMatrix
extends AbstractFieldMatrix<Rational, RationalVector, RationalMatrix, RationalMatrixSpace>
{    
	RationalMatrix(Rational[][] data, RationalMatrixSpace matrixStructure) { super(data, matrixStructure); }
	RationalMatrix(int rows, int cols, RationalMatrixSpace matrixStructure) { super(rows, cols, matrixStructure); }


    @Override // AbstractSemiringMatrix impls
    protected Class<Rational> getScalarClass() { return Rational.class; }


    @Override // SemiringMatrixElement impls
    public RationalVector getRowVector(int row)
    {
    	if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }

        return matrixStructure.getVectorStructure().createVector(Arrays.copyOf(data[row], cols));
	}

	@Override // SemiringMatrixElement impls
	public RationalVector getColumnVector(int col)
	{
		if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }

        Rational[] columnData = new Rational[rows];
        for (int i = 0; i < rows; i++) {
            columnData[i] = data[i][col];
        }

        return matrixStructure.getVectorStructure().createVector(columnData);
	}


    @Override // AbstractFieldMatrix impls
    protected Comparator<Rational> getMagnitudeComparator() {
        return Comparator.naturalOrder(); 
    }
}
