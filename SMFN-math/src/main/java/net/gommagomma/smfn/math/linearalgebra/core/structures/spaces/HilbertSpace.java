package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces; 


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Normable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.InnerProductSpaceElement;


public interface HilbertSpace<K extends FieldElement<K> & Normable<Real, K>, V extends InnerProductSpaceElement<K, V>> 
extends InnerProductSpace<K, V> 
{
    // La completezza è un'assunzione di design.
}
