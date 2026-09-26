package net.gommagomma.smfn.math.analysis.core.solvers;

/**
 * Esito completo prodotto da un solutore numerico: il valore calcolato insieme
 * ai metadati universali del processo che lo ha prodotto.
 * <p>
 * Contiene solo le informazioni comuni a qualunque solutore della libreria, che sia o meno
 * un {@link IterativeSolver} in senso stretto (es. anche {@code EigenvalueSolver}, che non riceve
 * un {@link net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace} esterno):
 * il valore finale, l'esito di terminazione e il numero di iterazioni eseguite.
 * <p>
 * Diagnostiche che non sono universali a tutti i solutori non fanno parte di questo contratto,
 * ma sono esposte come capacita' opzionali su interfacce a parte, verificate con {@code instanceof}
 * da chi consuma il risultato -- cosi' come la libreria fa gia' per le capacita' opzionali dei
 * problemi (es. {@code DifferentiableScalarProblem}):
 * <ul>
 *   <li>{@link StepDistanceAware} -- la distanza tra gli ultimi due iterati, disponibile solo quando
 *       il solutore riceve un {@code MetricSpace} esterno sullo spazio di iterazione (es. Newton,
 *       tempo di fuga), non per un solutore con un proprio criterio di arresto interno (es. Jacobi,
 *       che si ferma sulla norma della parte fuori diagonale, non su una distanza tra iterati).</li>
 *   <li>{@link ResidualAware} -- il residuo ||F(x)|| di un'equazione, definito solo per un problema
 *       di ricerca degli zeri, non per un problema di punto fisso o per una decomposizione spettrale.</li>
 * </ul>
 *
 * @param <R> Il tipo del valore calcolato dal solutore
 */
public interface SolverResult<R>
{
	/**
     * Restituisce il valore calcolato dal solutore: la soluzione esatta se lo stato e' {@link ConvergenceStatus#CONVERGED},
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
	ConvergenceStatus getStatus();

	/**
     * Restituisce il numero di iterazioni effettivamente eseguite prima dell'arresto.
     *
     * @return Il conteggio delle iterazioni (k >= 0)
     */
	int getIterationsExecuted();
}
