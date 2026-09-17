package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.CompositeElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Capacit di calcolare l'integrale simbolico (formale) indefinito di un elemento
 * composto E, costruito su uno scalare K (es. i coefficienti di un polinomio),
 * data una costante di integrazione.
 */
public interface SymbolicIntegrationProvider<K extends ScalarElement<K>, E extends CompositeElement<K, E>>
{
	E integrate(E element, K constant);
}
