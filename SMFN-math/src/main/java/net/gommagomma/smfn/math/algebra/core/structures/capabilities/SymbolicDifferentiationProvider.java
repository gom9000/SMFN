package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.CompositeElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Rappresenta le capacità di calcolo della derivata simbolica per elementi composti 
 * (come espressioni algebriche o polinomi).
 *
 * @param <K> il tipo degli scalari di supporto
 * @param <E> il tipo dell'elemento composto su cui operare
 */
public interface SymbolicDifferentiationProvider<K extends ScalarElement<K>, E extends CompositeElement<K, E>>
{
	/**
     * Calcola la derivata simbolica dell'elemento composto specificato.
     * 
     * @param element l'elemento da derivare
     * @return un nuovo elemento composto che rappresenta la derivata
     */
	E derivative(E element);
}
