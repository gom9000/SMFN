package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.Morphism;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Definisce un problema di ricerca di radici, V t.c. F(V) = Zero.
 * K: Tipo dello scalare (es. Real, Complex).
 * V: Tipo del dominio (es. Vettore) e del codominio della funzione F.
 */
public interface RootFindingProblem<P extends AlgebraicElement<P>>
extends AnalysisProblem<P>
{
    /**
     * La funzione F(V) il cui zero deve essere trovato.
     * @return La MathFunction F.
     */
	Morphism<P, P> getFunction();
}