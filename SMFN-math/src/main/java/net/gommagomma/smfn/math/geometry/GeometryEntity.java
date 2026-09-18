package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

/**
 * Un ente geometrico rappresentato tramite la sua funzione implicita f(P) = 0.
 *
 * @param <D> Il tipo di input (es. Point).
 * @param <C> Il tipo di output (es. Real).
 */
public interface GeometryEntity<D extends AlgebraicElement<D>, C extends AlgebraicElement<C>>
extends Mapping<D, C>
{
	int getAmbientDimension();
	int getEntityDimension();
	boolean isOnEntity(D point);

	@Override
	default C apply(D point) {
		return implicitFunctionAt(point);
	}

	C implicitFunctionAt(D point);
}
