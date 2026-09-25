package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Modellizza un problema di punto fisso della forma: x = G(x) dove si ricerca un
 * elemento x^* di T tale che x^* = G(x^*).
 * 
 * Un FixedPointProblem interpreta la mappa sottostante come la funzione di transizione
 * di stato G, che calcola l'iterato successivo T_{k+1} = G(T_k) a partire dallo stato
 * corrente T_k.
 *
 * @param <T> Il tipo dell'elemento algebrico appartenente allo spazio di iterazione
 */
public interface FixedPointProblem<T extends AlgebraicElement<T>>
extends IterationProblem<T>
{
	/**
     * Calcola lo stato o l'iterato successivo T_{k+1} = G(T_k) a partire dall'iterato corrente T_k.
     * 
     * Questo metodo costituisce un alias semantico per apply, rendendo il codice
     * dei solutori di punto fisso esplicito.
     *
     * @param current L'iterato o stato corrente T_k
     * @return L'iterato successivo T_{k+1} risultante dall'applicazione della funzione di punto fisso G
     */
	default T nextIteration(T current) { return apply(current); }
}
