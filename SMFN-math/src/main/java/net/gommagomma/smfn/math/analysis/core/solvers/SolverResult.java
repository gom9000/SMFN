package net.gommagomma.smfn.math.analysis.core.solvers;

/**
 * Esito completo prodotto da un solutore numerico: il valore calcolato insieme
 * ai metadati del processo che lo ha prodotto.
 * 
 * @param <R> Il tipo del valore calcolato dal solutore
 */
public interface SolverResult<R>
{
	/**
     * Restituisce il valore calcolato dal solutore: la soluzione esatta se lo stato e' {@link TerminationStatus#CONVERGED},
     * altrimenti la migliore approssimazione disponibile al momento dell'arresto.
     *
     * @return Il valore prodotto dal processo del solutore
     */
	R getValue();

	/**
     * Restituisce l'esito di terminazione del processo del solutore.
     *
     * @return Lo stato di convergenza raggiunto dal solutore
     */
	TerminationStatus getStatus();

	/**
     * Restituisce il numero di iterazioni effettivamente eseguite prima dell'arresto.
     *
     * @return Il conteggio delle iterazioni (k >= 0)
     */
	int getIterationsExecuted();
}
