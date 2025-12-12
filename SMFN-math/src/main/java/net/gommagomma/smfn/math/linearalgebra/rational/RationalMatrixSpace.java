package net.gommagomma.smfn.math.linearalgebra.rational;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.FieldMatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.SemiringMatrixSemimodule;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.VectorSpace;

public final class RationalMatrixSpace
implements FieldMatrixSpace<Rational, RationalVector, RationalMatrix>
{    
    private final int rows;
    private final int cols;


    public RationalMatrixSpace(int rows, int cols) {
    	if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive.");
        }
        this.rows = rows;
        this.cols = cols;
    }


    @Override // AlgebraicStructure impls
    public String getName() {
        return "Rational Matrix Space (Q^mxn)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(RationalMatrix m) {
    	return m.getRows() == this.rows && m.getColumns() == this.cols;
    }


    @Override // SemiringMatrixSemimodule impls
    public int getMatrixRows() { return rows; }

    @Override // SemiringMatrixSemimodule impls
    public int getMatrixColumns() { return cols; }


    @Override // FieldMatrixSpace impls
    public Field<Rational, Natural> getScalarStructure() { return RationalField.getInstance(); }

    @Override // FieldMatrixSpace impls
	public VectorSpace<Rational, RationalVector> getVectorStructure() { return RationalVectorSpace.getInstance(); }


    @Override // MatrixElementFactory impls
    public RationalMatrix createMatrix(Rational[][] data) {
    	if (data == null || data.length != this.rows || (this.rows > 0 && data[0].length != this.cols)) {
    		throw new IllegalArgumentException("Input data dimensions do not match this Space dimensions (" + this.rows + "x" + this.cols + ").");
    	}
        return new RationalMatrix(data, this);
    }

    @Override // MatrixElementFactory impls
    public RationalMatrix createMatrix(double[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Rational[][] components = new Rational[rows][cols]; 
        
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
    public RationalMatrix createMatrix(long[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Rational[][] components = new Rational[rows][cols]; 
        
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
    public RationalMatrix createMatrix(int[][] data) {
        int rows = data.length;
        if (rows == 0) {
            return createZeroMatrix(0, 0); 
        }
        int cols = data[0].length;

        Rational[][] components = new Rational[rows][cols]; 
        
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
    public RationalMatrix createZeroMatrix(int rows, int cols) {
    	if (rows != this.rows || cols != this.cols) {
    		throw new IllegalArgumentException("Requested zero matrix dimensions do not match this Space dimensions (" + this.rows + "x" + this.cols + ").");
    	}
        return new RationalMatrix(rows, cols, this);
    }


	@Override // DimensionalStructure impls
	public SemiringMatrixSemimodule<Rational, RationalVector, RationalMatrix> getSpaceOfDimensions(int rows, int cols) {
		return new RationalMatrixSpace(rows, cols);
	}
}
