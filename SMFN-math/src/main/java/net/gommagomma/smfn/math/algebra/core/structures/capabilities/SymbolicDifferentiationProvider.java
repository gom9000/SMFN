package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.CompositeElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;

/**
 * Capacit di calcolare la derivata simbolica (formale) di un elemento composto E,
 * costruito su uno scalare K (es. i coefficienti di un polinomio).
 *
 * Non ogni AlgebraicElement ha una nozione di derivata: solo le strutture composte
 * che si appoggiano a uno scalare K la possono definire algebricamente.
 */
public interface SymbolicDifferentiationProvider<K extends ScalarElement<K>, E extends CompositeElement<K, E>>
{
	E derivative(E element);
}
