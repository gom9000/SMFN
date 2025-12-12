package net.gommagomma.smfn.math.linearalgebra.complex;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.FieldMatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.VectorSpace;

public final class ComplexMatrixSpace
implements FieldMatrixSpace<Complex, ComplexVector, ComplexMatrix>
{
    private final int rows;
    private final int cols;


    public ComplexMatrixSpace(int rows, int cols) {
    	if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive.");
        }
        this.rows = rows;
        this.cols = cols;
    }


    @Override // AlgebraicStructure impls
    public String getName() {
        return "Complex Matrix Space (C^mxn)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(ComplexMatrix m) {
        return (m != null);
    }


    @Override // SemiringMatrixSemimodule impls 
    public int getMatrixRows() { return rows; }

    @Override // SemiringMatrixSemimodule impls
    public int getMatrixColumns() { return cols; }


    @Override // FieldMatrixSpace impls
    public Field<Complex, Natural> getScalarStructure() { return ComplexField.getInstance(); }

    @Override // FieldMatrixSpace impls
	public VectorSpace<Complex, ComplexVector> getVectorStructure() { return ComplexVectorSpace.getInstance(); }


    @Override // MatrixElementFactory impls
    public ComplexMatrix createMatrix(Complex[][] data) {
    	if (data == null || data.length != this.rows || (this.rows > 0 && data[0].length != this.cols)) {
    		throw new IllegalArgumentException("Input data dimensions do not match this Space dimensions (" + this.rows + "x" + this.cols + ").");
    	}
        return new ComplexMatrix(data, this);
    }

    @Override // MatrixElementFactory impls
    public ComplexMatrix createMatrix(double[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Complex[][] components = new Complex[rows][cols]; 
        
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
    public ComplexMatrix createMatrix(long[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Complex[][] components = new Complex[rows][cols]; 
        
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
    public ComplexMatrix createMatrix(int[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Complex[][] components = new Complex[rows][cols]; 
        
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
    public ComplexMatrix createZeroMatrix(int rows, int cols) {
    	if (rows != this.rows || cols != this.cols) {
    		throw new IllegalArgumentException("Requested zero matrix dimensions do not match this Space dimensions (" + this.rows + "x" + this.cols + ").");
    	}
        return new ComplexMatrix(rows, cols, this);
    }


	@Override // DimensionalStructure impls
	public FieldMatrixSpace<Complex, ComplexVector, ComplexMatrix> getSpaceOfDimensions(int rows, int cols) {
		return new ComplexMatrixSpace(rows, cols);
	}
}
