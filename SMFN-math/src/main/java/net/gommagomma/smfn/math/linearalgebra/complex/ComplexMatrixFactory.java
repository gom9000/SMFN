package net.gommagomma.smfn.math.linearalgebra.complex;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.linearalgebra.core.structures.factories.MatrixFactory;


public class ComplexMatrixFactory
implements MatrixFactory<Complex, ComplexVector, ComplexMatrix>
{
    private static final ComplexMatrixFactory INSTANCE = new ComplexMatrixFactory();

    private ComplexMatrixFactory() {}

    public static ComplexMatrixFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public ComplexMatrix createMatrix(Complex[][] data) {
        return new ComplexMatrix(data);
    }

    @Override
    public ComplexVector createVector(Complex[] data) {
        return new ComplexVector(data);
    }

    @Override
    public Complex getZeroScalar() {
        return Complex.ZERO;
    }

    @Override
    public Complex getOneScalar() {
        return Complex.ONE;
    }

    @Override
    public ComplexMatrix createZeroMatrix(int rows, int cols) {
        return new ComplexMatrix(rows, cols);
    }
}
