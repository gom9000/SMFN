package net.gommagomma.smfn.math.analysis.core.solvers;

/**
 * Esito di terminazione di un processo iterativo, cosi' come lo riporta il solutore che lo ha eseguito.
 * <p>
 * Questo valore e' un dato descrittivo neutro: non esprime un giudizio di successo o fallimento,
 * perche' quel giudizio dipende dal problema che il solutore sta servendo. Ad esempio,
 * {@link #MAX_ITERATIONS_REACHED} e' un fallimento per un solutore di ricerca degli zeri
 * (la radice non e' stata trovata entro il budget concesso), ma e' l'esito normale e atteso
 * per un solutore a tempo di fuga applicato a un punto interno a un insieme frattale
 * (il punto non e' mai divergente entro il budget, il che e' proprio l'informazione cercata).
 * E' compito di chi consuma il {@link SolverResult} interpretare lo stato nel proprio dominio.
 */
public enum ConvergenceStatus
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
