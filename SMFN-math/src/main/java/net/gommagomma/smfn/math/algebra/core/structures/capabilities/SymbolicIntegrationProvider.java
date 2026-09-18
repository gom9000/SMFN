package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.CompositeElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Fornisce le capacità di calcolo dell'integrale simbolico per elementi composti 
 * (come espressioni algebriche o polinomi).
 *
 * @param <K> il tipo degli scalari di supporto
 * @param <E> il tipo dell'elemento composto su cui operare
 */
public interface SymbolicIntegrationProvider<K extends ScalarElement<K>, E extends CompositeElement<K, E>>
{
	/**
     * Calcola l'integrale simbolico dell'elemento composto specificato, 
     * includendo la costante di integrazione fornita.
     * 
     * @param element l'elemento da integrare
     * @param constant la costante di integrazione scalare
     * @return un nuovo elemento composto che rappresenta l'integrale
     */
	E integrate(E element, K constant);
}
