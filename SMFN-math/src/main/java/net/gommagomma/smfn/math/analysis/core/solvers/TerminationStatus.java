package net.gommagomma.smfn.math.analysis.core.solvers;

/**
 * Esito di terminazione di un processo iterativo.
 */
public enum TerminationStatus
{
	/** Il processo ha soddisfatto il criterio di convergenza stabilito prima del limite di iterazioni. */
	CONVERGED,

	/** Il limite massimo di iterazioni e' stato raggiunto senza che il criterio di convergenza fosse soddisfatto. */
	MAX_ITERATIONS_REACHED,

	/** Il processo ha rilevato la divergenza della sequenza rispetto a una soglia stabilita (es. tempo di fuga). */
	DIVERGED,

	/** Il processo si e' arrestato per una condizione numerica non gestibile (es. derivata nulla, matrice singolare). */
	NUMERICAL_ERROR
}
