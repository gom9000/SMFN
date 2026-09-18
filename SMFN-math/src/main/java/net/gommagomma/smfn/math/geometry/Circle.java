package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Un cerchio nel piano 2D: f(P) = dist(P, C) - r.
 */
public final class Circle
implements GeometryEntity<Point, Real>
{
	private static final RealField R = RealField.INSTANCE;

	private final Point center;
	private final Real radius;

	public Circle(Point center, Real radius) {
		if (center.dimension() != 2) {
			throw new IllegalArgumentException("Il centro di un cerchio deve essere un punto 2D.");
		}
		if (radius.getValue() < 0) {
			throw new IllegalArgumentException("Il raggio non puo' essere negativo.");
		}
		this.center = center;
		this.radius = radius;
	}

	@Override public int getAmbientDimension() { return 2; }
	@Override public int getEntityDimension() { return 1; }

	@Override
	public boolean isOnEntity(Point point) {
		return Math.abs(implicitFunctionAt(point).getValue()) < MathConstants.EPSILON;
	}

	@Override
	public Real implicitFunctionAt(Point point) {
		if (point.dimension() != 2) {
			throw new IllegalArgumentException("Il punto deve essere 2D.");
		}
		return R.subtract(point.distanceTo(center), radius);
	}

	public Point getCenter() { return center; }
	public Real getRadius() { return radius; }

	@Override
	public String toString() {
		return String.format("Circle(center=%s, radius=%s)", center, radius);
	}
}
