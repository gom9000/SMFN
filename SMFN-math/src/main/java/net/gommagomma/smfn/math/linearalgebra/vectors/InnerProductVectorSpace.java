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
			K prod = scalarStructure.multiply(a.get(i), b.get(i));
			result = scalarStructure.add(result, prod);
		}
		return result;
	}

	@Override
	public Real norm(Vector<K> v) {
		K dot = innerProduct(v, v);
		return scalarStructure.magnitude(dot).sqrt();
	}
}