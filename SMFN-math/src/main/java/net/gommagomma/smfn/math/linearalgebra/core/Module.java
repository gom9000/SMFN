package net.gommagomma.smfn.math.linearalgebra.core;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;

// V è il tipo del vettore (es. SignedIntVector)
// K è il tipo dello scalare (es. SignedInt)
public interface Module<V extends VectorElement<K, V>, K extends RingElement<K>> 
extends Space<V>
{
    /**
     * Restituisce l'anello degli scalari su cui è definito questo modulo.
     */
    Ring<K> getScalarRing();
}