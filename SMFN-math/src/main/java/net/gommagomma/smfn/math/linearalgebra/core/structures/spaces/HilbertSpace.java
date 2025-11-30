package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces; 


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.InnerProductSpaceElement;


public interface HilbertSpace<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> 
extends InnerProductSpace<K, V> 
{
    // La completezza è un'assunzione di design.
}
