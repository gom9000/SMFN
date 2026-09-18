package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Una retta nel piano 2D, definita da un punto di riferimento e una
 * direzione: f(P) = distanza con segno di P dalla retta.
 *
 * La normale alla direzione (dx,dy) e' (-dy,dx). f(P) e' la proiezione dello
 * spostamento (P - origine) sulla normale normalizzata: positiva da un lato
 * della retta, negativa dall'altro, zero esattamente sulla retta -- non solo
 * un segnale binario, ma una vera distanza con segno, coerente con
 * Circle/Ellipse.
 */
public final class Line implements GeometryEntity<Point, Real>
{
	private static final RealField R = RealField.INSTANCE;

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
		Real[] direction = b.displacementTo(a); // b - a
		return new Line(a, direction[0], direction[1]);
	}

	@Override public int getAmbientDimension() { return 2; }
	@Override public int getEntityDimension() { return 1; }

	@Override
	public boolean isOnEntity(Point point) {
		return Math.abs(implicitFunctionAt(point).getValue()) < MathConstants.EPSILON;
	}

	/** f(P) = ((dx,dy) x (P-origine)) / |(dx,dy)| -- distanza con segno. */
	@Override
	public Real implicitFunctionAt(Point point) {
		if (point.dimension() != 2) {
			throw new IllegalArgumentException("Il punto deve essere 2D.");
		}
		Real[] displacement = point.displacementTo(origin); // P - origine
		Real px = displacement[0];
		Real py = displacement[1];

		// Componente z del prodotto vettoriale direzione x spostamento
		Real cross = R.subtract(R.multiply(dx, py), R.multiply(dy, px));
		return R.divide(cross, directionLength);
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
