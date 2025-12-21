package net.gommagomma.smfn.math.linearalgebra.signedint;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.AbstractRingMatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.RingMatrixModule;

public final class SignedIntMatrixModule 
extends AbstractRingMatrixSpace<SignedInt, SignedIntVector, SignedIntMatrix>
{
    public SignedIntMatrixModule(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public String getName() {
        return "SignedInt Matrix Ring (Z^" + rows + "x" + cols + ")";
    }

    @Override
    public IntegerRing getScalarStructure() {
        return IntegerRing.getInstance();
    }

    @Override
    public SignedIntModule getVectorStructure() {
        return SignedIntModule.getInstance();
    }

    @Override
    public SignedIntMatrix createMatrix(SignedInt[][] data) {
        validateInputDimensions(data.length, data.length > 0 ? data[0].length : 0);
        return new SignedIntMatrix(data, this);
    }

    @Override
    protected SignedInt[][] createDataArray(int r, int c) {
        return new SignedInt[r][c];
    }

    @Override
    public RingMatrixModule<SignedInt, SignedIntVector, SignedIntMatrix> getSpaceOfDimensions(int rows, int cols) {
        return new SignedIntMatrixModule(rows, cols);
    }
}