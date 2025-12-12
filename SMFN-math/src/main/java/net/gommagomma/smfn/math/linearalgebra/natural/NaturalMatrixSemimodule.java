package net.gommagomma.smfn.math.linearalgebra.natural;

import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.Semimodule;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.SemiringMatrixSemimodule;

/**
 * Rappresenta lo Spazio delle Matrici M x N sui Numeri Naturali (NaturalMatrixSpace).
 * Tutte le matrici in questo spazio hanno dimensioni fisse e utilizzano Natural come scalari.
 */
public final class NaturalMatrixSemimodule 
implements SemiringMatrixSemimodule<Natural, NaturalVector, NaturalMatrix>
{
    private final int rows;
    private final int cols;


    public NaturalMatrixSemimodule(int rows, int cols) {
    	if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive.");
        }
        this.rows = rows;
        this.cols = cols;
    }


    @Override // AlgebraicStructure impls
    public String getName() {
    	return "Natural Matrix Semiring (N^mxn)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(NaturalMatrix m) {
        return m.getRows() == this.rows && m.getColumns() == this.cols;
    }


    @Override // SemiringMatrixSemimodule impls
    public int getMatrixRows() {
        return this.rows;
    }

    @Override // SemiringMatrixSemimodule impls
    public int getMatrixColumns() {
        return this.cols;
    }

    @Override // SemiringMatrixSemimodule impls
    public Semiring<Natural> getScalarStructure() { return NaturalSemiring.getInstance(); }

	@Override // SemiringMatrixSemimodule impls
	public Semimodule<Natural, NaturalVector> getVectorStructure() { return NaturalSemimodule.getInstance(); }


    @Override // MatrixElementFactory impls
    public NaturalMatrix createMatrix(Natural[][] data) {
    	if (data == null || data.length != this.rows || (this.rows > 0 && data[0].length != this.cols)) {
    		throw new IllegalArgumentException("Input data dimensions do not match this Space dimensions (" + this.rows + "x" + this.cols + ").");
    	}
    	return new NaturalMatrix(data, this);
    }

    @Override // MatrixElementFactory impls
    public NaturalMatrix createMatrix(double[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Natural[][] components = new Natural[rows][cols]; 
        
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            for (int j = 0; j < cols; j++) {
                components[i][j] = getScalarStructure().of(data[i][j]);
            }
        }

        return createMatrix(components);
    }

    @Override // MatrixElementFactory impls
    public NaturalMatrix createMatrix(long[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Natural[][] components = new Natural[rows][cols]; 
        
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            for (int j = 0; j < cols; j++) {
                components[i][j] = getScalarStructure().of(data[i][j]); 
            }
        }

        return createMatrix(components);
    }

    @Override // MatrixElementFactory impls
    public NaturalMatrix createMatrix(int[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Natural[][] components = new Natural[rows][cols]; 
        
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            for (int j = 0; j < cols; j++) {
                components[i][j] = getScalarStructure().of(data[i][j]); 
            }
        }

        return createMatrix(components);
    }

    @Override // MatrixElementFactory impls
    public NaturalMatrix createZeroMatrix(int rows, int cols) {
    	if (rows != this.rows || cols != this.cols) {
    		throw new IllegalArgumentException("Requested zero matrix dimensions do not match this Space dimensions (" + this.rows + "x" + this.cols + ").");
    	}
    	return new NaturalMatrix(rows, cols, this);
    }


	@Override // DimensionalStructure impls
	public SemiringMatrixSemimodule<Natural, NaturalVector, NaturalMatrix> getSpaceOfDimensions(int rows, int cols) {
		return new NaturalMatrixSemimodule(rows, cols);
	}
}
