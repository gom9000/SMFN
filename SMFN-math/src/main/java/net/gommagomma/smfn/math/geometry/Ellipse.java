package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Un'ellisse nel piano 2D: f(P) = (x-cx)^2/a^2 + (y-cy)^2/b^2 - 1.
 */
public final class Ellipse
implements GeometryEntity<Real>
{
	private static final RealField R = RealField.INSTANCE;
	private static final VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(R, 2);

	private final Point center;
	private final Real semiAxisA;
	private final Real semiAxisB;
	private final Real aSquared;
	private final Real bSquared;

	public Ellipse(Point center, Real semiAxisA, Real semiAxisB) {
		if (center.dimension() != 2) {
			throw new IllegalArgumentException("Il centro di un'ellisse deve essere un punto 2D.");
		}
		if (semiAxisA.getValue() <= 0 || semiAxisB.getValue() <= 0) {
			throw new IllegalArgumentException("I semiassi devono essere positivi.");
		}
		this.center = center;
		this.semiAxisA = semiAxisA;
		this.semiAxisB = semiAxisB;
		this.aSquared = R.multiply(semiAxisA, semiAxisA);
		this.bSquared = R.multiply(semiAxisB, semiAxisB);
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
		Vector<Real> displacement = point.displacementTo(center);
		Real x = displacement.get(0);
		Real y = displacement.get(1);

		Real termX = R.divide(R.multiply(x, x), aSquared);
		Real termY = R.divide(R.multiply(y, y), bSquared);

		return R.subtract(R.add(termX, termY), R.one());
	}

	@Override
	public Mapping<Vector<Real>, Vector<Real>> getGradient() {
		return v -> {
			Point p = new Point(v);
			Vector<Real> displacement = p.displacementTo(center);
			Real x = displacement.get(0);
			Real y = displacement.get(1);
			Real gx = R.multiply(new Real(2.0), R.divide(x, aSquared));
			Real gy = R.multiply(new Real(2.0), R.divide(y, bSquared));
			return V2.of(new Real[] { gx, gy });
		};
	}

	public Point getCenter() { return center; }
	public Real getSemiAxisA() { return semiAxisA; }
	public Real getSemiAxisB() { return semiAxisB; }

	@Override
	public String toString() {
		return String.format("Ellipse[center=%s, a=%s, b=%s]", center, semiAxisA, semiAxisB);
	}
}
