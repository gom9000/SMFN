package net.gommagomma.smfn.math.linearalgebra.core.elements;

import net.gommagomma.smfn.math.algebra.core.elements.additive.AbelianGroupElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;


public interface RingMatrixElement<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>> 
extends SemiringMatrixElement<K, V, M>, AbelianGroupElement<M>
{}
