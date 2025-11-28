package net.gommagomma.smfn.math.core.linearalgebra.elements;

import net.gommagomma.smfn.math.core.algebra.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.algebra.numeric.Real;

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

    // Nota: la norma di default è già gestita in NormedVectorElement e usa subtract/norm.
    // Matematicamente, la norma dovrebbe essere la radice del prodotto interno di un vettore con se stesso: sqrt(dotProduct(v,v)).
    // Se vuoi far rispettare questa relazione, puoi sovrascrivere il metodo default 'norm()' qui.
}

