package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;

/**
 * Contratto generale per solutori numerici basati su schemi iterativi.
 * 
 * Definisce l'infrastruttura per gli algoritmi che generano
 * una successione di approssimazioni successive S_0, S_1, ..., S_k a partire da uno stato
 * iniziale S_0, fino al soddisfacimento di specifici criteri di arresto o al raggiungimento
 * del numero massimo di iterazioni consentito.
 * 
 * Il processo di convergenza viene guidato da tre componenti fondamentali:
 * - StoppingCriteria: definisce la politica e le condizioni logiche di arresto
 *       (es. controllo sulla norma del residuo, sulla distanza tra iterati consecutivi o combinazioni).
 * - StoppingParameters: contiene i parametri quantitativi di controllo
 *       (es. tolleranza target epsilon e limite massimo di iterazioni N_max).
 * - MetricSpace: fornisce la struttura metrica necessaria per calcolare le distanze
 *       d(S_{k+1}, S_k) o le norme dei residui nello spazio degli stati.
 *
 * @param <P> Il tipo del problema di analisi numerica da risolvere
 * @param <S> Il tipo degli elementi dello spazio di iterazione o di stato
 * @param <R> Il tipo del risultato finale prodotto dal solutore
 */
public interface IterativeSolver<P, S extends AlgebraicElement<S>, R extends AlgebraicElement<R>>
extends Solver<P, R>
{
	/**
     * Esegue il processo iterativo di risoluzione del problema a partire dallo stato iniziale specificato.
     *
     * @param problem Il problema di analisi numerica da risolvere (es. problema di punto fisso o di ricerca degli zeri)
     * @param initialState Lo stato o punto di partenza iniziale S_0 per l'iterazione
     * @param criteria Il criterio di convergenza che determina quando l'algoritmo deve arrestarsi con successo
     * @param params I parametri numerici di controllo (tolleranza ed iterazioni massime)
     * @param space Lo spazio metrico associato agli elementi di stato, utilizzato per la misurazione delle distanze e delle norme
     * @return Il {@link SolverResult} contenente il valore finale R e i metadati del processo iterativo (esito, iterazioni eseguite, distanza dell'ultimo passo)
     */
    SolverResult<R> solve(P problem, S initialState, StoppingCriteria criteria, StoppingParameters params, MetricSpace<S> space);
}
 