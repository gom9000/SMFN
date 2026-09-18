package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Una retta nel piano 2D, definita da un punto di riferimento e una
 * direzione: f(P) = distanza con segno di P dalla retta.
 */
public final class Line
implements GeometryEntity<Real>
{
	private static final RealField R = RealField.INSTANCE;
	private static final VectorSemimodule<Real, RealField> V2 = new VectorSemimodule<>(R, 2);

	private final Point origin;
	private final Real dx;
	private final Real dy;
	private final Real directionLength;

	public Line(Point origin, Real dx, Real dy) {
		if (origin.dimension() != 2) {
			throw new IllegalArgumentException("Il punto di riferimento di una retta deve essere 2D.");
		}
		double len = Math.sqrt(dx.getValue() * dx.getValue() + dy.getValue() * dy.getValue());
		if (len < MathConstants.EPSILON) {
			throw new IllegalArgumentException("Il vettore direzione non puo' essere nullo.");
		}
		this.origin = origin;
		this.dx = dx;
		this.dy = dy;
		this.directionLength = new Real(len);
	}

	/** Retta passante per due punti distinti. */
	public static Line through(Point a, Point b) {
		Vector<Real> direction = b.displacementTo(a); // b - a
		return new Line(a, direction.get(0), direction.get(1));
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

	/** f(P) = ((dx,dy) x (P-origine)) / |(dx,dy)| -- distanza con segno. */
	public Real implicitFunctionAt(Point point) {
		if (point.dimension() != 2) {
			throw new IllegalArgumentException("Il punto deve essere 2D.");
		}
		Vector<Real> displacement = point.displacementTo(origin); // P - origine
		Real px = displacement.get(0);
		Real py = displacement.get(1);

		Real cross = R.subtract(R.multiply(dx, py), R.multiply(dy, px));
		return R.divide(cross, directionLength);
	}

	@Override
	public Mapping<Vector<Real>, Vector<Real>> getGradient() {
		// f(P) = (dx*(y-oy) - dy*(x-ox)) / L -- gradiente costante, la retta e' affine
		Real gx = R.divide(R.negate(dy), directionLength);
		Real gy = R.divide(dx, directionLength);
		Vector<Real> gradient = V2.of(new Real[] { gx, gy });
		return v -> gradient;
	}

	/** Distanza (sempre non negativa) di un punto dalla retta. */
	public Real distanceTo(Point point) {
		return implicitFunctionAt(point).abs();
	}

	public Point getOrigin() { return origin; }
	public Real getDirectionX() { return dx; }
	public Real getDirectionY() { return dy; }

	@Override
	public String toString() {
		return String.format("Line(origin=%s, direction=(%s, %s))", origin, dx, dy);
	}
}
