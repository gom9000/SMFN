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
        K[] workingData = original.getData(); 
        performGauss(workingData, rows, cols);
        return of(workingData);
    }

    protected void performGauss(K[] data, int r, int c) {
        int pivotRow = 0;
        for (int j = 0; j < c && pivotRow < r; j++) {
            int bestRow = findBestPivotInArray(data, j, pivotRow, r, c);
            K pivotValue = getFromData(data, bestRow, j, c);
            if (scalarStructure.isZero(pivotValue)) continue;

            swapRowsInArray(data, bestRow, pivotRow, c);

            for (int i = pivotRow + 1; i < r; i++) {
                K currentVal = getFromData(data, i, j, c);
                if (!scalarStructure.isZero(currentVal)) {
                    K factor = scalarStructure.multiply(
                        scalarStructure.negate(scalarStructure.one()),
                        scalarStructure.divide(currentVal, getFromData(data, pivotRow, j, c))
                    );
                    combineRowsInArray(data, i, pivotRow, factor, c);
                }
            }
            pivotRow++;
        }
    }

    public int findBestPivotInArray(K[] data, int col, int startRow, int r, int c) {
        int bestRow = startRow;
        Real maxMag = scalarStructure.magnitude(getFromData(data, startRow, col, c));
        for (int i = startRow + 1; i < r; i++) {
            Real currentMag = scalarStructure.magnitude(getFromData(data, i, col, c));
            if (currentMag.isGreaterThan(maxMag)) {
                maxMag = currentMag;
                bestRow = i;
            }
        }
        return bestRow;
    }

    public void swapRowsInArray(K[] data, int r1, int r2, int c) {
        if (r1 == r2) return;
        for (int j = 0; j < c; j++) {
            K temp = getFromData(data, r1, j, c);
            setData(data, r1, j, getFromData(data, r2, j, c), c);
            setData(data, r2, j, temp, c);
        }
    }

    public void combineRowsInArray(K[] data, int target, int source, K factor, int c) {
        for (int j = 0; j < c; j++) {
            K scaledSource = scalarStructure.multiply(factor, getFromData(data, source, j, c));
            K newVal = scalarStructure.add(getFromData(data, target, j, c), scaledSource);
            setData(data, target, j, newVal, c);
        }
    }

    public K getFromData(K[] data, int r, int j, int totalCols) { return data[r * totalCols + j]; }
    public void setData(K[] data, int r, int j, K val, int totalCols) { data[r * totalCols + j] = val; }
    
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
}