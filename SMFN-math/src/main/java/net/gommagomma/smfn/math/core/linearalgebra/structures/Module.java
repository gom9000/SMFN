package net.gommagomma.smfn.math.core.linearalgebra.structures;


import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.core.algebra.structures.Ring;
import net.gommagomma.smfn.math.core.linearalgebra.elements.ModuleElement;


public interface Module<K extends RingElement<K>, V extends ModuleElement<K, V>> 
extends Space<V>
{
    /**
     * Restituisce l'anello degli scalari su cui è definito questo modulo.
     */
    Ring<K> getScalarRing();
}