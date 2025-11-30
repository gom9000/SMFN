package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces;


import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.ModuleElement;


public interface Module<K extends RingElement<K>, V extends ModuleElement<K, V>> 
extends Semimodule<K, V>
{
    /**
     * Restituisce l'anello degli scalari su cui è definito questo modulo.
     */
	@Override
    Ring<K> getScalarStructure();
}