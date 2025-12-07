package net.gommagomma.smfn.math.linearalgebra.core.factories;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;


public interface SemimoduleVectorFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>>
{
    NumericFactory<K> getScalarFactory();
    Class<K> getScalarClass();

    V createVector(K[] data);
    V createVector(double[] data);
    V createVector(long[] data);
    V createVector(int[] data);
    V createZeroVector(int dimension);

    default K getZeroScalar() { return getScalarFactory().zero(); }
    default K getOneScalar() { return getScalarFactory().one(); }
}