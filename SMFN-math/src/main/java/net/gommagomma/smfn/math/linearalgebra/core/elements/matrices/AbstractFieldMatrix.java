package net.gommagomma.smfn.math.linearalgebra.core.elements.matrices;


import java.util.Comparator;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.FieldMatrixSpace;


public abstract class AbstractFieldMatrix<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>, S extends FieldMatrixSpace<K, V, M>>
extends AbstractRingMatrix<K, V, M, S>
implements FieldMatrixElement<K, V, M>
{
    protected AbstractFieldMatrix(K[][] data, S matrixStructure) {
        super(data, matrixStructure);
    }

    protected AbstractFieldMatrix(int rows, int cols, S matrixStructure) {
    	super(rows, cols, matrixStructure);
    }


    /**
     * Restituisce un comparatore per confrontare la magnitudo (es. valore assoluto) degli scalari K, 
     * necessario per il pivoting negli algoritmi come l'eliminazione gaussiana.
     * Deve essere implementato dalle classi concrete.
     */
    protected abstract Comparator<K> getMagnitudeComparator();


    @Override // FieldMatrixElement impls
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

        K det = matrixStructure.getScalarStructure().one();
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
            if (A[i][i].isMathematicallyEqualTo(matrixStructure.getScalarStructure().zero())) {
                return matrixStructure.getScalarStructure().zero();
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

    @Override // FieldMatrixElement impls
    public M inverse() {
        if (getColumns() != getRows()) {
            throw new IllegalArgumentException("Inverse can only be calculated for square matrices.");
        }
        int n = getRows();
        K zero = matrixStructure.getScalarStructure().zero();
        K one = matrixStructure.getScalarStructure().one();
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
            if (pivot.isMathematicallyEqualTo(zero)) {
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
        return matrixStructure.createMatrix(inverseData);
    }
}
