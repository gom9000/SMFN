package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixModule;

/**
 * Rappresenta l'Anello delle matrici quadrate n x n sopra un Anello K.
 * Unisce le capacità di MatrixModule (sottrazione) e MatrixSemiring (moltiplicazione).
 */
public class SquareMatrixRing<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>>
extends SquareMatrixSemiring<K, S>
implements Ring<SquareMatrix<K>>, Module<SquareMatrix<K>, K, S>
{
	private final MatrixModule<K, S> moduleDelegate;

    public SquareMatrixRing(S scalarStructure, int n) {
    	super(scalarStructure, n);
        this.moduleDelegate = new MatrixModule<>(scalarStructure, n, n);
    }

    @Override
    public String getName() {
        return "Square Matrix Ring (" + n + "x" + n + ") over " + scalarStructure.getName();
    }

    @Override
    public SquareMatrix<K> negate(SquareMatrix<K> m) {
        Matrix<K> negated = moduleDelegate.negate(m.asMatrix());
        return of(negated.getData());
    }

    @Override
    public SquareMatrix<K> subtract(SquareMatrix<K> a, SquareMatrix<K> b) {
        Matrix<K> result = moduleDelegate.subtract(a.asMatrix(), b.asMatrix());
        return of(result.getData());
    }


	@Override
	public Real magnitude(SquareMatrix<K> element) {
		return super.magnitude(element);
	}


    /**
     * Calcola il determinante per via combinatoria (Laplace).
     * Non richiede la divisione, quindi funziona su Anelli (Polinomi, Interi).
     */
	public K determinant(SquareMatrix<K> m) {
        validateDimensions(m);
        return computeRecursive(m);
    }

    private K computeRecursive(SquareMatrix<K> m) {
        int size = m.getN();
        if (size == 1) return m.get(0, 0);
        if (size == 2) {
            K ad = scalarStructure.multiply(m.get(0, 0), m.get(1, 1));
            K bc = scalarStructure.multiply(m.get(0, 1), m.get(1, 0));
            return scalarStructure.subtract(ad, bc);
        }

        K det = scalarStructure.zero();
        for (int j = 0; j < size; j++) {
            K element = m.get(0, j);
            if (scalarStructure.isZero(element)) continue;

            SquareMatrix<K> minor = getMinor(m, 0, j);
            K cofactor = computeRecursive(minor);
            K term = scalarStructure.multiply(element, cofactor);

            if (j % 2 == 1) det = scalarStructure.subtract(det, term);
            else det = scalarStructure.add(det, term);
        }
        return det;
    }

    @SuppressWarnings("unchecked")
    private SquareMatrix<K> getMinor(SquareMatrix<K> m, int r, int c) {
        int minorN = m.getN() - 1;
        K[] minorData = (K[]) new ScalarElement[minorN * minorN];
        int rowIdx = 0;
        for (int i = 0; i < m.getN(); i++) {
            if (i == r) continue;
            int colIdx = 0;
            for (int j = 0; j < m.getN(); j++) {
                if (j == c) continue;
                minorData[rowIdx * minorN + colIdx] = m.get(i, j);
                colIdx++;
            }
            rowIdx++;
        }
        return new SquareMatrixRing<>(scalarStructure, minorN).of(minorData);
    }

    private void validateDimensions(SquareMatrix<K> m) {
        if (m.getN() != n) {
            throw new IllegalArgumentException("Dimensione " + m.getN() + "x" + m.getN() + " incompatibile con anello di ordine " + n);
        }
    }
}