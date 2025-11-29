package net.gommagomma.smfn.math.linearalgebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.elements.InnerProductSpaceElement;


public interface InnerProductSpace<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> 
extends VectorSpace<K, V>, MetricSpace<V>
{
    /**
     * Calcola il prodotto interno tra due elementi nello spazio.
     * Delega l'operazione all'elemento v1.
     * @param v1 Il primo elemento.
     * @param v2 Il secondo elemento.
     * @return Lo scalare K risultante dal prodotto interno.
     */
    default K innerProduct(V v1, V v2) {
        return v1.dotProduct(v2);
    }

    /**
     * Implementazione della distanza richiesta da MetricSpace<V>.
     * Deriva la distanza dalla norma L2 indotta dal prodotto interno.
     */
    @Override
    default Real distance(V point1, V point2) {
        return point1.distanceTo(point2);
    }
}
