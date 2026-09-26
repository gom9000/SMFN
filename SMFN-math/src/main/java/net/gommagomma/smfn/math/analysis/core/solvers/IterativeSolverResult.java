package net.gommagomma.smfn.math.analysis.core.solvers;

import java.util.Objects;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Risultato di un solutore che riceve un {@code MetricSpace} esterno sullo spazio di iterazione
 * (cioe' un vero {@link IterativeSolver}): rispetto a {@link BasicSolverResult}, espone in aggiunta
 * la distanza dell'ultimo passo tramite {@link StepDistanceAware}.
 * <p>
 * E' la classe giusta per un solutore come {@code EscapeTimeSolver}, che ha una metrica esterna
 * ma nessuna equazione da annullare (il problema di punto fisso che risolve non ha un residuo
 * associato). Un solutore di ricerca degli zeri, che ha sia la metrica sia un residuo, usa invece
 * {@link RootFindingSolverResult}.
 *
 * @param <R> Il tipo del valore calcolato dal solutore
 */
public class IterativeSolverResult<R> extends BasicSolverResult<R> implements StepDistanceAware
{
	private final Real finalStepDistance;

	/**
     * Costruisce un risultato immutabile per un solutore con metrica esterna.
     *
     * @param value Il valore calcolato dal solutore
     * @param status L'esito di terminazione del processo del solutore
     * @param iterationsExecuted Il numero di iterazioni effettivamente eseguite (k >= 0)
     * @param finalStepDistance La distanza tra gli ultimi due iterati eseguiti
     * @throws NullPointerException Se uno qualsiasi degli argomenti obbligatori e' nullo
     * @throws IllegalArgumentException Se iterationsExecuted e' negativo
     */
	public IterativeSolverResult(R value, ConvergenceStatus status, int iterationsExecuted, Real finalStepDistance) {
		super(value, status, iterationsExecuted);
		this.finalStepDistance = Objects.requireNonNull(finalStepDistance, "finalStepDistance must not be null.");
	}

	@Override
	public Real getFinalStepDistance() {
		return finalStepDistance;
	}
}
