package net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.analysis.core.functions.MultivariateFunction;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

/**
 * Stima il gradiente di una MultivariateFunction<K> per differenze centrali,
 * componente per componente: grad_i(v) ~ (f(v+h*e_i) - f(v-h*e_i)) / (2h).
 *
 * Fallback generico per chi non implementa DifferentiableMultivariateFunction.
 */
public class CentralDifferenceGradientEstimator<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
{
	private final S field;
	private final K h;

	public CentralDifferenceGradientEstimator(S field, K h) {
		if (h == null || field.isZero(h)) {
			throw new IllegalArgumentException("Il passo h non puo' essere nullo o zero.");
		}
		this.field = field;
		this.h = h;
	}

	public Vector<K> estimateAt(MultivariateFunction<K> f, Vector<K> v) {
		int n = (int) v.size();
		VectorSemimodule<K, S> space = new VectorSemimodule<>(field, n);
		K twoH = field.add(h, h);

		@SuppressWarnings("unchecked")
		K[] gradient = (K[]) new ScalarElement[n];
		for (int i = 0; i < n; i++) {
			Vector<K> vPlus = perturb(space, v, i, h, n);
			Vector<K> vMinus = perturb(space, v, i, field.negate(h), n);
			K fPlus = f.apply(vPlus);
			K fMinus = f.apply(vMinus);
			gradient[i] = field.divide(field.subtract(fPlus, fMinus), twoH);
		}
		return space.of(gradient);
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
