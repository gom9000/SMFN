package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Rappresenta la capacità di un elemento di restituire la propria norma
 * sotto forma di valore scalare.
 *
 * @param <K> il tipo scalare della norma risultante
 */
public interface Normable<K extends ScalarElement<K>>
{
	/**
     * Calcola la norma dell'elemento.
     * 
     * @return lo scalare che rappresenta la norma
     */
    K norm();
}