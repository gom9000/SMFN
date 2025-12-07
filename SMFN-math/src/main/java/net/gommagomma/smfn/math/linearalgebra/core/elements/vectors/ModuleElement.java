package net.gommagomma.smfn.math.linearalgebra.core.elements.vectors;


import net.gommagomma.smfn.math.algebra.core.elements.additive.AbelianGroupElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;


public interface ModuleElement<K extends RingElement<K>, V extends ModuleElement<K, V>> 
extends SemimoduleElement<K, V>, AbelianGroupElement<V>
{}
