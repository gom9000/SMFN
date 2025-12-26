package net.gommagomma.smfn.math.linearalgebra.core.elements.matrices;

import java.lang.reflect.Array;
import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.elements.tensors.TensorElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.SemiringMatrixSemimodule;

public abstract class AbstractSemiringMatrix<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>, S extends SemiringMatrixSemimodule<K, V, M>>
implements SemiringMatrixElement<K, V, M>, TensorElement<K>
{
    protected final K[][] data;
    protected final int rows;
    protected final int cols;
    protected final S matrixStructure;


    protected AbstractSemiringMatrix(K[][] data, S matrixStructure)
    {
        if (data == null || data.length == 0 || data[0] == null || data[0].length == 0) {
            throw new IllegalArgumentException("Matrix data cannot be null or empty.");
        }

        this.matrixStructure = matrixStructure;
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = copyAndValidateData(data);
    }

    /**
     * Costruttore helper per creare matrici vuote/zero specificando le dimensioni.
     * @param rows Il numero di righe.
     * @param cols Il numero di colonne.
     * @param factory La factory specifica per il tipo K.
     */
    protected AbstractSemiringMatrix(int rows, int cols, S matrixStructure)
    {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive.");
        }
        this.rows = rows;
        this.cols = cols;
        this.matrixStructure = matrixStructure;
        this.data = createMatrixArray(rows, cols);
        for (int i = 0; i < rows; i++) {
		    Arrays.fill(this.data[i], matrixStructure.getScalarStructure().zero());
	    }
    }


    /**
     * Restituisce la classe runtime di K (il tipo scalare).
     * Necessario a causa della type erasure di Java per la creazione dinamica di array.
     */
    protected abstract Class<K> getScalarClass();


    @Override
    public V apply(V vector) {
        return this.multiply(vector);
    }

    // Helpers

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
            for (int j = 0; j < cols; j++) {
                newData[i][j] = sourceData[i][j].copy(); 
            }
        }
        return newData;
    }
    
    /**
     * Valida che le dimensioni della matrice corrente e dell'altra siano identiche.
     * @param other L'altra matrice.
     * @throws IllegalArgumentException se le dimensioni non coincidono.
     */
    protected void validateDimensions(M other)
    {
        if (this.rows != other.getRows() || this.cols != other.getColumns()) {
            throw new IllegalArgumentException("Matrices must have the same dimensions.");
        }
    }


    @Override
    public M getOne() {
        // l'identità esiste solo nel sottoinsieme delle matrici quadrate
        if (this.rows != this.cols) {
            throw new UnsupportedOperationException("Multiplicative identity is not defined for rectangular matrices.");
        }

        // Procediamo con la creazione dell'identità specifica per il tipo K
        K[][] identityData = createMatrixArray(rows, cols);
        K zero = matrixStructure.getScalarStructure().zero();
        K one = matrixStructure.getScalarStructure().one();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                identityData[i][j] = (i == j) ? one : zero;
            }
        }

        return matrixStructure.createMatrix(identityData);
    }

    @Override // AlgebraicElement impls
    public boolean isMathematicallyEqualTo(M other) {
    	if (this == other) {
            return true;
        }
        
        if (other == null) {
            return false;
        }
        
        if (this.rows != other.getRows() || this.cols != other.getColumns()) {
            return false;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!this.data[i][j].isMathematicallyEqualTo(other.get(i, j))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override // AlgebraicElement impls
    public M copy() {
        K[][] resultData = createMatrixArray(rows, cols); 
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultData[i][j] = this.data[i][j].copy(); 
            }
        }
        return matrixStructure.createMatrix(resultData);
    }


    @Override // AdditiveMonoidElement impls
    public M add(M other) {
        validateDimensions(other);
        K[][] resultData = createMatrixArray(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultData[i][j] = this.data[i][j].add(other.get(i, j));
            }
        }
        return matrixStructure.createMatrix(resultData);
    }

    @Override // AdditiveMonoidElement impls
    public M getZero() {
         return matrixStructure.createZeroMatrix(this.rows, this.cols);
    }


    @Override // SemiringMatrixElement impls
    public int getRows() { return this.rows; }

    @Override // SemiringMatrixElement impls
    public int getColumns() { return this.cols; }

    @Override // SemiringMatrixElement impls
    public K get(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Indices out of bounds: row=" + row + ", col=" + col);
        }
        return this.data[row][col];
    }

    @Override // SemiringMatrixElement impls
    public M multiplyByScalar(K scalar) {
        K[][] resultData = createMatrixArray(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultData[i][j] = this.data[i][j].multiply(scalar);
            }
        }
        return matrixStructure.createMatrix(resultData);
    }
    
    @Override // SemiringMatrixElement impls
    public M multiply(M other) {
        if (this.cols != other.getRows()) {
            throw new IllegalArgumentException("Number of columns in the first matrix must match number of rows in the second.");
        }
        int resultRows = this.rows;
        int resultCols = other.getColumns();
        K[][] resultData = createMatrixArray(resultRows, resultCols);
        K zero = matrixStructure.getScalarStructure().zero();

        for (int i = 0; i < resultRows; i++) {
            for (int j = 0; j < resultCols; j++) {
                K sum = zero;
                for (int k = 0; k < this.cols; k++) {
                    sum = sum.add(this.data[i][k].multiply(other.get(k, j)));
                }
                resultData[i][j] = sum;
            }
        }
        return matrixStructure.createMatrix(resultData);
    }

    @Override // SemiringMatrixElement impls
    public M transpose() {
    	K[][] resultData = createMatrixArray(cols, rows); 
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultData[j][i] = this.data[i][j];
            }
        }
        return matrixStructure.createMatrix(resultData);
    }


	@Override // SemiringMatrixElement impls
    public V multiply(V vector) {
        if (this.cols != vector.dimension()) {
             throw new IllegalArgumentException("Matrix columns must match vector dimension for multiplication.");
        }
        @SuppressWarnings("unchecked")
        K[] resultData = (K[]) Array.newInstance(getScalarClass(), this.rows);
        K zero = matrixStructure.getScalarStructure().zero();

        for (int i = 0; i < this.rows; i++) {
            K sum = zero;
            for (int j = 0; j < this.cols; j++) {
                sum = sum.add(this.data[i][j].multiply(vector.get(j)));
            }
            resultData[i] = sum;
        }
		return matrixStructure.getVectorStructure().createVector(resultData);
    }


    @Override // TensorElement impls
    public final int rank() {
        return 2;
    }

    @Override // TensorElement impls
    public final int[] getShape() {
        return new int[] { this.rows, this.cols };
    }

    @Override // TensorElement impls
    public final long size() {
        return (long) this.rows * this.cols;
    }

    @Override // TensorElement impls
    public final K get(int... indices) {
        if (indices.length != rank()) {
            throw new IllegalArgumentException("Indices length (" + indices.length + ") must match the Tensor Rank (2) for matrices.");
        }
        int row = indices[0];
        int col = indices[1];

        return this.data[row][col]; 
    }


    @Override  // Java Standard impls
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(rows).append("x").append(cols).append(" Matrix:\n");
        for (int i = 0; i < rows; i++) {
            sb.append(Arrays.toString(data[i])).append("\n");
        }

        return sb.toString();
    }

    @Override  // Java Standard impls
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        @SuppressWarnings("unchecked")
		M castedOther = (M) other; 

        if (this.rows != castedOther.getRows() || this.cols != castedOther.getColumns()) {
            return false;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!this.data[i][j].equals(castedOther.get(i, j))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override  // Java Standard impls
    public final int hashCode() {
        int result = java.util.Objects.hash(rows, cols);

        result = 31 * result + Arrays.deepHashCode(data); 
        return result;
    }
}
