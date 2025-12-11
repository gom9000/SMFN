package net.gommagomma.smfn.math.linearalgebra.core.elements.vectors;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;


public interface NormedVectorElement<K extends FieldElement<K, ?> & NormableElement<Real, K>, V extends NormedVectorElement<K, V>>
extends VectorElement<K, V>, NormableElement<Real, V>
{
    default Real distanceTo(V other) {
        if (dimension() != other.dimension()) {
            throw new IllegalArgumentException("Vectors must have the same dimension to calculate distance.");
        }
        
        V diff = this.subtract(other);
        return diff.norm();
    }
}