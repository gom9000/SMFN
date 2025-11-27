package net.gommagomma.smfn.math.linearalgebra.core;


import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;


//K = Tipo Scalare (es. Real)
//V = Tipo Vettore (es. RealVector)
//M = Tipo Matrice (es. RealMatrix)
//F = Tipo Factory (es. RealMatrixFactory)
public abstract class AbstractMatrix<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends MatrixElement<K, V, M>, F extends MatrixFactory<K, V, M>>
implements MatrixElement<K, V, M> 
{
    protected final K[][] data;
    protected final int rows;
    protected final int cols;
    protected final F factory;


    /**
     * Costruttore principale per AbstractMatrix.
     * Gestisce la validazione e la copia difensiva.
     * @param data I dati della matrice.
     * @param factory La factory specifica per il tipo K.
     */
    public AbstractMatrix(K[][] data, F factory)
    {
    	if (data == null || data.length == 0 || data[0].length == 0) {
            throw new IllegalArgumentException("Matrix data cannot be null or empty.");
        }

        this.factory = factory;
        this.rows = data.length;
        this.cols = data.length == 0 ? 0 : data[0].length;
        this.data = copyAndValidateData(data);
    }
    
    /**
     * Costruttore helper per creare matrici vuote/zero.
     */
    public AbstractMatrix(int rows, int cols, F factory)
    {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive.");
        }
        this.rows = rows;
        this.cols = cols;
        this.factory = factory;
        this.data = (K[][]) new FieldElement[rows][cols]; 
    }


    // --- Metodi Helper Interni

    private K[][] copyAndValidateData(K[][] sourceData)
    {
        K[][] newData = (K[][]) new FieldElement[rows][cols];
        for (int i = 0; i < rows; i++) {
            if (sourceData[i].length != cols) {
                 throw new IllegalArgumentException("All rows must have the same number of columns.");
            }
            newData[i] = Arrays.copyOf(sourceData[i], cols);
        }
        return newData;
    }
    protected void validateDimensions(M other)
    {
        if (this.rows != other.getRows() || this.cols != other.getColumns()) {
            throw new IllegalArgumentException("Matrices must have the same dimensions.");
        }
    }


    // --- Implementazioni di MatrixElement

    @Override
    public int getRows() { return this.rows; }

    @Override
    public int getColumns() { return this.cols; }

    @Override
    public K get(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Indices out of bounds: row=" + row + ", col=" + col);
        }
        return this.data[row][col];
    }

    @Override
    public boolean isEqual(M other) {
        if (this.rows != other.getRows() || this.cols != other.getColumns()) return false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!this.data[i][j].isEqual(other.get(i, j))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public M add(M other) {
        validateDimensions(other);
        K[][] resultData = (K[][]) new FieldElement[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultData[i][j] = this.data[i][j].add(other.get(i, j));
            }
        }
        return factory.createMatrix(resultData);
    }
    
    @Override
    public M subtract(M other) {
        validateDimensions(other);
        // Implementazione efficiente senza factory.createMatrix() per la negazione intermedia
        K[][] resultData = (K[][]) new FieldElement[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Sottrai l'elemento other[i][j] negato
                resultData[i][j] = this.data[i][j].subtract(other.get(i, j));
            }
        }
        return factory.createMatrix(resultData);
    }
    
    @Override
    public M multiplyByScalar(K scalar) {
        K[][] resultData = (K[][]) new FieldElement[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultData[i][j] = this.data[i][j].multiply(scalar);
            }
        }
        return factory.createMatrix(resultData);
    }

    @Override
    public M multiply(M other) {
        if (this.cols != other.getRows()) {
            throw new IllegalArgumentException("Number of columns in the first matrix must match number of rows in the second.");
        }
        int resultRows = this.rows;
        int resultCols = other.getColumns();
        K[][] resultData = (K[][]) new FieldElement[resultRows][resultCols];
        K zero = factory.getZeroScalar();

        for (int i = 0; i < resultRows; i++) {
            for (int j = 0; j < resultCols; j++) {
                K sum = zero;
                for (int k = 0; k < this.cols; k++) {
                    sum = sum.add(this.data[i][k].multiply(other.get(k, j)));
                }
                resultData[i][j] = sum;
            }
        }
        return factory.createMatrix(resultData);
    }

    @Override
    public V multiply(V vector) {
        if (this.cols != vector.dimension()) {
             throw new IllegalArgumentException("Matrix columns must match vector dimension for multiplication.");
        }
        K[] resultData = (K[]) new FieldElement[this.rows];
        K zero = factory.getZeroScalar();

        for (int i = 0; i < this.rows; i++) {
            K sum = zero;
            for (int j = 0; j < this.cols; j++) {
                sum = sum.add(this.data[i][j].multiply(vector.get(j)));
            }
            resultData[i] = sum;
        }
		return factory.createVector(resultData);
    }

    @Override
    public M transpose() {
        K[][] resultData = (K[][]) new FieldElement[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultData[j][i] = this.data[i][j];
            }
        }
        return factory.createMatrix(resultData);
    }


    // --- Implementazioni Java Standard

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(rows).append("x").append(cols).append(" Matrix:\n");
        for (int i = 0; i < rows; i++) {
            sb.append(Arrays.toString(data[i])).append("\n");
        }

        return sb.toString();
    }


    // --- Metodi Astratti ---

    @Override
    public abstract K determinant();
    @Override
    public abstract M inverse();
    @Override
    public abstract V getRowVector(int row);
    @Override
    public abstract V getColumnVector(int col);
    @Override
    public abstract boolean equals(Object other);
    @Override
    public abstract int hashCode();
}
