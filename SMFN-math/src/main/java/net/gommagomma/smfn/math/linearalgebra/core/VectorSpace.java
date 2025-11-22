package net.gommagomma.smfn.math.linearalgebra.core;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;

// V è il tipo del vettore (es. RealVector)
// K è il tipo dello scalare (es. Real)
// Estende Module, ma vincola R a essere K (un FieldElement)
public interface VectorSpace<V extends VectorElement<K, V>, K extends FieldElement<K>> 
extends Module<V, K>
{
    /**
     * Restituisce il campo degli scalari.
     * Sovrascrive getScalarRing per restituire esplicitamente un Field.
     */
	@Override
    Field<K> getScalarRing();
    
    // Metodi specifici per spazi vettoriali (es. base ortonormale, etc.)
}