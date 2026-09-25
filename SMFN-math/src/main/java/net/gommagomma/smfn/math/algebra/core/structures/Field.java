package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.InvertibleElements;

/**
 * Rappresenta la struttura algebrica di Campo (Field), ossia
 * Un campo è un anello commutativo unitario in cui ogni elemento non nullo 
 * ammette un inverso rispetto alla moltiplicazione.
 *
 * @param <E> il tipo degli elementi appartenenti al campo
 */
public interface Field<E extends AlgebraicElement<E>>
extends CommutativeRing<E>, MultiplicativeGroup<E>, InvertibleElements<E>
{
	@Override
	default boolean isInvertible(E e) {
		return !isZero(e);
	}
}
