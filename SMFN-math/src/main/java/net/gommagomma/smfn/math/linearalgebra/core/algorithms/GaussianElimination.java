package net.gommagomma.smfn.math.linearalgebra.core.algorithms;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import java.util.Comparator;

public final class GaussianElimination
{
    private GaussianElimination() { /* Impedisce l'instanziazione */ }

    @SuppressWarnings("unchecked")
    public static <K extends FieldElement<K>> K determinant(K[][] matrixData, K elementZero, Comparator<K> magnitudeComparator) {
        // ... [Il codice del determinante va qui dentro] ...
        int n = matrixData.length;
        if (matrixData.length != n) {
            throw new IllegalArgumentException("Matrix must be square to calculate the determinant.");
        }

        K[][] A = (K[][]) new FieldElement[n][n]; // Aggiungi @SuppressWarnings("unchecked") se necessario
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrixData[i], 0, A[i], 0, n);
        }

        K det = (K) elementZero.getOne();
        int sign = 1;

        for (int i = 0; i < n; i++) {
            int pivotRow = i;
            for (int k = i + 1; k < n; k++) {
                if (magnitudeComparator.compare(A[k][i], A[pivotRow][i]) > 0) {
                     pivotRow = k;
                }
            }

            if (pivotRow != i) {
                K[] temp = A[i];
                A[i] = A[pivotRow];
                A[pivotRow] = temp;
                sign *= -1;
            }

            if (A[i][i].isEqual(elementZero)) {
                return elementZero;
            }
            
            for (int k = i + 1; k < n; k++) {
                K factor = A[k][i].divide(A[i][i]);
                for (int j = i; j < n; j++) {
                    A[k][j] = A[k][j].subtract(factor.multiply(A[i][j]));
                }
            }
        }

        for (int i = 0; i < n; i++) {
            det = det.multiply(A[i][i]);
        }
        
        if (sign == -1) {
            det = det.negate();
        }

        return det;
    }
}
