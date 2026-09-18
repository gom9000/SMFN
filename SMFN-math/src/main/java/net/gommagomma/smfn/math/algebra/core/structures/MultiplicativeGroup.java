package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta un gruppo moltiplicativo, estendendo il monoide moltiplicativo 
 * con la richiesta che ogni elemento ammetta un inverso rispetto alla moltiplicazione.
 *
 * @param <E> il tipo degli elementi appartenenti alla struttura
 */
public interface MultiplicativeGroup<E extends AlgebraicElement<E>>
extends CommutativeMultiplicativeMonoid<E>
{
	/**
     * Calcola l'inverso moltiplicativo di un elemento.
     * 
     * @param e l'elemento di cui calcolare l'inverso
     * @return l'elemento inverso
     */
	E inverse(E e);

	/**
     * Esegue la divisione tra due elementi sfruttando l'inverso moltiplicativo.
     * 
     * @param a il numeratore (dividendo)
     * @param b il denominatore (divisore)
     * @return il risultato della divisione
     */
	default E divide(E a, E b) {
        return multiply(a, inverse(b));
    }
}
