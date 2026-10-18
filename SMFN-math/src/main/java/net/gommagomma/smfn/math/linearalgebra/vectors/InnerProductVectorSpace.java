package net.gommagomma.smfn.math.linearalgebra.vectors;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.metric.InnerProductSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;

public class InnerProductVectorSpace<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
extends VectorSpace<K, S>
implements InnerProductSpace<Vector<K>, K, S>
{
	public InnerProductVectorSpace(S scalarStructure, int dimension) {
		super(scalarStructure, dimension);
	}

	@Override
	public K innerProduct(Vector<K> a, Vector<K> b) {
		K result = scalarStructure.zero();
		for (int i = 0; i < dimension; i++) {
			// Prodotto interno hermitiano: <a,b> = sum conj(a_i) * b_i
			K conjAi = conjugateIfPossible(a.get(i));
			K prod = scalarStructure.multiply(conjAi, b.get(i));
			result = scalarStructure.add(result, prod);
		}
		return result;
	}

	@Override
	public Real norm(Vector<K> v) {
		// Norma scalata per la componente di modulo massimo: sommare direttamente i quadrati
		// andrebbe in overflow oltre ~1e154 e in underflow sotto ~1e-154.
		double[] magnitudes = new double[dimension];
		double max = 0.0;
		for (int i = 0; i < dimension; i++) {
			magnitudes[i] = scalarStructure.magnitude(v.get(i)).getValue();
			if (Double.isNaN(magnitudes[i])) return new Real(Double.NaN);
			max = Math.max(max, magnitudes[i]);
		}
		if (max == 0.0 || Double.isInfinite(max)) return new Real(max);
		double sum = 0.0;
		for (double magnitude : magnitudes) {
			double ratio = magnitude / max;
			sum += ratio * ratio;
		}
		return new Real(max * Math.sqrt(sum));
	}
}
