package net.gommagomma.smfn.math.linearalgebra.complex;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.AbstractFieldMatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.FieldMatrixSpace;

public final class ComplexMatrixSpace 
extends AbstractFieldMatrixSpace<Complex, ComplexVector, ComplexMatrix>
{
    public ComplexMatrixSpace(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public ComplexField getScalarStructure() { 
        return ComplexField.getInstance(); 
    }

    @Override
    public ComplexVectorSpace getVectorStructure() { 
        return ComplexVectorSpace.getInstance(); 
    }

    @Override
    public ComplexMatrix createMatrix(Complex[][] data) {
        // Possiamo usare il validatore dell'astratta anche qui!
        validateInputDimensions(data.length, data.length > 0 ? data[0].length : 0);
        return new ComplexMatrix(data, this);
    }

    @Override
    protected Complex[][] createDataArray(int r, int c) {
        return new Complex[r][c];
    }

    @Override
    public String getName() {
        return "Complex Matrix Space (C^" + rows + "x" + cols + ")";
    }

    @Override
    public FieldMatrixSpace<Complex, ComplexVector, ComplexMatrix> getSpaceOfDimensions(int rows, int cols) {
        return new ComplexMatrixSpace(rows, cols);
    }
}
