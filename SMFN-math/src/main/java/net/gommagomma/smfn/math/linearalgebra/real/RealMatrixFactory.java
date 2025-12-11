package net.gommagomma.smfn.math.linearalgebra.real;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.core.factories.FieldMatrixFactory;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;


public final class RealMatrixFactory
implements FieldMatrixFactory<Real, RealVector, RealMatrix>
{
    private static final RealMatrixFactory INSTANCE = new RealMatrixFactory();
    private final RealVectorFactory vectorFactory = RealVectorFactory.getInstance();
    private final RealField scalarFactory = RealField.getInstance();


    private RealMatrixFactory() {}
    public static RealMatrixFactory getInstance() { return INSTANCE; }


    @Override // SemiringMatrixFactory impls
    public SemimoduleVectorFactory<Real, RealVector> getVectorFactory() { return this.vectorFactory; }

	@Override // SemiringMatrixFactory impls
    public NumericFactory<Real> getScalarFactory() { return this.scalarFactory; }


    @Override // SemiringMatrixFactory impls
    public RealMatrix createMatrix(Real[][] data) {
        return new RealMatrix(data);
    }

    @Override // SemiringMatrixFactory impls
    public RealMatrix createMatrix(double[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Real[][] components = new Real[rows][cols]; 
        
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
    public RealMatrix createMatrix(long[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Real[][] components = new Real[rows][cols]; 
        
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
    public RealMatrix createMatrix(int[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Real[][] components = new Real[rows][cols]; 
        
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
    public RealMatrix createZeroMatrix(int rows, int cols) {
        return new RealMatrix(rows, cols);
    }
}
