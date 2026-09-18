package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.InvertibleElements;

/**

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
