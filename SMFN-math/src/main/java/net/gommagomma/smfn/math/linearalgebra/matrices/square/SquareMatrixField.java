package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;

public class SquareMatrixField {

	
	
	
    /**
     * Scambia due righe nella matrice.
     */
    public void swapRows(Matrix<K> m, int i, int j) {
        if (i == j) return;
        for (int col = 0; col < m.getCols(); col++) {
            K temp = m.get(i, col);
            m.set(i, col, m.get(j, col));
            m.set(j, col, temp);
        }
    }
    /**
     * Moltiplica la riga r per uno scalare k
     */
    public void multiplyRow(Matrix<K> m, int r, K k) {
        int currentCols = m.getCols(); // Usa la larghezza reale della matrice (es. 6 se aumentata)
        for (int col = 0; col < currentCols; col++) {
            m.set(r, col, scalarStructure.multiply(k, m.get(r, col)));
        }
    }
    /**
     * rigaTarget = rigaTarget + (rigaSource * k)
     */
    public void combineRows(Matrix<K> m, int target, int source, K k) {
        int currentCols = m.getCols();
        for (int col = 0; col < currentCols; col++) {
            K scaledSource = scalarStructure.multiply(k, m.get(source, col));
            K newValue = scalarStructure.add(m.get(target, col), scaledSource);
            m.set(target, col, newValue);
        }
    }


    /**
     * Calcola il determinante della matrice.
     * Richiede che la matrice sia quadrata.
     */
    @Override
    public K determinant(Matrix<K> original) {
        if (original.getRows() != original.getCols()) {
            throw new IllegalArgumentException("Determinant is only defined for square matrices.");
        }

        Matrix<K> m = original.copy();
        int n = m.getRows();
        K det = scalarStructure.one();
        int sign = 1;

        for (int j = 0; j < n; j++) {
            // 1. Ricerca del Pivot (Partial Pivoting)
            int bestRow = j;
            Real maxMagnitude = scalarStructure.magnitude(m.get(j, j));

            for (int i = j + 1; i < n; i++) {
                Real currentMag = scalarStructure.magnitude(m.get(i, j));
                if (currentMag.isGreaterThan(maxMagnitude)) {
                    maxMagnitude = currentMag;
                    bestRow = i;
                }
            }

            // Se il pivot è zero, il determinante è zero
            if (scalarStructure.isZero(m.get(bestRow, j))) {
                return scalarStructure.zero();
            }

            // 2. Scambio righe e inversione segno
            if (bestRow != j) {
                swapRows(m, bestRow, j);
                sign *= -1;
            }

            // 3. Accumulo il valore del pivot nel determinante
            det = scalarStructure.multiply(det, m.get(j, j));

            // 4. Eliminazione Gaussiana
            for (int i = j + 1; i < n; i++) {
                K factor = scalarStructure.divide(m.get(i, j), m.get(j, j));
                combineRows(m, i, j, scalarStructure.negate(factor));
            }
        }

        // Applichiamo il segno finale
        if (sign == -1) {
            det = scalarStructure.negate(det);
        }

        return det;
    }

    public Matrix<K> invert(Matrix<K> original) {
        if (original.getRows() != original.getCols()) {
            throw new IllegalArgumentException("Solo le matrici quadrate possono essere invertite.");
        }

        int n = original.getRows();
        // Creiamo la matrice aumentata [A | I] di dimensione n x 2n
        Matrix<K> aug = new Matrix<>(n, n * 2, scalarStructure);
        K zero = scalarStructure.zero();
        K one = scalarStructure.one();

        // Inizializzazione della matrice aumentata
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                aug.set(i, j, original.get(i, j)); // Parte A
                aug.set(i, j + n, (i == j) ? one : zero); // Parte I
            }
        }

        // 1. Eliminazione in avanti (Forward Elimination)
        for (int j = 0; j < n; j++) {
            int pivot = j;
            // Ricerca pivot parziale per stabilità
            for (int i = j + 1; i < n; i++) {
                if (scalarStructure.magnitude(aug.get(i, j))
                    .isGreaterThan(scalarStructure.magnitude(aug.get(pivot, j)))) {
                    pivot = i;
                }
            }

            if (scalarStructure.isZero(aug.get(pivot, j))) {
                throw new ArithmeticException("Matrice singolare: non invertibile.");
            }

            swapRows(aug, pivot, j);

            // Normalizziamo la riga del pivot affinché il pivot sia 1
            K pivotVal = aug.get(j, j);
            K invPivot = scalarStructure.divide(one, pivotVal);
            multiplyRow(aug, j, invPivot);

            // Azzeriamo le altre celle della colonna j (sopra e sotto)
            for (int i = 0; i < n; i++) {
                if (i != j) {
                    K factor = scalarStructure.negate(aug.get(i, j));
                    combineRows(aug, i, j, factor);
                }
            }
        }

        // 2. Estraiamo la parte destra della matrice aumentata (l'inversa)
        Matrix<K> inverse = new Matrix<>(n, n, scalarStructure);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse.set(i, j, aug.get(i, j + n));
            }
        }
        return inverse;
    }

    /**
     * Risolve il sistema lineare Ax = b utilizzando l'eliminazione di Gauss-Jordan.
     * A: matrice quadrata n x n
     * b: vettore colonna n x 1
     * Ritorna: x come Matrix n x 1
     */
    public Matrix<K> solve(Matrix<K> A, Matrix<K> b) {
        if (A.getRows() != A.getCols()) {
            throw new IllegalArgumentException("La matrice A deve essere quadrata.");
        }
        if (A.getRows() != b.getRows() || b.getCols() != 1) {
            throw new IllegalArgumentException("Dimensioni di b non compatibili con A.");
        }

        int n = A.getRows();
        // Creiamo la matrice aumentata [A | b] di dimensione n x (n + 1)
        Matrix<K> aug = new Matrix<>(n, n + 1, scalarStructure);
        
        // Inizializzazione della matrice aumentata
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                aug.set(i, j, A.get(i, j));
            }
            aug.set(i, n, b.get(i, 0)); // Colonna b
        }

        // Processo di eliminazione
        for (int j = 0; j < n; j++) {
            // Ricerca del miglior pivot per stabilità numerica
            int pivotRow = findBestPivot(aug, j, j);
            
            if (scalarStructure.isZero(aug.get(pivotRow, j))) {
                throw new ArithmeticException("Sistema singolare: nessuna soluzione unica esistente.");
            }

            swapRows(aug, pivotRow, j);

            // Normalizzazione della riga: rendiamo il pivot uguale a 1
            K pivotVal = aug.get(j, j);
            K invPivot = scalarStructure.divide(scalarStructure.one(), pivotVal);
            multiplyRow(aug, j, invPivot);

            // Eliminazione degli elementi sopra e sotto il pivot
            for (int i = 0; i < n; i++) {
                if (i != j) {
                    K factor = scalarStructure.negate(aug.get(i, j));
                    combineRows(aug, i, j, factor);
                }
            }
        }

        // Estrazione della soluzione x dall'ultima colonna
        Matrix<K> x = new Matrix<>(n, 1, scalarStructure);
        for (int i = 0; i < n; i++) {
            x.set(i, 0, aug.get(i, n));
        }
        return x;
    }
}
