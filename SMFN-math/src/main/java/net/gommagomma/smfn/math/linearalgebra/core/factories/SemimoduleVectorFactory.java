package net.gommagomma.smfn.math.linearalgebra.core.factories;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;


public interface SemimoduleVectorFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>>
{
    NumericFactory<K> getScalarFactory();

    V createVector(int dimension);
    V createVector(K[] data);

    default V createVector(double... data) {
        NumericFactory<K> scalars = getScalarFactory();

        K[] components = (K[]) new SemiringElement[data.length]; 

        for (int i = 0; i < data.length; i++) {
            components[i] = scalars.fromDouble(data[i]);
        }

        return createVector(components);
    }
}