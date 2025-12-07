package net.gommagomma.smfn.math.linearalgebra.core.factories;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.RingMatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.ModuleElement;


public interface RingMatrixFactory<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>>
extends SemiringMatrixFactory<K, V, M>
{}
