
package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

/**
 * Definisce un Problema di Punto Fisso: trovare T_k+1 = G(T_k).
 * T: Lo stato del sistema (il tipo di elemento iterato).
 */
public interface FixedPointProblem<T extends AlgebraicElement<T>> 
extends AnalysisProblem<T>
{
    /**
     * La funzione di mappatura G(x) che definisce il passo iterativo: x_{k+1} = G(x_k).
     * @param current Lo stato T all'iterazione k.
     * @return Lo stato T all'iterazione k+1.
     */
    T nextIteration(T current);
}
