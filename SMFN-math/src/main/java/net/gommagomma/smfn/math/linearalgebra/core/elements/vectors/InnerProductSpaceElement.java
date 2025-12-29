package net.gommagomma.smfn.math.linearalgebra.core.elements.vectors;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Normable;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Rappresenta un elemento in uno spazio con prodotto interno (Inner Product Space).
 * Estende NormedVectorElement, poiché un prodotto interno induce naturalmente una norma.
 */
public interface InnerProductSpaceElement<K extends FieldElement<K> & Normable<Real, K>, V extends InnerProductSpaceElement<K, V>>
extends NormedVectorElement<K, V>
{
    /**
     * Calcola il prodotto scalare (prodotto interno) tra questo vettore e un altro.
     * @param other L'altro vettore.
     * @return Il risultato scalare K.
     */
    K dotProduct(V other);
}

