package net.gommagomma.smfn.math.linearalgebra.core.elements.vectors;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Scalable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.elements.tensors.TensorElement;


/**
 * Rappresenta un elemento di un Semimodulo.
 * Generalizzato sul tipo di scalare K (che deve essere un SemiringElement)
 * e sul tipo del vettore V.
 */
public interface SemimoduleElement<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>>
extends SpaceElement<V>, Scalable<K, V>, TensorElement<K>
{
    int dimension(); 
    K get(int index);
}