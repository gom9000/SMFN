package net.gommagomma.smfn.math.algebra.core.structures.metric;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public interface InnerProductSpace<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> 
extends NormedSpace<V, K, S>
{
	K innerProduct(V a, V b);
}
