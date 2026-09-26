package net.gommagomma.smfn.math.analysis.core.solvers;

import java.util.Objects;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Risultato di un solutore applicato a un problema di ricerca degli zeri: rispetto a {@link BasicSolverResult},
 * espone in aggiunta il residuo finale dell'equazione tramite {@link ResidualAware}.
 *
 * @param <R> Il tipo del valore calcolato dal solutore (es. lo scalare radice, o il vettore soluzione)
 */
public final class RootFindingSolverResult<R> extends BasicSolverResult<R> implements ResidualAware
{
	private final Real finalResidual;

	/**
     * Costruisce un risultato immutabile per un solutore di ricerca degli zeri.
     *
     * @param value Il valore calcolato dal solutore
     * @param status L'esito di terminazione del processo iterativo
     * @param iterationsExecuted Il numero di iterazioni effettivamente eseguite (k >= 0)
     * @param finalStepDistance La distanza tra gli ultimi due iterati eseguiti
     * @param finalResidual La norma del residuo finale ||F(x_k)|| dell'equazione al valore restituito
     * @throws NullPointerException Se uno qualsiasi degli argomenti obbligatori e' nullo
     * @throws IllegalArgumentException Se iterationsExecuted e' negativo
     */
	public RootFindingSolverResult(R value, ConvergenceStatus status, int iterationsExecuted, Real finalStepDistance, Real finalResidual) {
		super(value, status, iterationsExecuted, finalStepDistance);
		this.finalResidual = Objects.requireNonNull(finalResidual, "finalResidual must not be null.");
	}

	@Override
	public Real getFinalResidual() {
		return finalResidual;
	}
}
