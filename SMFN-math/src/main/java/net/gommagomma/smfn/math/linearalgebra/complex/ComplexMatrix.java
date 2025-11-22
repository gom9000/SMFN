package net.gommagomma.smfn.math.linearalgebra.complex;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.linearalgebra.core.Matrix;

/**
 * Rappresenta una matrice di numeri complessi immutabile.
 * Aderisce all'interfaccia Matrix<Complex, ComplexVector, ComplexMatrix>.
 */
public final class ComplexMatrix
implements Matrix<Complex, ComplexVector, ComplexMatrix>
{    
    private final Complex[][] data;
    private final int rows;
    private final int cols;

    // --- Costruttori ---

    /**
     * Costruttore principale che accetta un array 2D di Complex. 
     * DEDUCE le dimensioni dall'array e verifica che sia rettangolare.
     */
    public ComplexMatrix(Complex[][] data) {
        if (data == null || data.length == 0 || data[0].length == 0) { // Controlli corretti
            throw new IllegalArgumentException("Matrix data cannot be null or empty.");
        }
        
        this.rows = data.length;
        this.cols = data[0].length; // Dedotto dalla lunghezza della prima riga
        
        this.data = new Complex[rows][cols]; // Inizializza l'array interno

        // Copia difensiva riga per riga e verifica la consistenza delle dimensioni
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                 throw new IllegalArgumentException("All rows must have the same number of columns.");
            }
            this.data[i] = Arrays.copyOf(data[i], cols);
        }
    }

    // --- Implementazioni di AlgebraicElement (isEqual, copy, getZero, getOne) ---

    @Override
    public boolean isEqual(ComplexMatrix other) {
        if (this.rows != other.rows || this.cols != other.cols) return false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Usa isEqual di Complex (epsilon-based)
                if (!this.data[i][j].isEqual(other.data[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getRows() {
        return this.rows;
    }

    @Override
    public int getColumns() {
        return this.cols;
    }

    @Override 
    public Complex get(int row, int col) { 
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Indices out of bounds: row=" + row + ", col=" + col);
        }
        return this.data[row][col];
    }

    @Override public ComplexMatrix copy() { return this; }

	// --- Implementazioni Aritmetiche (dall'interfaccia Matrix) ---

	@Override
	public ComplexMatrix add(ComplexMatrix other) {
		if (this.rows != other.rows || this.cols != other.cols) throw new IllegalArgumentException("Dimensions must match.");
		Complex[][] resultData = new Complex[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				resultData[i][j] = this.data[i][j].add(other.data[i][j]);
			}
		}
		return new ComplexMatrix(resultData);
	}
    
    public ComplexMatrix negate() {
        Complex[][] resultData = new Complex[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				resultData[i][j] = this.data[i][j].negate();
			}
		}
		return new ComplexMatrix(resultData);
    }

	@Override public ComplexMatrix subtract(ComplexMatrix other) { return this.add(other.negate()); }

	@Override
	public ComplexMatrix multiply(ComplexMatrix other) {
		if (this.cols != other.rows) throw new IllegalArgumentException("Dimensions incompatible for multiplication.");
		Complex[][] resultData = new Complex[this.rows][other.cols];
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < other.cols; j++) {
				Complex sum = Complex.ZERO;
				for (int k = 0; k < this.cols; k++) {
					sum = sum.add(this.data[i][k].multiply(other.data[k][j]));
				}
				resultData[i][j] = sum;
			}
		}
		return new ComplexMatrix(resultData);
	}
    
	@Override
	public ComplexVector multiply(ComplexVector vector) {
        if (this.cols != vector.dimension()) throw new IllegalArgumentException("Matrix columns must match vector dimension.");
        Complex[] resultData = new Complex[this.rows];
        for (int i = 0; i < this.rows; i++) {
            Complex sum = Complex.ZERO;
            for (int j = 0; j < this.cols; j++) {
                sum = sum.add(this.data[i][j].multiply(vector.get(j)));
            }
            resultData[i] = sum;
        }
		return new ComplexVector(resultData);
	}

	@Override
	public ComplexMatrix transpose() {
		Complex[][] resultData = new Complex[cols][rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				resultData[j][i] = this.data[i][j];
			}
		}
		return new ComplexMatrix(resultData);
	}
    
    // NOTA: Per l'algebra lineare complessa, spesso serve la trasposta coniugata (Hermitian transpose/adjoint)
    public ComplexMatrix conjugateTranspose() {
        Complex[][] resultData = new Complex[cols][rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				resultData[j][i] = this.data[i][j].conjugate(); // Usa il coniugato
			}
		}
		return new ComplexMatrix(resultData);
    }

    @Override
    public ComplexVector getRowVector(int row) {
        if (row < 0 || row >= rows) throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
		return new ComplexVector(Arrays.copyOf(data[row], cols));
	}

	@Override
	public ComplexVector getColumnVector(int col) {
        if (col < 0 || col >= cols) throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        Complex[] columnData = new Complex[rows];
		for (int i = 0; i < rows; i++) {
			columnData[i] = data[i][col];
		}
		return new ComplexVector(columnData);
	}

    // --- Metodi Complessi (Determinante, Inversa) ---
    // Questi metodi sono placeholder per ora, richiedono algoritmi avanzati.
    @Override
    public Complex determinant() { throw new UnsupportedOperationException("Determinant calculation not yet implemented."); }

    @Override
    public ComplexMatrix inverse() { throw new UnsupportedOperationException("Matrix inversion not yet implemented."); }

    // --- Standard Java impls ---
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rows).append("x").append(cols).append(" Complex Matrix:\n");
        for (int i = 0; i < rows; i++) {
            sb.append(Arrays.toString(data[i])).append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public final boolean equals(Object other) {
        return (other instanceof ComplexMatrix) && isEqual((ComplexMatrix)other);
    }
    
    @Override
    public final int hashCode() {
        int result = java.util.Objects.hash(rows, cols);
        result = 31 * result + Arrays.deepHashCode(data);
        return result;
    }
}
