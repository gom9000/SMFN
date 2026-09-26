package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Capacita' opzionale di un {@link SolverResult}: espone la distanza tra gli ultimi due iterati
 * d(x_k, x_{k-1}) prodotti dal processo del solutore.
 */
public interface StepDistanceAware
{
	/**
     * Restituisce la distanza tra gli ultimi due iterati d(x_k, x_{k-1}), o zero se il processo
     * si e' arrestato prima di eseguire un solo passo.
     *
     * @return Il valore reale della distanza dell'ultimo passo eseguito
     */
	Real getFinalStepDistance();
}
