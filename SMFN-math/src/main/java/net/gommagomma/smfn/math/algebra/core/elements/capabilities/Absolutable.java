package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Rappresenta la capacità di un elemento algebrico di calcolare il proprio valore assoluto 
 * (o modulo) e di determinare il proprio segno.
 *
 * @param <E> il tipo concreto dell'elemento
 */
public interface Absolutable<E extends AlgebraicElement<E>> 
{
	/**
     * Calcola il valore assoluto (o magnitudine) dell'elemento.
     * 
     * @return un nuovo elemento che rappresenta il valore assoluto
     */
	E abs();

	/**
     * Determina il segno dell'elemento.
     * 
     * @return un valore intero che indica il segno (-1, 0, o 1)
     */
	int signum();
}