package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

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
		double len = Math.hypot(dx.getValue(), dy.getValue());
		if (R.isZero(new Real(len))) {
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

	/** Vero se la distanza dalla retta e' trascurabile rispetto alla distanza del punto dal punto di riferimento. */
	public boolean isOnEntity(Point point) {
		Vector<Real> displacement = point.displacementTo(origin);
		Real scale = new Real(Math.hypot(displacement.get(0).getValue(), displacement.get(1).getValue()));
		return R.isNegligible(implicitFunctionAt(point), scale);
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
		return Mapping.constant(gradient);
	}

	/** Distanza (sempre non negativa) di un punto dalla retta. */
	public Real distanceTo(Point point) {
		return implicitFunctionAt(point).abs();
	}

	/**
	 * Forma esplicita della retta: la funzione x -> y tale che il punto (x, y) sta sulla retta,
	 * y = oy + (dy/dx) * (x - ox). E' la rappresentazione "grafico di una funzione di una
	 * variabile"; la funzione implicita ({@link #apply}) serve invece a decidere se un punto 2D
	 * sta sulla retta.
	 * <p>
	 * Esiste solo se la retta non e' verticale (dx != 0): una retta verticale non e' il grafico di
	 * una funzione di x.
	 *
	 * @return la funzione che a x associa la y della retta
	 * @throws ArithmeticException se la retta e' verticale
	 */
	public Mapping<Real, Real> explicitForm() {
		if (R.isZero(dx)) {
			throw new ArithmeticException("La retta e' verticale (dx = 0): non ha forma esplicita y = f(x).");
		}
		Real slope = R.divide(dy, dx);
		return x -> R.add(origin.getY(), R.multiply(slope, R.subtract(x, origin.getX())));
	}

	public Point getOrigin() { return origin; }
	public Real getDirectionX() { return dx; }
	public Real getDirectionY() { return dy; }

	@Override
	public String toString() {
		return String.format("Line(origin=%s, direction=(%s, %s))", origin, dx, dy);
	}
}
