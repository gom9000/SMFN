package net.gommagomma.smfn.math.linearalgebra.core;


import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Normable;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableOrderedFieldElement;


public interface NormedVectorElement<K extends NormableOrderedFieldElement<K>, V extends NormedVectorElement<K, V>>
extends VectorElement<K, V>, Normable<K, V>
{
    default K distanceTo(V other) {
        if (dimension() != other.dimension()) {
            throw new IllegalArgumentException("Vectors must have the same dimension to calculate distance.");
        }
        
        V diff = this.subtract(other);
        return diff.norm();
    }
}