package net.gommagomma.smfn.math.linearalgebra.core;


import java.lang.reflect.Array;
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
    protected AbstractMatrix(K[][] data, F factory)
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
     * Restituisce la classe runtime di K (il tipo scalare).
     * Necessario a causa della type erasure di Java per la creazione dinamica di array.
     */
    protected abstract Class<K> getScalarClass();

    /**
     * Costruttore helper per creare matrici vuote/zero.
     */
    protected AbstractMatrix(int rows, int cols, F factory)
    {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive.");
        }
        this.rows = rows;
        this.cols = cols;
        this.factory = factory;
        this.data = createMatrixArray(rows, cols);
    }


    // --- Metodi Helper Interni

    /**
     * Metodo helper per creare un array K[][] in modo sicuro (senza unchecked cast warnings)
     * utilizzando la reflection e la classe K ottenuta da getScalarClass().
     */
    @SuppressWarnings("unchecked")
    protected K[][] createMatrixArray(int rows, int cols)
    {
        return (K[][]) Array.newInstance(getScalarClass(), rows, cols);
    }

    private K[][] copyAndValidateData(K[][] sourceData)
    {
        K[][] newData = createMatrixArray(rows, cols);
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


    // --- Implementazioni di AlgebraicElement

    @Override
    public M copy()
    {
        return factory.createMatrix(this.data);
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
    public M add(M other) {
        validateDimensions(other);
        K[][] resultData = createMatrixArray(rows, cols);
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
        K[][] resultData = createMatrixArray(rows, cols);
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
        K[][] resultData = createMatrixArray(rows, cols);
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
        K[][] resultData = createMatrixArray(rows, cols);
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
	@SuppressWarnings("unchecked")
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
        K[][] resultData = createMatrixArray(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultData[j][i] = this.data[i][j];
            }
        }
        return factory.createMatrix(resultData);
    }


    @Override
    public M getZero() {
         return factory.createZeroMatrix(this.rows, this.cols);
    }


	@Override
    public M negate() {
        K[][] resultData = createMatrixArray(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
                // Delega la negazione all'elemento K sottostante
				resultData[i][j] = this.data[i][j].negate();
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
}
