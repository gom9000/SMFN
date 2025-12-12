package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;
import net.gommagomma.smfn.math.linearalgebra.core.factories.VectorElementFactory;


public interface Semimodule<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>>
extends Space<V>, VectorElementFactory<K, V>
{
	/**
     * Restituisce il semianello degli scalari su cui è definito questo semimodulo.
     */
	Semiring<K> getScalarStructure();
}
