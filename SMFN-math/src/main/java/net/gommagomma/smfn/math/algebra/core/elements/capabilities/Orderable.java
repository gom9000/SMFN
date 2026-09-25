package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta la capacità di un elemento di essere ordinato.
 *
 * @param <E> il tipo concreto dell'elemento
 */
public interface Orderable<E extends AlgebraicElement<E>>
extends Comparable<E>
{
	/**
     * Verifica se questo elemento � strettamente minore di un altro.
     * 
     * @param other l'elemento con cui effettuare il confronto
     * @return true se questo elemento precede l'altro, false altrimenti
     */
    default boolean isLessThan(E other) {
        return compareTo(other) < 0;
    }

    /**
     * Verifica se questo elemento � strettamente maggiore di un altro.
     * 
     * @param other l'elemento con cui effettuare il confronto
     * @return true se questo elemento segue l'altro, false altrimenti
     */
    default boolean isGreaterThan(E other) {
        return compareTo(other) > 0;
    }

    /**
     * Verifica se questo elemento � minore o uguale a un altro.
     * 
     * @param other l'elemento con cui effettuare il confronto
     * @return true se minore o uguale, false altrimenti
     */
    default boolean isLessThanOrEqual(E other) {
        return !isGreaterThan(other);
    }

    /**
     * Verifica se questo elemento � maggiore o uguale a un altro.
     * 
     * @param other l'elemento con cui effettuare il confronto
     * @return true se maggiore o uguale, false altrimenti
     */
    default boolean isGreaterThanOrEqual(E other) {
        return !isLessThan(other);
    }
}
