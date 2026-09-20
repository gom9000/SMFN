package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Un cerchio nel piano 2D: f(P) = dist(P, C) - r.
 */
public final class Circle
implements GeometryEntity<Real>
{
	private static final RealField R = RealField.INSTANCE;
	private static final VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(R, 2);

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

	@Override public int getEntityDimension() { return 1; }

	@Override
	public Real apply(Vector<Real> point) {
		return implicitFunctionAt(new Point(point));
	}

	@Override
	public boolean isOnEntity(Vector<Real> point) {
		return isOnEntity(new Point(point));
	}

	public boolean isOnEntity(Point point) {
		return Math.abs(implicitFunctionAt(point).getValue()) < MathConstants.EPSILON;
	}

	public Real implicitFunctionAt(Point point) {
		if (point.dimension() != 2) {
			throw new IllegalArgumentException("Il punto deve essere 2D.");
		}
		return R.subtract(point.distanceTo(center), radius);
	}

	@Override
	public Mapping<Vector<Real>, Vector<Real>> getGradient() {
		return v -> {
			Point p = new Point(v);
			Real dist = p.distanceTo(center);
			if (R.isZero(dist)) {
				throw new ArithmeticException("Il gradiente del cerchio non e' definito esattamente nel centro "
					+ "(la distanza dal centro, e quindi la direzione del gradiente, non ha un valore univoco in quel punto).");
			}
			Real gx = R.divide(R.subtract(p.getX(), center.getX()), dist);
			Real gy = R.divide(R.subtract(p.getY(), center.getY()), dist);
			return V2.of(new Real[] { gx, gy });
		};
	}

	public Point getCenter() { return center; }
	public Real getRadius() { return radius; }

	@Override
	public String toString() {
		return String.format("Circle(center=%s, radius=%s)", center, radius);
	}
}
