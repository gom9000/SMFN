package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Un'ellisse nel piano 2D: f(P) = (x-cx)^2/a^2 + (y-cy)^2/b^2 - 1.
 */
public final class Ellipse
implements GeometryEntity<Point, Real>
{
	private static final RealField R = RealField.INSTANCE;

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
		Real[] displacement = point.displacementTo(center);
		Real x = displacement[0];
		Real y = displacement[1];

		Real termX = R.divide(R.multiply(x, x), aSquared);
		Real termY = R.divide(R.multiply(y, y), bSquared); // corretto: bSquared, non aSquared come nell'originale

		return R.subtract(R.add(termX, termY), R.one());
	}

	public Point getCenter() { return center; }
	public Real getSemiAxisA() { return semiAxisA; }
	public Real getSemiAxisB() { return semiAxisB; }

	@Override
	public String toString() {
		return String.format("Ellipse[center=%s, a=%s, b=%s]", center, semiAxisA, semiAxisB);
	}
}
