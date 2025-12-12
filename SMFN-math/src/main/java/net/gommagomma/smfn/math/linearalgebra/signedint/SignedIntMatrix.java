package net.gommagomma.smfn.math.linearalgebra.signedint;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.AbstractRingMatrix;

/**
 * Rappresenta una matrice i cui elementi sono numeri interi relativi (SignedInt).
 */
public final class SignedIntMatrix
extends AbstractRingMatrix<SignedInt, SignedIntVector, SignedIntMatrix, SignedIntMatrixModule>
{
    SignedIntMatrix(SignedInt[][] data, SignedIntMatrixModule matrixStructure) { super(data, matrixStructure); }
    SignedIntMatrix(int rows, int cols, SignedIntMatrixModule matrixStructure) { super(rows, cols, matrixStructure); }


    @Override // AbstractSemiringMatrix impls
    protected Class<SignedInt> getScalarClass() { return SignedInt.class; }


    @Override // SemiringMatrixElement impls
    public SignedIntVector getRowVector(int row)
    {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }

        return matrixStructure.getVectorStructure().createVector(Arrays.copyOf(data[row], cols));
    }

    @Override // SemiringMatrixElement impls
    public SignedIntVector getColumnVector(int col)
    {
        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }

        SignedInt[] columnData = new SignedInt[rows];
        for (int i = 0; i < rows; i++) {
            columnData[i] = data[i][col];
        }

        return matrixStructure.getVectorStructure().createVector(columnData);
    }
}
