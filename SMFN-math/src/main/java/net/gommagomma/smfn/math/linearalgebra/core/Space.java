package net.gommagomma.smfn.math.linearalgebra.core;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.AlgebraicStructure;

public interface Space<V extends AlgebraicElement<V>>
extends AlgebraicStructure<V>
{
    /**
     * Restituisce la dimensione (o rango) dello spazio.
     * Per spazi infiniti, potrebbe richiedere un approccio diverso (es. Optional<Integer> o -1).
     */
    int dimension();
}