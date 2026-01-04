package net.gommagomma.smfn.math.linearalgebra.matrices;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.LinearSpace;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Rappresenta uno spazio vettoriale di matrici m x n sopra un campo K.
 */
public class MatrixSpace<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
extends MatrixModule<K, S>
implements LinearSpace<Matrix<K>, K, S>
{
    public MatrixSpace(S scalarStructure, int rows, int cols) {
        super(scalarStructure, rows, cols);
    }


    @Override
    public String getName() {
        return "Matrix Space (" + rows + "x" + cols + ") over Field " + scalarStructure.getName();
    }

    /**
     * In uno spazio vettoriale su un campo, possiamo definire la divisione 
     * di una matrice per uno scalare (moltiplicazione per l'inverso).
     */
    public Matrix<K> divide(Matrix<K> m, K scalar) {
    	validateDimensions(m);
        if (scalarStructure.isZero(scalar)) {
            throw new ArithmeticException("Division by zero scalar");
        }
        K inverse = scalarStructure.inverse(scalar);
        return scale(inverse, m);
    }


    public Matrix<K> toRowEchelonForm(Matrix<K> original) {
        validateDimensions(original);
        
        // Lavoriamo su un array di lavoro per evitare set() continui sulla struttura
        K[] workingData = original.getData(); 
        int pivotRow = 0;

        for (int j = 0; j < cols && pivotRow < rows; j++) {
            // 1. Ricerca del Pivot (Partial Pivoting per stabilità numerica)
            int bestRow = findBestPivotInArray(workingData, j, pivotRow);
            
            K pivotValue = getFromData(workingData, bestRow, j);
            if (scalarStructure.isZero(pivotValue)) continue;

            // 2. Scambio righe
            swapRowsInArray(workingData, bestRow, pivotRow);

            // 3. Eliminazione
            for (int i = pivotRow + 1; i < rows; i++) {
                K currentVal = getFromData(workingData, i, j);
                if (!scalarStructure.isZero(currentVal)) {
                    // factor = - (currentVal / pivotVal)
                    K factor = scalarStructure.multiply(
                        scalarStructure.negate(scalarStructure.one()),
                        scalarStructure.divide(currentVal, getFromData(workingData, pivotRow, j))
                    );
                    combineRowsInArray(workingData, i, pivotRow, factor);
                }
            }
            pivotRow++;
        }
        return of(workingData);
    }


    
    public int rank(Matrix<K> original) {
        // Portiamo in forma a gradini
        Matrix<K> ref = toRowEchelonForm(original);
        int rank = 0;
        
        for (int i = 0; i < ref.getRows(); i++) {
            boolean isRowZero = true;
            for (int j = 0; j < ref.getCols(); j++) {
                if (!scalarStructure.isZero(ref.get(i, j))) {
                    isRowZero = false;
                    break;
                }
            }
            if (!isRowZero) rank++;
        }
        return rank;
    }



    private int findBestPivotInArray(K[] data, int col, int startRow) {
        int bestRow = startRow;
        Real maxMag = scalarStructure.magnitude(getFromData(data, startRow, col));
        for (int i = startRow + 1; i < rows; i++) {
            Real currentMag = scalarStructure.magnitude(getFromData(data, i, col));
            if (currentMag.isGreaterThan(maxMag)) {
                maxMag = currentMag;
                bestRow = i;
            }
        }
        return bestRow;
    }

    private void swapRowsInArray(K[] data, int r1, int r2) {
        if (r1 == r2) return;
        for (int c = 0; c < cols; c++) {
            K temp = getFromData(data, r1, c);
            setData(data, r1, c, getFromData(data, r2, c));
            setData(data, r2, c, temp);
        }
    }

    private void combineRowsInArray(K[] data, int target, int source, K factor) {
        for (int c = 0; c < cols; c++) {
            K scaledSource = scalarStructure.multiply(factor, getFromData(data, source, c));
            K newVal = scalarStructure.add(getFromData(data, target, c), scaledSource);
            setData(data, target, c, newVal);
        }
    }

    private K getFromData(K[] data, int r, int c) { return data[r * cols + c]; }
    private void setData(K[] data, int r, int c, K val) { data[r * cols + c] = val; }
}