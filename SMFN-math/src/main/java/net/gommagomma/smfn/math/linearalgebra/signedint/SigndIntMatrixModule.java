package net.gommagomma.smfn.math.linearalgebra.signedint;

import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.RingMatrixSpace;


public class SigndIntMatrixModule
implements RingMatrixSpace<SignedInt, SignedIntVector, SignedIntMatrix>
{
    private final int rows;
    private final int cols;


    public SigndIntMatrixModule(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public Ring<SignedInt> getScalarStructure() {
        return IntegerRing.getInstance();
    }

    @Override
    public int getMatrixRows() {
        return this.rows;
    }

    @Override
    public int getMatrixColumns() {
        return this.cols;
    }

    @Override
    public String getName() {
    	return "SignedInt Matrix Ring (Z^mxn)";
    }

    @Override
    public boolean contains(SignedIntMatrix m) {
        return m.getRows() == this.rows && m.getColumns() == this.cols;
    }
}
