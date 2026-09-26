package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Capacita' opzionale di un {@link SolverResult}: espone il residuo finale ||F(x_k)||
 * dell'equazione risolta dal solutore.
 */
public interface ResidualAware
{
	/**
     * Restituisce la norma del residuo finale ||F(x_k)|| dell'equazione al punto restituito da {@link SolverResult#getValue()}.
     *
     * @return Il valore reale del residuo finale
     */
	Real getFinalResidual();
}
