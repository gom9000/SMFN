package net.gommagomma.smfn.math.linearalgebra.natural;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.AbstractSemiringMatrix;


/**
 * Rappresenta una matrice i cui elementi sono numeri naturali (Natural).
 */
public final class NaturalMatrix 
extends AbstractSemiringMatrix<Natural, NaturalVector, NaturalMatrix, NaturalMatrixSemimodule>
{
    NaturalMatrix(Natural[][] data, NaturalMatrixSemimodule matrixStructure) { super(data, matrixStructure); }
    NaturalMatrix(int rows, int cols, NaturalMatrixSemimodule matrixStructure) { super(rows, cols, matrixStructure); }


    @Override // AbstractSemiringMatrix impls
    protected Class<Natural> getScalarClass() { return Natural.class; }


    @Override // SemiringMatrixElement impls
    public NaturalVector getRowVector(int row)
    {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }
        return matrixStructure.getVectorStructure().createVector(Arrays.copyOf(this.data[row], this.cols));
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
        return matrixStructure.getVectorStructure().createVector(columnData);
    }
}
