package net.gommagomma.smfn.math.analysis.numerical.solvers.linear;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Risolve Ax = b per eliminazione di Gauss con sostituzione all'indietro.
 */
public final class GaussianEliminationSolver<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
{
	private final S scalarStructure;
	private final int n;
	private final MatrixSpace<K, S> augmentedSpace;
	private final VectorSpace<K, S> vectorSpace;

	public GaussianEliminationSolver(S scalarStructure, int n) {
		this.scalarStructure = scalarStructure;
		this.n = n;
		this.augmentedSpace = new MatrixSpace<>(scalarStructure, n, n + 1);
		this.vectorSpace = new VectorSpace<>(scalarStructure, n);
	}

	public Vector<K> solve(SquareMatrix<K> A, Vector<K> b) {
		@SuppressWarnings("unchecked")
		K[] augmentedData = (K[]) new ScalarElement[n * (n + 1)];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				augmentedData[i * (n + 1) + j] = A.get(i, j);
			}
			augmentedData[i * (n + 1) + n] = b.get(i);
		}
		Matrix<K> augmented = augmentedSpace.of(augmentedData);
		Matrix<K> reduced = augmentedSpace.toRowEchelonForm(augmented);

		@SuppressWarnings("unchecked")
		K[] x = (K[]) new ScalarElement[n];
		for (int i = n - 1; i >= 0; i--) {
			K sum = reduced.get(i, n);
			for (int j = i + 1; j < n; j++) {
				sum = scalarStructure.subtract(sum, scalarStructure.multiply(reduced.get(i, j), x[j]));
			}
			K pivot = reduced.get(i, i);
			if (scalarStructure.isZero(pivot)) {
				throw new ArithmeticException("Il sistema non ha una soluzione unica (matrice dei coefficienti singolare).");
			}
			x[i] = scalarStructure.divide(sum, pivot);
		}
		return vectorSpace.of(x);
	}
}
