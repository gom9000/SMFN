package net.gommagomma.smfn.math.linearalgebra.core.operators;

import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Scalable;

public interface LinearMapping<K, V, M extends LinearMapping<K, V, M>> 
extends Scalable<K, M>, Morphism<V, V>
{
	default V transform(V vector) {
		return apply(vector);
	}
}