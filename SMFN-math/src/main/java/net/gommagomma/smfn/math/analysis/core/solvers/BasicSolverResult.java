package net.gommagomma.smfn.math.analysis.core.solvers;

import java.util.Objects;

/**
 * Incapsula i risultati di un processo risolutivo iterativo.
 * E' adatta a solutori iterativi generici per la generazione di insiemi frattali
 * o algoritmi a punto fisso.
 *
 * @param <R> Il tipo del valore calcolato dal solutore
 */
public class BasicSolverResult<R>
implements SolverResult<R>
{
	private final R value;
	private final TerminationStatus status;
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
	public BasicSolverResult(R value, TerminationStatus status, int iterationsExecuted) {
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
	public TerminationStatus getStatus() {
		return status;
	}

	@Override
	public int getIterationsExecuted() {
		return iterationsExecuted;
	}
}
