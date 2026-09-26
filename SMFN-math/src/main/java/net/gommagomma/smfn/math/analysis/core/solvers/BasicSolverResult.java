package net.gommagomma.smfn.math.analysis.core.solvers;

import java.util.Objects;

/**
 * Implementazione immutabile minima di {@link SolverResult}: solo i tre campi universali
 * a qualunque solutore della libreria (valore, esito, iterazioni eseguite), senza alcuna
 * capacita' opzionale aggiuntiva.
 * <p>
 * E' la classe giusta per un solutore che non riceve un {@code MetricSpace} esterno e non ha
 * un'equazione da annullare (es. un solutore agli autovalori, che si ferma su un proprio criterio
 * interno). Un solutore che riceve anche un {@code MetricSpace} esterno usa invece
 * {@link IterativeSolverResult}, che aggiunge {@link StepDistanceAware}.
 *
 * @param <R> Il tipo del valore calcolato dal solutore
 */
public class BasicSolverResult<R> implements SolverResult<R>
{
	private final R value;
	private final ConvergenceStatus status;
	private final int iterationsExecuted;

	/**
     * Costruisce un risultato immutabile.
     *
     * @param value Il valore calcolato dal solutore
     * @param status L'esito di terminazione del processo del solutore
     * @param iterationsExecuted Il numero di iterazioni effettivamente eseguite (k >= 0)
     * @throws NullPointerException Se value o status sono nulli
     * @throws IllegalArgumentException Se iterationsExecuted e' negativo
     */
	public BasicSolverResult(R value, ConvergenceStatus status, int iterationsExecuted) {
		if (iterationsExecuted < 0) {
			throw new IllegalArgumentException("Iterations executed must not be negative.");
		}
		this.value = Objects.requireNonNull(value, "value must not be null.");
		this.status = Objects.requireNonNull(status, "status must not be null.");
		this.iterationsExecuted = iterationsExecuted;
	}

	@Override
	public R getValue() {
		return value;
	}

	@Override
	public ConvergenceStatus getStatus() {
		return status;
	}

	@Override
	public int getIterationsExecuted() {
		return iterationsExecuted;
	}
}
