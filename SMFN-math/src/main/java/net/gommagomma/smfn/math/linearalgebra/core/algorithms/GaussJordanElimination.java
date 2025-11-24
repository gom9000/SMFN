package net.gommagomma.smfn.math.linearalgebra.core.algorithms;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;

public final class GaussJordanElimination {

    private GaussJordanElimination() { /* Impedisce l'instanziazione */ }

    @SuppressWarnings("unchecked")
    public static <K extends FieldElement<K>> K[][] inverse(K[][] matrixData, K elementZero, K elementOne, java.util.Comparator<K> magnitudeComparator)
    {
        int n = matrixData.length;
        if (matrixData[0].length != n) {
            throw new IllegalArgumentException("Matrix must be square to be inverted (n x n).");
        }

        // 1. Creare la matrice aumentata [A | I]
        // Usiamo un array 2D piatto come struttura dati temporanea efficiente
        K[][] augmented = (K[][]) new FieldElement[n][2 * n];
        for (int i = 0; i < n; i++) {
            // Copia la riga originale A
            System.arraycopy(matrixData[i], 0, augmented[i], 0, n);
            // Inizializza la parte I (Identità)
            for (int j = 0; j < n; j++) {
                augmented[i][j + n] = (i == j) ? elementOne.copy() : elementZero.copy();
            }
        }

        // 2. Applicare Gauss-Jordan
        for (int i = 0; i < n; i++) {
            // Cerca il pivot (elemento massimo nella colonna i, dalla riga i in giù)
            int pivotRow = i;
            for (int k = i + 1; k < n; k++) {
            	if (magnitudeComparator.compare(augmented[k][i], augmented[pivotRow][i]) > 0) {
                     pivotRow = k;
                }
            }

            // Scambia le righe se necessario (sposta il pivot alla riga corrente i)
            if (pivotRow != i) {
                K[] temp = augmented[i];
                augmented[i] = augmented[pivotRow];
                augmented[pivotRow] = temp;
            }

            // Se l'elemento pivot è zero anche dopo lo scambio, la matrice è singolare
            if (augmented[i][i].isEqual(elementZero)) {
                throw new ArithmeticException("Matrix is singular and cannot be inverted.");
            }

            // Normalizza la riga pivot: dividi tutta la riga per l'elemento pivot
            K pivot = augmented[i][i];
            for (int j = i; j < 2 * n; j++) {
                augmented[i][j] = augmented[i][j].divide(pivot);
            }

            // Elimina gli altri elementi della colonna: sottrai multipli della riga i dalle altre righe
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    K factor = augmented[k][i];
                    for (int j = i; j < 2 * n; j++) {
                        // R[k] = R[k] - factor * R[i]
                        K product = factor.multiply(augmented[i][j]);
                        augmented[k][j] = augmented[k][j].subtract(product);
                    }
                }
            }
        }

        // 3. Estrai la matrice inversa (la metà destra della matrice aumentata)
        K[][] inverseMatrix = (K[][]) new FieldElement[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(augmented[i], n, inverseMatrix[i], 0, n);
        }

        return inverseMatrix;
    }
}
