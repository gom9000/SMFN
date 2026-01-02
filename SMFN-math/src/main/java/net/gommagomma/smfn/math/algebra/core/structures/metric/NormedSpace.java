package net.gommagomma.smfn.math.algebra.core.structures.metric;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.LinearSpace;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;

public interface NormedSpace<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> 
extends LinearSpace<V, K, S>, MetricSpace<V>
{
	Real norm(V v);

	@Override
    default Real distance(V a, V b) {
        V difference = subtract(a, b); 
        return norm(difference);
    }
}
