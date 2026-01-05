package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSpace;

public class SquareMatrixField<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
extends SquareMatrixRing<K, S>
implements Field<SquareMatrix<K>>
{
	private final MatrixSpace<K, S> spaceDelegate;

	public SquareMatrixField(S scalarStructure, int n) {
		super(scalarStructure, n);
        this.spaceDelegate = new MatrixSpace<>(scalarStructure, n, n);
    }


    @Override
    public String getName() {
        return "Square Matrix Field (" + n + "x" + n + ") over " + scalarStructure.getName();
    }

    /**
     * Calcola l'inversa della matrice.
     * Implementazione tramite matrice aumentata [A | I] e riduzione a gradini.
     */
    @Override
    @SuppressWarnings("unchecked")
    public SquareMatrix<K> inverse(SquareMatrix<K> m) {
        int totalCols = 2 * n;
        K[] augData = (K[]) new ScalarElement[n * totalCols];
        K zero = scalarStructure.zero();
        K one = scalarStructure.one();

        // 1. Costruzione Matrice Aumentata [A | I]
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augData[i * totalCols + j] = m.get(i, j);
                augData[i * totalCols + (j + n)] = (i == j) ? one : zero;
            }
        }

        // 2. Gauss-Jordan
        performGaussJordan(augData, n, totalCols);

        // 3. Estrazione Inversa
        K[] invData = (K[]) new ScalarElement[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                invData[i * n + j] = augData[i * totalCols + (j + n)];
            }
        }
        return of(invData);
    }

    /**
     * Trasforma la matrice aumentata in forma RREF (Reduced Row Echelon Form).
     * Al termine, se la matrice originale era invertibile, la parte sinistra [0..n-1] 
     * sarà l'identità e la parte destra [n..2n-1] sarà l'inversa.
     */
    private void performGaussJordan(K[] data, int rows, int cols) {
        int pivotRow = 0;

        for (int j = 0; j < n && pivotRow < rows; j++) {
            // 1. Ricerca del Pivot (Partial Pivoting)
            int bestRow = spaceDelegate.findBestPivotInArray(data, j, pivotRow, rows, cols);
            K pivotValue = spaceDelegate.getFromData(data, bestRow, j, cols);

            if (scalarStructure.isZero(pivotValue)) {
                throw new ArithmeticException("Matrice singolare: impossibile calcolare l'inversa.");
            }

            // 2. Scambio righe per portare il pivot sulla riga corrente
            spaceDelegate.swapRowsInArray(data, bestRow, pivotRow, cols);

            // 3. Normalizzazione della riga Pivot (Rendi il pivot = 1)
            K invPivot = scalarStructure.inverse(pivotValue);
            for (int k = j; k < cols; k++) {
                K currentVal = spaceDelegate.getFromData(data, pivotRow, k, cols);
                spaceDelegate.setData(data, pivotRow, k, scalarStructure.multiply(currentVal, invPivot), cols);
            }

            // 4. Eliminazione sopra e sotto il pivot
            for (int i = 0; i < rows; i++) {
                if (i != pivotRow) {
                    K factor = spaceDelegate.getFromData(data, i, j, cols);
                    if (!scalarStructure.isZero(factor)) {
                        // Riga_i = Riga_i - (factor * Riga_pivot)
                        // Il factor da passare a combineRowsInArray è -factor
                        K negFactor = scalarStructure.negate(factor);
                        spaceDelegate.combineRowsInArray(data, i, pivotRow, negFactor, cols);
                    }
                }
            }
            pivotRow++;
        }
    }

    /**
     * In un Field, possiamo sovrascrivere il determinante di Laplace (O(n!))
     * con l'eliminazione Gaussiana (O(n^3)), molto più efficiente.
     */
    @Override
    public K determinant(SquareMatrix<K> m) {
        // Se n è piccolo, Laplace va bene, altrimenti Gauss
        if (n <= 3) return super.determinant(m);
        
        return calculateDeterminantViaGauss(m);
    }


    private K calculateDeterminantViaGauss(SquareMatrix<K> m) {
        K[] data = m.getData();
        K det = scalarStructure.one();
        int swaps = 0;

        for (int j = 0; j < n; j++) {
            // Ricerca pivot (logica semplificata)
            int pivot = j; // Trova riga con valore max nel campo
            if (pivot != j) {
                // swap e det = det * -1
                swaps++;
            }
            K pivotVal = data[j * n + j];
            if (scalarStructure.isZero(pivotVal)) return scalarStructure.zero();
            
            det = scalarStructure.multiply(det, pivotVal);
            
            // Eliminazione sotto
            for (int i = j + 1; i < n; i++) {
                K factor = scalarStructure.divide(data[i * n + j], pivotVal);
                for (int k = j; k < n; k++) {
                    K sub = scalarStructure.multiply(factor, data[j * n + k]);
                    data[i * n + k] = scalarStructure.subtract(data[i * n + k], sub);
                }
            }
        }
        return (swaps % 2 == 0) ? det : scalarStructure.negate(det);
    }
}
