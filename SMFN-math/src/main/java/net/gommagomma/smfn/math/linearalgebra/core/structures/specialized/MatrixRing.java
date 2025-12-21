package net.gommagomma.smfn.math.linearalgebra.core.structures.specialized;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.linearalgebra.core.elements.matrices.RingMatrixElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.ModuleElement;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.RingMatrixModule;

//Definisce una struttura che è sia un Modulo Matrice (per l'algebra lineare) che un Anello (per l'algebra astratta)
public interface MatrixRing<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>>
extends Ring<M>, RingMatrixModule<K, V, M>
{
    M getIdentity();
}
