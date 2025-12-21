package net.gommagomma.smfn.math.linearalgebra.rational;

import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.AbstractFieldMatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.FieldMatrixSpace;

public final class RationalMatrixSpace 
extends AbstractFieldMatrixSpace<Rational, RationalVector, RationalMatrix>
{
    public RationalMatrixSpace(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public String getName() {
        return "Rational Matrix Space (Q^" + rows + "x" + cols + ")";
    }

    @Override
    public RationalField getScalarStructure() {
        return RationalField.getInstance();
    }

    @Override
    public RationalVectorSpace getVectorStructure() {
        return RationalVectorSpace.getInstance();
    }

    @Override
    public RationalMatrix createMatrix(Rational[][] data) {
        validateInputDimensions(data.length, data.length > 0 ? data[0].length : 0);
        return new RationalMatrix(data, this);
    }

    @Override
    protected Rational[][] createDataArray(int r, int c) {
        return new Rational[r][c];
    }

    @Override
    public FieldMatrixSpace<Rational, RationalVector, RationalMatrix> getSpaceOfDimensions(int rows, int cols) {
        return new RationalMatrixSpace(rows, cols);
    }
}