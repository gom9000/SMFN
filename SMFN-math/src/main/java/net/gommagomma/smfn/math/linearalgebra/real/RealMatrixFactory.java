package net.gommagomma.smfn.math.linearalgebra.real;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.structures.factories.MatrixFactory;


public class RealMatrixFactory
implements MatrixFactory<Real, RealVector, RealMatrix>
{
    private static final RealMatrixFactory INSTANCE = new RealMatrixFactory();

    private RealMatrixFactory() {}

    public static RealMatrixFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public RealMatrix createMatrix(Real[][] data) {
        return new RealMatrix(data);
    }

    @Override
    public RealVector createVector(Real[] data) {
        return new RealVector(data);
    }

    @Override
    public Real getZeroScalar() {
        return Real.ZERO;
    }

    @Override
    public Real getOneScalar() {
        return Real.ONE;
    }

    @Override
    public RealMatrix createZeroMatrix(int rows, int cols) {
        return new RealMatrix(rows, cols);
    }
}
