package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.analysis.core.functions.DifferentiableMultivariateFunction;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Un ente geometrico rappresentato tramite la sua funzione implicita f(P) = 0.
 */
public interface GeometryEntity<K extends ScalarElement<K>>
extends DifferentiableMultivariateFunction<K>
{
	int getEntityDimension();
	boolean isOnEntity(Vector<K> point);
}
