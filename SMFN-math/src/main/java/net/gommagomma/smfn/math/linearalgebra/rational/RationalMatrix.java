package net.gommagomma.smfn.math.linearalgebra.rational;

import java.util.Arrays;
import java.util.Comparator;

import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.linearalgebra.core.MatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.algorithms.GaussJordanElimination;
import net.gommagomma.smfn.math.linearalgebra.core.algorithms.GaussianElimination;

/**
 * Rappresenta una matrice di numeri razionali immutabile, fornendo precisione esatta.
 * Aderisce all'interfaccia Matrix<Rational, RationalVector, RationalMatrix>.
 */
public final class RationalMatrix
implements MatrixElement<Rational, RationalVector, RationalMatrix>
{    
    private final Rational[][] data;
    private final int rows;
    private final int cols;

    // --- Costruttori ---

    /**
     * Costruttore principale che accetta un array 2D di Rational. 
     * DEDUCE le dimensioni dall'array e verifica che sia rettangolare.
     * Esegue una copia difensiva per garantire l'immutabilità.
     * 
     * @param data L'array 2D dei componenti.
     */
    public RationalMatrix(Rational[][] data) {
        if (data == null || data.length == 0 || data[0].length == 0) {
            throw new IllegalArgumentException("Matrix data cannot be null or empty.");
        }
        
        this.rows = data.length;
        this.cols = data[0].length; // Dedotto dalla lunghezza della prima riga
        
        this.data = new Rational[rows][cols]; // Inizializza l'array interno

        // Copia difensiva riga per riga e verifica la consistenza delle dimensioni
        for (int i = 0; i < rows; i++) { // Loop corretto
            if (data[i].length != cols) {
                 throw new IllegalArgumentException("All rows must have the same number of columns.");
            }
            this.data[i] = Arrays.copyOf(data[i], cols);
        }
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
    public Rational get(int row, int col) { 
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Indices out of bounds: row=" + row + ", col=" + col);
        }
        return this.data[row][col];
    }

    // --- Implementazioni di AlgebraicElement (isEqual, copy, getZero, getOne) ---

    @Override
    public boolean isEqual(RationalMatrix other) {
        if (this.rows != other.rows || this.cols != other.cols) return false;
        // Usa isEqual di Rational (esatto, non epsilon-based)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!this.data[i][j].isEqual(other.data[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override public RationalMatrix copy() { return new RationalMatrix(this.data); }

	// --- Implementazioni Aritmetiche (dall'interfaccia Matrix) ---

	@Override
	public RationalMatrix add(RationalMatrix other) {
		if (this.rows != other.rows || this.cols != other.cols) throw new IllegalArgumentException("Dimensions must match.");
		Rational[][] resultData = new Rational[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				resultData[i][j] = this.data[i][j].add(other.data[i][j]);
			}
		}
		return new RationalMatrix(resultData);
	}
    
    public RationalMatrix negate() {
        Rational[][] resultData = new Rational[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				resultData[i][j] = this.data[i][j].negate();
			}
		}
		return new RationalMatrix(resultData);
    }

	@Override public RationalMatrix subtract(RationalMatrix other) { return this.add(other.negate()); }

	@Override
	public RationalMatrix multiply(RationalMatrix other) {
		if (this.cols != other.rows) throw new IllegalArgumentException("Dimensions incompatible for multiplication.");
		Rational[][] resultData = new Rational[this.rows][other.cols];
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < other.cols; j++) {
				Rational sum = Rational.ZERO;
				for (int k = 0; k < this.cols; k++) {
					sum = sum.add(this.data[i][k].multiply(other.data[k][j]));
				}
				resultData[i][j] = sum;
			}
		}
		return new RationalMatrix(resultData);
	}
    
    @Override
	public RationalVector multiply(RationalVector vector) {
        if (this.cols != vector.dimension()) throw new IllegalArgumentException("Matrix columns must match vector dimension.");
        Rational[] resultData = new Rational[this.rows];
        for (int i = 0; i < this.rows; i++) {
            Rational sum = Rational.ZERO;
            for (int j = 0; j < this.cols; j++) {
                sum = sum.add(this.data[i][j].multiply(vector.get(j)));
            }
            resultData[i] = sum;
        }
		return new RationalVector(resultData);
	}

	@Override
	public RationalMatrix transpose() {
		Rational[][] resultData = new Rational[cols][rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				resultData[j][i] = this.data[i][j];
			}
		}
		return new RationalMatrix(resultData);
	}
    
    @Override
    public RationalVector getRowVector(int row) {
        if (row < 0 || row >= rows) throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
		return new RationalVector(Arrays.copyOf(data[row], cols));
	}

	@Override
	public RationalVector getColumnVector(int col) {
        if (col < 0 || col >= cols) throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        Rational[] columnData = new Rational[rows];
		for (int i = 0; i < rows; i++) {
			columnData[i] = data[i][col];
		}
		return new RationalVector(columnData);
	}

	@Override
    public Rational determinant()
    {
        Comparator<Rational> rationalComparator = Comparator.naturalOrder();

        return GaussianElimination.determinant(this.data, Rational.ZERO, rationalComparator);
    }

    @Override
    public RationalMatrix inverse()
    {
        Comparator<Rational> rationalComparator = Comparator.naturalOrder();
        Rational[][] invertedData = GaussJordanElimination.inverse(this.data, Rational.ZERO, Rational.ONE,rationalComparator);

        return new RationalMatrix(invertedData);
    }

    // --- Standard Java impls ---
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rows).append("x").append(cols).append(" Rational Matrix:\n");
        for (int i = 0; i < rows; i++) {
            sb.append(Arrays.toString(data[i])).append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public final boolean equals(Object other) {
        return (other instanceof RationalMatrix) && isEqual((RationalMatrix)other);
    }
    
    @Override
    public final int hashCode() {
        int result = java.util.Objects.hash(rows, cols);
        result = 31 * result + Arrays.deepHashCode(data);
        return result;
    }
}
