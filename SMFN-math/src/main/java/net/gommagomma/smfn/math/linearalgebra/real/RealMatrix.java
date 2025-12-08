package net.gommagomma.smfn.math.linearalgebra.real;

import java.util.Arrays;
import java.util.Comparator;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.AbstractFieldMatrix;


public final class RealMatrix 
extends AbstractFieldMatrix<Real, RealVector, RealMatrix, RealMatrixFactory>
{
    private static final RealMatrixFactory FACTORY_INSTANCE = RealMatrixFactory.getInstance();


    public RealMatrix(Real[][] data) { super(data, FACTORY_INSTANCE); }
    RealMatrix(int rows, int cols) { super(rows, cols, FACTORY_INSTANCE);}


    @Override // AbstractSemiringMatrix impls
    protected Class<Real> getScalarClass() { return Real.class; }


    @Override // SemiringMatrixElement impls
    public RealVector getRowVector(int row)
    {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }

        return factory.createVector(Arrays.copyOf(data[row], cols));
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

        return factory.createVector(columnData);
    }


    @Override // AbstractFieldMatrix impls
    protected Comparator<Real> getMagnitudeComparator() {
    	return (r1, r2) -> {
            double abs1 = r1.norm().getValue();
            double abs2 = r2.norm().getValue();            
            return Double.compare(abs1, abs2);
        };
    }
}
