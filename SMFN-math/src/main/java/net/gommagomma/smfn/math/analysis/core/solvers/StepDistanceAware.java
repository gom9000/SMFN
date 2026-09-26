package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Capacita' opzionale di un {@link SolverResult}: espone la distanza tra gli ultimi due iterati
 * d(x_k, x_{k-1}) prodotti dal processo del solutore.
 * <p>
 * Questa distanza e' definita solo quando il solutore riceve un
 * {@link net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace} esterno sullo spazio
 * di iterazione, come fa {@link IterativeSolver#solve}. Un solutore con un proprio criterio di
 * arresto interno che non si basa su una metrica esterna (es. un solutore agli autovalori di Jacobi,
 * che si ferma sulla norma della parte fuori diagonale della matrice) non ha modo di calcolarla
 * senza inventare una metrica non richiesta dal proprio algoritmo, quindi il suo {@link SolverResult}
 * non implementa questa interfaccia.
 */
public interface StepDistanceAware
{
	/**
     * Restituisce la distanza tra gli ultimi due iterati d(x_k, x_{k-1}), o zero se il processo
     * si e' arrestato prima di eseguire un solo passo.
     *
     * @return Il valore reale della distanza dell'ultimo passo eseguito
     */
	Real getFinalStepDistance();
}
