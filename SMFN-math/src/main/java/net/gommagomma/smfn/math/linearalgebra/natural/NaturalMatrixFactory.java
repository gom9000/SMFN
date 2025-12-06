package net.gommagomma.smfn.math.linearalgebra.natural;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.numeric.NaturalFactory;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;
import net.gommagomma.smfn.math.linearalgebra.core.structures.factories.SemiringMatrixFactory;


public final class NaturalMatrixFactory
implements SemiringMatrixFactory<Natural, NaturalVector, NaturalMatrix>
{
	private static final NaturalMatrixFactory INSTANCE = new NaturalMatrixFactory();
    private final NaturalVectorFactory vectorFactory = NaturalVectorFactory.getInstance();
    private final NaturalFactory scalarFactory = NaturalFactory.getInstance();


	private NaturalMatrixFactory() {}
	public static NaturalMatrixFactory getInstance() { return INSTANCE; }


	@Override // SemiringMatrixFactory impls
    public SemimoduleVectorFactory<Natural, NaturalVector> getVectorFactory() { return this.vectorFactory; }

	@Override // SemiringMatrixFactory impls
    public NumericFactory<Natural> getScalarFactory() { return this.scalarFactory; }

    @Override // SemiringMatrixFactory impls
    public NaturalMatrix createMatrix(Natural[][] data) {  return new NaturalMatrix(data); }

    @Override // SemiringMatrixFactory impls
    public NaturalMatrix createMatrix(double[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        NaturalFactory scalarFactory = (NaturalFactory) getScalarFactory();
        Natural[][] components = new Natural[rows][cols]; 
        
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            for (int j = 0; j < cols; j++) {
                components[i][j] = scalarFactory.fromDouble(data[i][j]);
            }
        }

        return createMatrix(components);
    }

    @Override // SemiringMatrixFactory impls
    public NaturalMatrix createZeroMatrix(int rows, int cols) {
    	if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive.");
       }
    	return new NaturalMatrix(rows, cols);
    }

//    @Override // SemiringMatrixFactory impls
//    public NaturalMatrix createMatrix(NaturalVector... rowVectors) {
//        if (rowVectors == null || rowVectors.length == 0) {
//             throw new IllegalArgumentException("Row vectors cannot be null or empty.");
//        }
//        return new NaturalMatrix(rowVectors); 
//    }
}
