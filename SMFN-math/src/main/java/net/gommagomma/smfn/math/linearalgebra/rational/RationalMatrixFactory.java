package net.gommagomma.smfn.math.linearalgebra.rational;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.numeric.RationalFactory;
import net.gommagomma.smfn.math.linearalgebra.core.factories.FieldMatrixFactory;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;


public final class RationalMatrixFactory
implements FieldMatrixFactory<Rational, RationalVector, RationalMatrix>
{
    private static final RationalMatrixFactory INSTANCE = new RationalMatrixFactory();
    private final RationalVectorFactory vectorFactory = RationalVectorFactory.getInstance();
    private final RationalFactory scalarFactory = RationalFactory.getInstance();


    private RationalMatrixFactory() {}
    public static RationalMatrixFactory getInstance() { return INSTANCE; }


    @Override // SemiringMatrixFactory impls
    public SemimoduleVectorFactory<Rational, RationalVector> getVectorFactory() { return this.vectorFactory; }

	@Override // SemiringMatrixFactory impls
    public NumericFactory<Rational> getScalarFactory() { return this.scalarFactory; }


    @Override // SemiringMatrixFactory impls
    public RationalMatrix createMatrix(Rational[][] data) {
        return new RationalMatrix(data);
    }

    @Override // SemiringMatrixFactory impls
    public RationalMatrix createMatrix(double[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        RationalFactory scalarFactory = (RationalFactory) getScalarFactory();
        Rational[][] components = new Rational[rows][cols]; 
        
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
    public RationalMatrix createMatrix(long[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        RationalFactory scalarFactory = (RationalFactory) getScalarFactory();
        Rational[][] components = new Rational[rows][cols]; 
        
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
    public RationalMatrix createMatrix(int[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        RationalFactory scalarFactory = (RationalFactory) getScalarFactory();
        Rational[][] components = new Rational[rows][cols]; 
        
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
    public RationalMatrix createZeroMatrix(int rows, int cols) {
        return new RationalMatrix(rows, cols);
    }
}
