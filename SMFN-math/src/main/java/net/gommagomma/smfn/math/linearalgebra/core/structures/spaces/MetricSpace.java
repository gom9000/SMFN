package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;

/**
 * Rappresenta uno Spazio Metrico (X, d), dove X è un insieme di elementi T 
 * e d è una funzione di distanza (metrica) d: X * X -> Real.
 */
public interface MetricSpace<K extends SemiringElement<K>, T extends AlgebraicElement<T>>
extends LinearSpace<K, T>
{
    /**
     * Calcola la distanza tra due punti nello spazio metrico.
     * Deve soddisfare gli assiomi della metrica:
     * 1. d(a, b) >= 0 (Non-negatività)
     * 2. d(a, b) = 0 <=> a = b (Identità degli indiscernibili)
     * 3. d(a, b) = d(b, a) (Simmetria)
     * 4. d(a, c) <= d(a, b) + d(b, c) (Disuguaglianza triangolare)
     * 
     * @param point1 Il primo punto.
     * @param point2 Il secondo punto.
     * @return La distanza tra i due punti (un valore Real non negativo).
     */
    Real distance(T point1, T point2);
}
