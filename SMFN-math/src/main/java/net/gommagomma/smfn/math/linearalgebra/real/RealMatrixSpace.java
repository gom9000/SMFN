package net.gommagomma.smfn.math.linearalgebra.real;

import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.AbstractFieldMatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.FieldMatrixSpace;

public final class RealMatrixSpace 
extends AbstractFieldMatrixSpace<Real, RealVector, RealMatrix> 
{
    public RealMatrixSpace(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public String getName() {
        return String.format("Space of %dx%d Real Matrices", rows, cols);
    }

    @Override
    public RealField getScalarStructure() {
        return RealField.getInstance();
    }

    @Override
    public RealVectorSpace getVectorStructure() {
        return RealVectorSpace.getInstance();
    }

    @Override
    public RealMatrix createMatrix(Real[][] data) {
        // Usiamo il validatore ereditato dall'astratta
        validateInputDimensions(data.length, data.length > 0 ? data[0].length : 0);
        return new RealMatrix(data, this);
    }

    @Override
    protected Real[][] createDataArray(int r, int c) {
        return new Real[r][c];
    }

    @Override
    public FieldMatrixSpace<Real, RealVector, RealMatrix> getSpaceOfDimensions(int rows, int cols) {
        return new RealMatrixSpace(rows, cols);
    }
}