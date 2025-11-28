package net.gommagomma.smfn.math.core.analysis.structures; 


import net.gommagomma.smfn.math.core.algebra.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.algebra.numeric.Real;
import net.gommagomma.smfn.math.core.linearalgebra.elements.InnerProductSpaceElement;
import net.gommagomma.smfn.math.core.linearalgebra.structures.InnerProductSpace;


public interface HilbertSpace<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> 
extends InnerProductSpace<K, V> 
{
    // La completezza è un'assunzione di design.
}
