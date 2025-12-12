package net.gommagomma.smfn.math.linearalgebra.core.factories;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;


public interface VectorElementFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>>
{
    V createVector(K[] data);
    V createVector(double[] data);
    V createVector(long[] data);
    V createVector(int[] data);
    V createZeroVector(int dimension);
}