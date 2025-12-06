package net.gommagomma.smfn.math.linearalgebra.core.factories;

import java.lang.reflect.Array;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;


public interface SemimoduleVectorFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>>
{
    NumericFactory<K> getScalarFactory();
    Class<K> getScalarClass();

    V createVector(int dimension);
    V createVector(K[] data);

    default V createVector(double... data) {
        NumericFactory<K> scalars = getScalarFactory();

        K[] components = (K[]) Array.newInstance(getScalarClass(), data.length); 

        for (int i = 0; i < data.length; i++) {
            components[i] = scalars.fromDouble(data[i]);
        }

        return createVector(components);
    }
}