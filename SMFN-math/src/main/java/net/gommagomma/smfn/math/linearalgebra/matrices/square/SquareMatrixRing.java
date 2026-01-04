package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixModule;

/**
 * Rappresenta l'Anello delle matrici quadrate n x n sopra un Anello K.
 * Unisce le capacità di MatrixModule (sottrazione) e MatrixSemiring (moltiplicazione).
 */
public class SquareMatrixRing<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>>
extends MatrixModule<K, S>
implements Ring<Matrix<K>>, ScalarStructure<Matrix<K>>
{
    private final SquareMatrixSemiring<K, S> semiringDelegate;

    public SquareMatrixRing(S scalarStructure, int n) {
        super(scalarStructure, n, n);
        // Deleghiamo la logica della moltiplicazione al semiring per non duplicare il codice
        this.semiringDelegate = new SquareMatrixSemiring<>(scalarStructure, n);
    }

    @Override
    public Matrix<K> one() {
        return semiringDelegate.one();
    }

    @Override
    public Matrix<K> multiply(Matrix<K> a, Matrix<K> b) {
        return semiringDelegate.multiply(a, b);
    }

    @Override
    public boolean isOne(Matrix<K> element) {
        return semiringDelegate.isOne(element);
    }
    
    @Override
    public String getName() {
        return "Matrix Ring (" + rows + "x" + cols + ") over " + scalarStructure.getName();
    }

	@Override
    public boolean isExact() {
        return semiringDelegate.isExact();
    }

	@Override
	public Real magnitude(Matrix<K> element) {
		return scalarStructure.magnitude(null);
	}


    public Matrix<K> multiply(Matrix<K> a, Matrix<K> b) {
        if (a.getCols() != b.getRows()) {
            throw new IllegalArgumentException("Dimensioni incompatibili per la moltiplicazione: " + 
                "colonne di A (" + a.getCols() + ") != righe di B (" + b.getRows() + ")");
        }

        // Il risultato ha le righe della prima e le colonne della seconda
        Matrix<K> result = new Matrix<>(a.getRows(), b.getCols(), scalarStructure);

        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < b.getCols(); j++) {
                K sum = scalarStructure.zero();
                for (int k = 0; k < a.getCols(); k++) {
                    // sum += a[i][k] * b[k][j]
                    K prod = scalarStructure.multiply(a.get(i, k), b.get(k, j));
                    sum = scalarStructure.add(sum, prod);
                }
                result.set(i, j, sum);
            }
        }
        return result;
    }


    /**
     * Calcola il determinante per via combinatoria (Laplace).
     * Non richiede la divisione, quindi funziona su Anelli (Polinomi, Interi).
     */
    public K determinant(Matrix<K> m) {
        if (m.getRows() != m.getCols()) {
            throw new IllegalArgumentException("La matrice deve essere quadrata.");
        }
        int n = m.getRows();

        // Casi base per efficienza
        if (n == 0) return scalarStructure.one(); 
        if (n == 1) return m.get(0, 0);
        if (n == 2) {
            // ad - bc
            K ad = scalarStructure.multiply(m.get(0, 0), m.get(1, 1));
            K bc = scalarStructure.multiply(m.get(0, 1), m.get(1, 0));
            return scalarStructure.subtract(ad, bc);
        }

        K det = scalarStructure.zero();
        for (int j = 0; j < n; j++) {
            // Sviluppo lungo la prima riga
            K element = m.get(0, j);
            if (scalarStructure.isZero(element)) continue;

            K cofactor = determinant(getMinor(m, 0, j));
            K term = scalarStructure.multiply(element, cofactor);

            if (j % 2 == 1) {
                det = scalarStructure.subtract(det, term);
            } else {
                det = scalarStructure.add(det, term);
            }
        }
        return det;
    }

    /**
     * Restituisce la sottomatrice (n-1)x(n-1) eliminando riga r e colonna c.
     */
    private Matrix<K> getMinor(Matrix<K> m, int r, int c) {
        int n = m.getRows();
        Matrix<K> minor = new Matrix<>(n - 1, n - 1, scalarStructure);
        int rowIdx = 0;
        for (int i = 0; i < n; i++) {
            if (i == r) continue;
            int colIdx = 0;
            for (int j = 0; j < n; j++) {
                if (j == c) continue;
                minor.set(rowIdx, colIdx, m.get(i, j));
                colIdx++;
            }
            rowIdx++;
        }
        return minor;
    }
}