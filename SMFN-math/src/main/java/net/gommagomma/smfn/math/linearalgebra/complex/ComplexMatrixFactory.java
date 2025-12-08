package net.gommagomma.smfn.math.linearalgebra.complex;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.ComplexFactory;
import net.gommagomma.smfn.math.linearalgebra.core.factories.FieldMatrixFactory;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;


public final class ComplexMatrixFactory
implements FieldMatrixFactory<Complex, ComplexVector, ComplexMatrix>
{
    private static final ComplexMatrixFactory INSTANCE = new ComplexMatrixFactory();
    private final ComplexVectorFactory vectorFactory = ComplexVectorFactory.getInstance();
    private final ComplexFactory scalarFactory = ComplexFactory.getInstance();


    private ComplexMatrixFactory() {}
    public static ComplexMatrixFactory getInstance() { return INSTANCE; }


    @Override // SemiringMatrixFactory impls
    public SemimoduleVectorFactory<Complex, ComplexVector> getVectorFactory() { return this.vectorFactory; }

	@Override // SemiringMatrixFactory impls
    public NumericFactory<Complex> getScalarFactory() { return this.scalarFactory; }


    @Override // SemiringMatrixFactory impls
    public ComplexMatrix createMatrix(Complex[][] data) {
        return new ComplexMatrix(data);
    }

    @Override // SemiringMatrixFactory impls
    public ComplexMatrix createMatrix(double[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        ComplexFactory scalarFactory = (ComplexFactory) getScalarFactory();
        Complex[][] components = new Complex[rows][cols]; 
        
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            for (int j = 0; j < cols; j++) {
                components[i][j] = scalarFactory.of(data[i][j]);
            }
        }

        return createMatrix(components);
    }

    @Override // SemiringMatrixFactory impls
    public ComplexMatrix createMatrix(long[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        ComplexFactory scalarFactory = (ComplexFactory) getScalarFactory();
        Complex[][] components = new Complex[rows][cols]; 
        
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            for (int j = 0; j < cols; j++) {
                components[i][j] = scalarFactory.of(data[i][j]); 
            }
        }

        return createMatrix(components);
    }

    @Override // SemiringMatrixFactory impls
    public ComplexMatrix createMatrix(int[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        ComplexFactory scalarFactory = (ComplexFactory) getScalarFactory();
        Complex[][] components = new Complex[rows][cols]; 
        
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            for (int j = 0; j < cols; j++) {
                components[i][j] = scalarFactory.of(data[i][j]); 
            }
        }

        return createMatrix(components);
    }

    @Override // SemiringMatrixFactory impls
    public ComplexMatrix createZeroMatrix(int rows, int cols) {
        return new ComplexMatrix(rows, cols);
    }
}
