package net.gommagomma.smfn.math.core.linearalgebra.elements;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;

import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.linearalgebra.structures.MatrixFactory;


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

    /**
     * Restituisce un comparatore per confrontare la magnitudo (es. valore assoluto) degli scalari K, 
     * necessario per il pivoting negli algoritmi come l'eliminazione gaussiana.
     * Deve essere implementato dalle classi concrete.
     */
    protected abstract Comparator<K> getMagnitudeComparator();


    // --- Implementazioni di MatrixElement

    @Override
    public K determinant() {
        if (getColumns() != getRows()) {
            throw new IllegalArgumentException("Matrix must be square to calculate the determinant.");
        }

        int n = getRows();

        // Creazione di una copia di lavoro sicura usando la reflection helper interna
        // Nota: questa copia è mutabile solo all'interno di questo algoritmo locale
        K[][] A = createMatrixArray(n, n);
        for (int i = 0; i < n; i++) {
            System.arraycopy(this.data[i], 0, A[i], 0, n);
        }

        K det = factory.getOneScalar();
        int sign = 1;

        for (int i = 0; i < n; i++) {
            int pivotRow = i;
            for (int k = i + 1; k < n; k++) {
                // Utilizzo del comparatore astratto per il pivoting
                if (getMagnitudeComparator().compare(A[k][i], A[pivotRow][i]) > 0) {
                     pivotRow = k;
                }
            }

            if (pivotRow != i) {
                K[] temp = A[i];
                A[i] = A[pivotRow];
                A[pivotRow] = temp;
                sign *= -1;
            }
            
            // Confronto con l'elemento zero della factory
            if (A[i][i].isEqual(factory.getZeroScalar())) {
                return factory.getZeroScalar();
            }
            
            for (int k = i + 1; k < n; k++) {
                K factor = A[k][i].divide(A[i][i]);
                for (int j = i; j < n; j++) {
                    A[k][j] = A[k][j].subtract(factor.multiply(A[i][j]));
                }
            }
            // Moltiplicazione lungo la diagonale principale
            det = det.multiply(A[i][i]);
        }
        
        if (sign == -1) {
            det = det.negate();
        }

        return det;
    }

    @Override
    public M inverse() {
        if (getColumns() != getRows()) {
            throw new IllegalArgumentException("Inverse can only be calculated for square matrices.");
        }
        int n = getRows();
        K zero = factory.getZeroScalar();
        K one = factory.getOneScalar();
        Comparator<K> comparator = getMagnitudeComparator();

        // Matrice aumentata [A | I]
        K[][] augmentedData = createMatrixArray(n, 2 * n);
        for (int i = 0; i < n; i++) {
            // Copia A
            System.arraycopy(this.data[i], 0, augmentedData[i], 0, n);
            // Inserisci l'Identità
            for (int j = 0; j < n; j++) {
                augmentedData[i][j + n] = (i == j) ? one : zero;
            }
        }

        // --- Inizio Algoritmo di Gauss-Jordan ---

        for (int i = 0; i < n; i++) {
            // Pivoting parziale
            int pivotRow = i;
            for (int k = i + 1; k < n; k++) {
                if (comparator.compare(augmentedData[k][i], augmentedData[pivotRow][i]) > 0) {
                    pivotRow = k;
                }
            }

            // Scambia le righe nella matrice aumentata completa
            if (pivotRow != i) {
                K[] temp = augmentedData[i];
                augmentedData[i] = augmentedData[pivotRow];
                augmentedData[pivotRow] = temp;
            }

            // Normalizzazione della riga pivot
            K pivot = augmentedData[i][i];
            if (pivot.isEqual(zero)) {
                throw new ArithmeticException("Matrix is singular, cannot be inverted.");
            }
            // Dividi tutta la riga per il pivot
            for (int j = i; j < 2 * n; j++) {
                augmentedData[i][j] = augmentedData[i][j].divide(pivot);
            }

            // Eliminazione delle altre righe (alto e basso)
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    K factor = augmentedData[k][i];
                    for (int j = i; j < 2 * n; j++) {
                        augmentedData[k][j] = augmentedData[k][j].subtract(factor.multiply(augmentedData[i][j]));
                    }
                }
            }
        }
        
        // --- Fine Algoritmo di Gauss-Jordan ---

        // Estrai la parte destra della matrice aumentata [I | Inverse]
        K[][] inverseData = createMatrixArray(n, n);
        for (int i = 0; i < n; i++) {
            System.arraycopy(augmentedData[i], n, inverseData[i], 0, n);
        }

        // 3. Utilizziamo la factory per creare una nuova istanza M finale e immutabile
        return factory.createMatrix(inverseData);
    }

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
