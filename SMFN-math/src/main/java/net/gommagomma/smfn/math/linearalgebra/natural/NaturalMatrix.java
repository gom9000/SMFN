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


    protected NaturalMatrix(Natural[][] data) {
        super(data, FACTORY_INSTANCE);
    }

    protected NaturalMatrix(int rows, int cols) {
    	super(rows, cols, FACTORY_INSTANCE);
//    	for (int i = 0; i < rows; i++) {
//    		Arrays.fill(this.data[i], factory.getScalarFactory().zero());
//    	}
    }


    @Override // AbstractSemiringMatrix impls
    protected Class<Natural> getScalarClass() {
        return Natural.class;
    }


    @Override // SemiringMatrixElement impls
    public NaturalVector getRowVector(int row)
    {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }
        return factory.getVectorFactory().createVector(Arrays.copyOf(this.data[row], this.cols));
    }

    @Override // SemiringMatrixElement impls
    public NaturalVector getColumnVector(int col)
    {
        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }

        Natural[] columnData = new Natural[rows];
        for (int i = 0; i < rows; i++) {
            columnData[i] = data[i][col];
        }
        return factory.getVectorFactory().createVector(columnData);
    }
}
