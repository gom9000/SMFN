package net.gommagomma.smfn.math.linearalgebra.core;


import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;


public interface Module<K extends RingElement<K>, V extends ModuleElement<K, V>> 
extends Space<V>
{
    /**
     * Restituisce l'anello degli scalari su cui è definito questo modulo.
     */
    Ring<K> getScalarRing();
}