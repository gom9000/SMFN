package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Capacita' opzionale di un {@link SolverResult}: espone il residuo finale ||F(x_k)||
 * dell'equazione risolta dal solutore.
 * <p>
 * Il residuo e' definito solo per solutori applicati a un problema di ricerca degli zeri
 * (es. {@code ScalarRootFindingProblem}, {@code VectorRootFindingProblem}), dove esiste
 * una funzione F il cui annullamento e' l'obiettivo della ricerca. Un solutore applicato
 * a un problema di punto fisso puro (es. {@code FixedPointProblem}, come nei solutori a
 * tempo di fuga) non ha nessuna equazione associata, quindi il suo {@link SolverResult}
 * non implementa questa interfaccia: chi consuma il risultato verifica la capacita' con
 * {@code instanceof} invece di ricevere un valore inventato o un'eccezione da un metodo
 * che non tutti i solutori possono onorare.
 */
public interface ResidualAware
{
	/**
     * Restituisce la norma del residuo finale ||F(x_k)|| dell'equazione al punto restituito da {@link SolverResult#getValue()}.
     *
     * @return Il valore reale del residuo finale
     */
	Real getFinalResidual();
}
