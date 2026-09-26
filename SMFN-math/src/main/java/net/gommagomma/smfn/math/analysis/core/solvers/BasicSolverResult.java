package net.gommagomma.smfn.math.analysis.core.solvers;

import java.util.Objects;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Implementazione immutabile standard di {@link SolverResult}, condivisa da qualunque {@link IterativeSolver}
 * che non abbia diagnostiche aggiuntive da esporre oltre a quelle universali.
 * <p>
 * I solutori applicati a problemi di ricerca degli zeri, che possiedono anche il residuo finale
 * dell'equazione, usano invece {@link RootFindingSolverResult}.
 *
 * @param <R> Il tipo del valore calcolato dal solutore
 */
public class BasicSolverResult<R> implements SolverResult<R>
{
	private final R value;
	private final ConvergenceStatus status;
	private final int iterationsExecuted;
	private final Real finalStepDistance;

	/**
     * Costruisce un risultato immutabile.
     *
     * @param value Il valore calcolato dal solutore
     * @param status L'esito di terminazione del processo iterativo
     * @param iterationsExecuted Il numero di iterazioni effettivamente eseguite (k >= 0)
     * @param finalStepDistance La distanza tra gli ultimi due iterati eseguiti
     * @throws NullPointerException Se value, status o finalStepDistance sono nulli
     * @throws IllegalArgumentException Se iterationsExecuted e' negativo
     */
	public BasicSolverResult(R value, ConvergenceStatus status, int iterationsExecuted, Real finalStepDistance) {
		if (iterationsExecuted < 0) {
			throw new IllegalArgumentException("Iterations executed must not be negative.");
		}
		this.value = Objects.requireNonNull(value, "value must not be null.");
		this.status = Objects.requireNonNull(status, "status must not be null.");
		this.iterationsExecuted = iterationsExecuted;
		this.finalStepDistance = Objects.requireNonNull(finalStepDistance, "finalStepDistance must not be null.");
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

	@Override
	public Real getFinalStepDistance() {
		return finalStepDistance;
	}
}
