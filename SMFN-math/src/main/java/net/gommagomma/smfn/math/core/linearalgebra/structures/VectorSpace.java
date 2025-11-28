package net.gommagomma.smfn.math.core.linearalgebra.structures;

import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.algebra.structures.Field;
import net.gommagomma.smfn.math.core.linearalgebra.elements.VectorElement;

// V è il tipo del vettore (es. RealVector)
// K è il tipo dello scalare (es. Real)
// Estende Module, ma vincola R a essere K (un FieldElement)
public interface VectorSpace<K extends FieldElement<K>, V extends VectorElement<K, V>> 
extends Module<K, V>
{
    /**
     * Restituisce il campo degli scalari.
     * Sovrascrive getScalarRing per restituire esplicitamente un Field.
     */
	@Override
    Field<K> getScalarRing();
    
    // Metodi specifici per spazi vettoriali (es. base ortonormale, etc.)
}