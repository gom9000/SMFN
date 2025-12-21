package net.gommagomma.smfn.math.linearalgebra.real;

import java.util.Arrays;
import java.util.Comparator;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.AbstractFieldMatrix;


public final class RealMatrix 
extends AbstractFieldMatrix<Real, RealVector, RealMatrix, RealMatrixSpace>
{
    RealMatrix(Real[][] data, RealMatrixSpace matrixStructure) { super(data, matrixStructure); }
    RealMatrix(int rows, int cols, RealMatrixSpace matrixStructure) { super(rows, cols, matrixStructure);}


    @Override // AbstractSemiringMatrix impls
    protected Class<Real> getScalarClass() { return Real.class; }


    @Override // SemiringMatrixElement impls
    public RealVector getRowVector(int row)
    {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }

        return matrixStructure.getVectorStructure().createVector(Arrays.copyOf(data[row], cols));
    }

    @Override // SemiringMatrixElement impls
    public RealVector getColumnVector(int col)
    {
        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }

        Real[] columnData = new Real[rows];
        for (int i = 0; i < rows; i++) {
            columnData[i] = data[i][col];
        }

        return matrixStructure.getVectorStructure().createVector(columnData);
    }


    @Override // AbstractFieldMatrix impls
    protected Comparator<Real> getMagnitudeComparator() {
    	return (r1, r2) -> {
            double abs1 = r1.norm().getValue();
            double abs2 = r2.norm().getValue();            
            return Double.compare(abs1, abs2);
        };
    }
	@Override
	public RealMatrix getOne() {
        return null;
	}
}
