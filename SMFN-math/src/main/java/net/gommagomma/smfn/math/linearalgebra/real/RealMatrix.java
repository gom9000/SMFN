package net.gommagomma.smfn.math.linearalgebra.real;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.Matrix;

public final class RealMatrix
implements Matrix<Real, RealVector, RealMatrix>
{    
    private final Real[][] data;
    private final int rows;
    private final int cols;

    
    public RealMatrix(Real[][] data) {
        if (data == null || data.length == 0 || data[0].length == 0) {
            throw new IllegalArgumentException("Matrix data cannot be null or empty.");
        }
        
        this.rows = data.length;
        this.cols = data[0].length; // Determina il numero di colonne dalla prima riga
        this.data = new Real[rows][cols];
        
        // Copia difensiva riga per riga e verifica la consistenza delle dimensioni
        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                 throw new IllegalArgumentException("All rows must have the same number of columns.");
            }
            // Copia l'array interno (difensivo)
            this.data[i] = Arrays.copyOf(data[i], cols);
        }
    }

    @Override
	public RealVector getRowVector(int row) {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }
		// Ritorna una copia difensiva della riga come nuovo RealVector
		return new RealVector(Arrays.copyOf(data[row], cols));
	}

    @Override
    public RealVector getColumnVector(int col) {
        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }

        Real[] columnData = new Real[rows];

        for (int i = 0; i < rows; i++) {
            columnData[i] = data[i][col];
        }

        return new RealVector(columnData);
    }

    
    @Override
    public Real determinant() {
        if (rows != cols) throw new UnsupportedOperationException("Determinant only for square matrices.");
        // L'implementazione efficiente richiede algoritmi di decomposizione (es. LU) 
        // che sono complessi da implementare da zero. Placeholder per ora.
        // Puoi usare un algoritmo ricorsivo di Laplace per piccole matrici (2x2, 3x3)
        if (rows == 2) {
            return data[0][0].multiply(data[1][1]).subtract(data[0][1].multiply(data[1][0]));
        }
        throw new UnsupportedOperationException("Advanced determinant calculation not yet implemented.");
    }

    @Override
    public RealMatrix inverse() {
        if (rows != cols) throw new UnsupportedOperationException("Inverse only for square matrices.");
        // Richiede algoritmi complessi come Gauss-Jordan. Placeholder per ora.
        throw new UnsupportedOperationException("Matrix inversion not yet implemented.");
    }

    // --- Standard Java impls ---
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rows).append("x").append(cols).append(" Real Matrix:\n");
        for (int i = 0; i < rows; i++) {
            sb.append(Arrays.toString(data[i])).append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public final boolean equals(Object other) {
        // Usa il warning di Real.equals()
        return (other instanceof RealMatrix) && isEqual((RealMatrix)other);
    }
    
    @Override
    public final int hashCode() {
        // Hash code basato sui contenuti
        int result = java.util.Objects.hash(rows, cols);
        result = 31 * result + Arrays.deepHashCode(data);
        return result;
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
    public Real get(int row, int col) {
        // Aggiungiamo controlli robusti sugli indici
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Indices out of bounds: row=" + row + ", col=" + col);
        }
        return this.data[row][col];
    }


    // --- Implementazioni di AlgebraicElement e Aritmetica ---

    @Override
    public boolean isEqual(RealMatrix other) {
        if (this.rows != other.rows || this.cols != other.cols) return false;
        // Confronto elemento per elemento usando Real.isEqual (epsilon-based)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!this.data[i][j].isEqual(other.data[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public RealMatrix copy() {
        return new RealMatrix(this.data);
    }

    @Override
    public RealMatrix add(RealMatrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Matrices must have the same dimensions for addition.");
        }
        // Crea un nuovo array per il risultato
        Real[][] resultData = new Real[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultData[i][j] = this.data[i][j].add(other.data[i][j]);
            }
        }
        // Restituisce una nuova istanza (immutabilità)
        return new RealMatrix(resultData);
    }
    
    // Metodo helper per la negazione (necessario per subtract)
    public RealMatrix negate() {
        Real[][] resultData = new Real[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				resultData[i][j] = this.data[i][j].negate();
			}
		}
		return new RealMatrix(resultData);
    }

    @Override
    public RealMatrix subtract(RealMatrix other) {
        // Usa il metodo add e negate per implementare la sottrazione
        return this.add(other.negate());
    }

    @Override
    public RealMatrix multiply(RealMatrix other) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Number of columns in the first matrix must match number of rows in the second.");
        }
        int resultRows = this.rows;
        int resultCols = other.cols;
        Real[][] resultData = new Real[resultRows][resultCols];

        for (int i = 0; i < resultRows; i++) { // Iterate over rows of A
            for (int j = 0; j < resultCols; j++) { // Iterate over columns of B
                Real sum = Real.ZERO;
                for (int k = 0; k < this.cols; k++) { // Dot product of row i and column j
                    sum = sum.add(this.data[i][k].multiply(other.data[k][j]));
                }
                resultData[i][j] = sum;
            }
        }
        return new RealMatrix(resultData);
    }

    @Override
    public RealVector multiply(RealVector vector) {
        if (this.cols != vector.dimension()) {
             throw new IllegalArgumentException("Matrix columns must match vector dimension for multiplication.");
        }
        Real[] resultData = new Real[this.rows];
        for (int i = 0; i < this.rows; i++) {
            Real sum = Real.ZERO;
            for (int j = 0; j < this.cols; j++) {
                sum = sum.add(this.data[i][j].multiply(vector.get(j)));
            }
            resultData[i] = sum;
        }
		return new RealVector(resultData);
    }

    @Override
    public RealMatrix transpose() {
        Real[][] resultData = new Real[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultData[j][i] = this.data[i][j];
            }
        }
        return new RealMatrix(resultData);
    }

    public static RealMatrix identity(int size)
    {
        if (size <= 0) {
            throw new IllegalArgumentException("Dimension must be positive.");
        }

        Real[][] data = new Real[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                data[i][j] = (i == j) ? Real.ONE : Real.ZERO;
            }
        }

        return new RealMatrix(data); 
    }
}
