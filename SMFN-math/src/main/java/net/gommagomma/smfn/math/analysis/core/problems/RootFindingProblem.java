package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;

/**
 * Definisce un problema di ricerca di radici, V t.c. F(V) = Zero.
 * K: Tipo dello scalare (es. Real, Complex).
 * V: Tipo del dominio (es. Vettore) e del codominio della funzione F.
 */
public interface RootFindingProblem<K extends FieldElement<K>, V extends VectorElement<K, V>>
extends AnalysisProblem<V>
{
    /**
     * La funzione F(V) il cui zero deve essere trovato.
     * @return La MathFunction F.
     */
	Mapping<V, V> getFunction();
}