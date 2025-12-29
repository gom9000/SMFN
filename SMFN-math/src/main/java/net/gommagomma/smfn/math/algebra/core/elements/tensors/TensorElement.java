package net.gommagomma.smfn.math.algebra.core.elements.tensors;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Interfaccia fondamentale per tutti gli elementi strutturati N-dimensionali (Tensori),
 * di rango 1 o superiore. Incapsula le proprietà strutturali e l'accesso ai componenti.
 * @param <E> Il tipo del tensore stesso
 * @param <K> Il tipo di scalare contenuto
 */
public interface TensorElement<E extends TensorElement<E, K>, K extends ScalarElement<K>> 
extends AlgebraicElement<E>
{
    int rank();
    int[] getShape();
    long size();
    K get(int... indices);
}