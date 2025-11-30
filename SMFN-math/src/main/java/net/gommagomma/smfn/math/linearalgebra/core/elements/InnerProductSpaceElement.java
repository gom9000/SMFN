package net.gommagomma.smfn.math.linearalgebra.core.elements;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;

/**
 * Rappresenta un elemento in uno spazio con prodotto interno (Inner Product Space).
 * Estende NormedVectorElement, poiché un prodotto interno induce naturalmente una norma.
 */
public interface InnerProductSpaceElement<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>>
extends NormedVectorElement<K, V>
{
    /**
     * Calcola il prodotto scalare (prodotto interno) tra questo vettore e un altro.
     * @param other L'altro vettore.
     * @return Il risultato scalare K.
     */
    K dotProduct(V other);
}

