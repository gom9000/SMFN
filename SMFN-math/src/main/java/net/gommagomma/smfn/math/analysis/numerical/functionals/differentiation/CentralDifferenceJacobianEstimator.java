package net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation;

import java.util.List;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

/**
 * Stima l'intera Jacobiana n x n di F: Vector<K> -> Vector<K> per differenze
 * centrali, una colonna alla volta: perturbando la dimensione j si ottiene,
 * in una sola valutazione di F, la colonna j per tutte le righe insieme.
 *
 * Fallback generico per chi non implementa DifferentiableVectorProblem.
 */
public class CentralDifferenceJacobianEstimator<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
{
	private final S field;
	private final K h;

	public CentralDifferenceJacobianEstimator(S field, K h) {
		if (h == null || field.isZero(h)) {
			throw new IllegalArgumentException("The step h must not be null or zero.");
		}
		this.field = field;
		this.h = h;
	}

	public SquareMatrix<K> estimateAt(Mapping<Vector<K>, Vector<K>> f, Vector<K> v) {
		int n = (int) v.size();
		VectorSemimodule<K, S> space = new VectorSemimodule<>(field, n);
		K twoH = field.add(h, h);

		@SuppressWarnings("unchecked")
		K[] data = (K[]) new ScalarElement[n * n];

		for (int j = 0; j < n; j++) {
			Vector<K> vPlus = perturb(space, v, j, h, n);
			Vector<K> vMinus = perturb(space, v, j, field.negate(h), n);
			Vector<K> fPlus = f.apply(vPlus);
			Vector<K> fMinus = f.apply(vMinus);

			for (int i = 0; i < n; i++) {
				K derivative = field.divide(field.subtract(fPlus.get(i), fMinus.get(i)), twoH);
				data[i * n + j] = derivative; // riga i, colonna j
			}
		}

		return SquareMatrixElementFactory.of(field, List.of(data));
	}

	@SuppressWarnings("unchecked")
	private Vector<K> perturb(VectorSemimodule<K, S> space, Vector<K> v, int index, K delta, int n) {
		K[] data = (K[]) new ScalarElement[n];
		for (int i = 0; i < n; i++) {
			data[i] = (i == index) ? field.add(v.get(i), delta) : v.get(i);
		}
		return space.of(data);
	}
}
