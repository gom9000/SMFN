package net.gommagomma.smfn.math.linearalgebra.core.elements.vectors;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.LinearCombinable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;


public interface ModuleElement<K extends RingElement<K>, V extends ModuleElement<K, V>> 
extends SemimoduleElement<K, V>, LinearCombinable<K, V>
{}
