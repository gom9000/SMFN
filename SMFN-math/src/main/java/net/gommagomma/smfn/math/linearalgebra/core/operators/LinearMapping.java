package net.gommagomma.smfn.math.linearalgebra.core.operators;

import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;

public interface LinearMapping<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends LinearMapping<K, V, M>> 
extends Morphism<V, V>
{
	default V transform(V vector) {
		return apply(vector);
	}
}