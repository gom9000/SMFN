package net.gommagomma.smfn.math.linearalgebra.rational;

import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.linearalgebra.core.structures.factories.FieldMatrixFactory;


public final class RationalMatrixFactory
implements FieldMatrixFactory<Rational, RationalVector, RationalMatrix>
{
    private static final RationalMatrixFactory INSTANCE = new RationalMatrixFactory();

    private RationalMatrixFactory() {}

    public static RationalMatrixFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public RationalMatrix createMatrix(Rational[][] data) {
        return new RationalMatrix(data);
    }

    @Override
    public RationalVector createVector(Rational[] data) {
        return new RationalVector(data);
    }

    @Override
    public Rational getZeroScalar() {
        return Rational.ZERO;
    }

    @Override
    public Rational getOneScalar() {
        return Rational.ONE;
    }

    @Override
    public RationalMatrix createZeroMatrix(int rows, int cols) {
        return new RationalMatrix(rows, cols);
    }
}
