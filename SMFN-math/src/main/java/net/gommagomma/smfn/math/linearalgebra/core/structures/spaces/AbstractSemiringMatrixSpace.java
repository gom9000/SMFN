package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.SemiringMatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;

/**
 * Implementazione astratta base per le strutture di matrici su un Semianello.
 * Centralizza la logica della factory per ridurre la duplicazione nelle classi concrete.
 */
public abstract class AbstractSemiringMatrixSpace<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>>
implements SemiringMatrixSemimodule<K, V, M> 
{
    protected final int rows;
    protected final int cols;

    protected AbstractSemiringMatrixSpace(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive.");
        }
        this.rows = rows;
        this.cols = cols;
    }

    // --- Metodi Dimensionali ---

    @Override public int getMatrixRows() { return rows; }
    @Override public int getMatrixColumns() { return cols; }

    // --- Logica Factory: Creazione Identità e Zero ---

    @Override
    public M createZeroMatrix(int rows, int cols) {
        validateDimensions(rows, cols);
        K[][] data = createDataArray(rows, cols);
        K zero = getScalarStructure().zero();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = zero;
            }
        }
        return createMatrix(data);
    }

    @Override
    public M createIdentityMatrix(int dimension) {
        // Un'identità può essere creata solo se lo spazio è quadrato e la dimensione coincide,
        // oppure se usata come factory generica per matrici quadrate.
        validateDimensions(dimension, dimension);
        
        K[][] data = createDataArray(dimension, dimension);
        K zero = getScalarStructure().zero();
        K one = getScalarStructure().one();

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                data[i][j] = (i == j) ? one : zero;
            }
        }
        return createMatrix(data);
    }

    // --- Logica Factory: Conversioni Primitive (Centralizzate) ---

    @Override
    public M createMatrix(double[][] data) {
        if (data == null) throw new IllegalArgumentException("Data cannot be null");
        validateInputDimensions(data.length, data.length > 0 ? data[0].length : 0);

        K[][] components = createDataArray(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                components[i][j] = getScalarStructure().of(data[i][j]);
            }
        }
        return createMatrix(components);
    }

    @Override
    public M createMatrix(long[][] data) {
    	if (data == null) throw new IllegalArgumentException("Data cannot be null");
        validateInputDimensions(data.length, data.length > 0 ? data[0].length : 0);

        K[][] components = createDataArray(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                components[i][j] = getScalarStructure().of(data[i][j]);
            }
        }
        return createMatrix(components);
    }

    @Override
    public M createMatrix(int[][] data) {
        if (data == null) throw new IllegalArgumentException("Data cannot be null");
        validateInputDimensions(data.length, data.length > 0 ? data[0].length : 0);

        K[][] components = createDataArray(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                components[i][j] = getScalarStructure().of(data[i][j]);
            }
        }
        return createMatrix(components);
    }

    // --- Metodi Helper e Template ---

    /**
     * Metodo Template: deve essere implementato dalle classi concrete 
     * per istanziare l'array fisico (es: new Complex[r][c]).
     */
    protected abstract K[][] createDataArray(int r, int c);

    protected void validateInputDimensions(int inputRows, int inputCols) {
        if (inputRows != this.rows || inputCols != this.cols) {
            throw new IllegalArgumentException(
                String.format("Input dimensions %dx%d do not match Space dimensions %dx%d", 
                inputRows, inputCols, this.rows, this.cols)
            );
        }
    }
    protected void validateDimensions(int r, int c) {
        if (r != this.rows || c != this.cols) {
            throw new IllegalArgumentException("Requested dimensions (" + r + "x" + c + ") do not match Space dimensions.");
        }
    }

    @Override
    public boolean contains(M m) {
        return m != null && m.getRows() == this.rows && m.getColumns() == this.cols;
    }
}