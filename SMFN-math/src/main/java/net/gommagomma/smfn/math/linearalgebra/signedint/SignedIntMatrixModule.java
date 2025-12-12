package net.gommagomma.smfn.math.linearalgebra.signedint;

import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.Module;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.RingMatrixModule;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.SemiringMatrixSemimodule;


public class SignedIntMatrixModule
implements RingMatrixModule<SignedInt, SignedIntVector, SignedIntMatrix>
{
    private final int rows;
    private final int cols;


    public SignedIntMatrixModule(int rows, int cols) {
    	if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive.");
        }
        this.rows = rows;
        this.cols = cols;
    }


    @Override // AlgebraicStructure impls
    public String getName() {
    	return "SignedInt Matrix Ring (Z^mxn)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(SignedIntMatrix m) {
        return m.getRows() == this.rows && m.getColumns() == this.cols;
    }


    @Override // SemiringMatrixSemimodule impls 
    public int getMatrixRows() { return this.rows; }

    @Override // SemiringMatrixSemimodule impls 
    public int getMatrixColumns() { return this.cols; }


    @Override // RingMatrixModule impls
    public Ring<SignedInt> getScalarStructure() { return IntegerRing.getInstance(); }

	@Override // RingMatrixModule impls
	public Module<SignedInt, SignedIntVector> getVectorStructure() { return SignedIntModule.getInstance(); }


    @Override // MatrixElementFactory impls
    public SignedIntMatrix createMatrix(SignedInt[][] data) {
    	if (data == null || data.length != this.rows || (this.rows > 0 && data[0].length != this.cols)) {
    		throw new IllegalArgumentException("Input data dimensions do not match this Space dimensions (" + this.rows + "x" + this.cols + ").");
    	}
    	return new SignedIntMatrix(data, this);
    }

    @Override // SemiringMatrixFactory impls
    public SignedIntMatrix createMatrix(double[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        SignedInt[][] components = new SignedInt[rows][cols]; 
        
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
    public SignedIntMatrix createMatrix(long[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        SignedInt[][] components = new SignedInt[rows][cols]; 
        
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
    public SignedIntMatrix createMatrix(int[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        SignedInt[][] components = new SignedInt[rows][cols]; 
        
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
    public SignedIntMatrix createZeroMatrix(int rows, int cols) {
    	if (rows != this.rows || cols != this.cols) {
    		throw new IllegalArgumentException("Requested zero matrix dimensions do not match this Space dimensions (" + this.rows + "x" + this.cols + ").");
    	}
        return new SignedIntMatrix(rows, cols, this);
    }


	@Override // DimensionalStructure impls
	public SemiringMatrixSemimodule<SignedInt, SignedIntVector, SignedIntMatrix> getSpaceOfDimensions(int rows,	int cols) {
		return new SignedIntMatrixModule(rows, cols);
	}
}
