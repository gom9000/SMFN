package net.gommagomma.smfn.math.linearalgebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.linearalgebra.core.elements.VectorElement;

// V è il tipo del vettore (es. RealVector)
// K è il tipo dello scalare (es. Real)
// Estende Module, ma vincola R a essere K (un FieldElement)
public interface VectorSpace<K extends FieldElement<K>, V extends VectorElement<K, V>> 
extends Module<K, V>
{
    /**
     * Restituisce il campo degli scalari.
     */
	@Override
    Field<K> getScalarStructure();
}