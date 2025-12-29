package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.MetricSpace;

/**
 * Interfaccia per un risolutore numerico che gestisce sistemi iterativi.
 * @param <P> Il tipo di problema da risolvere.
 * @param <S> Il tipo di stato del sistema durante l'iterazione (deve essere misurabile).
 * @param <R> Il tipo di risultato finale restituito dal solutore.
 */
public interface IterativeSolver<P, S extends AlgebraicElement<S>, R extends AlgebraicElement<R>>
extends Solver<P, R>
{
    /**
     * Risolve il problema P in modo iterativo.
     * @param problem La definizione del problema
     * @param initialState Il valore iniziale per l'iterazione (di tipo S).
     * @param criteria La regola per testare la convergenza.
     * @param params I parametri (tolleranza, max iterazioni).
     * @param space L'oggetto MetricSpace che definisce come misurare la distanza tra le iterazioni S.
     * @return Il risultato convergente R.
     */
    R solve(P problem, S initialState, ConvergenceCriteria criteria, ConvergenceParameters params, MetricSpace<?, S> space);
}
 