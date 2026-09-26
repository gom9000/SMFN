package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Esito completo prodotto da un {@link IterativeSolver}: il valore calcolato insieme
 * ai metadati numerici del processo che lo ha prodotto.
 * <p>
 * Contiene solo le informazioni universali a qualunque solutore iterativo della libreria,
 * a prescindere dalla famiglia di problema servita (ricerca di zeri, punto fisso, tempo di fuga, ...):
 * il valore finale, l'esito di terminazione, il numero di iterazioni eseguite e la distanza
 * dell'ultimo passo -- quest'ultima sempre calcolabile perche' {@link IterativeSolver#solve}
 * riceve gia' obbligatoriamente un {@link net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace}
 * sullo spazio di iterazione.
 * <p>
 * Diagnostiche che non sono universali (es. il residuo ||F(x)|| di un'equazione, definito solo
 * per problemi di ricerca degli zeri e non, ad esempio, per un problema di punto fisso) non fanno
 * parte di questo contratto: sono esposte come capacita' opzionali su interfacce a parte
 * (es. {@link ResidualAware}), verificate con {@code instanceof} da chi consuma il risultato,
 * cosi' come la libreria fa gia' per le capacita' opzionali dei problemi (es. {@code DifferentiableScalarProblem}).
 *
 * @param <R> Il tipo del valore calcolato dal solutore
 */
public interface SolverResult<R>
{
	/**
     * Restituisce il valore calcolato dal solutore: la soluzione esatta se lo stato e' {@link ConvergenceStatus#CONVERGED},
     * altrimenti la migliore approssimazione disponibile al momento dell'arresto.
     *
     * @return Il valore prodotto dal processo iterativo
     */
	R getValue();

	/**
     * Restituisce l'esito di terminazione del processo iterativo.
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

	/**
     * Restituisce la distanza tra gli ultimi due iterati d(x_k, x_{k-1}), o zero se il processo
     * si e' arrestato prima di eseguire un solo passo.
     *
     * @return Il valore reale della distanza dell'ultimo passo eseguito
     */
	Real getFinalStepDistance();
}
