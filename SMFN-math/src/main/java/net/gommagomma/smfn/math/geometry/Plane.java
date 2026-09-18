package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Un piano nello spazio 3D, definito da un punto di riferimento e una
 * normale: f(P) = distanza con segno di P dal piano.
 *
 * Stesso stampo di Line, una dimensione piu' su -- Point e GeometryEntity
 * non hanno mai avuto la dimensione cablata dentro, quindi non serve
 * toccare nulla di esistente per arrivare a 3D.
 */
public final class Plane
implements GeometryEntity<Real>
{
	private static final RealField R = RealField.INSTANCE;
	private static final VectorSemimodule<Real, RealField> V3 = new VectorSemimodule<>(R, 3);

	private final Point origin;
	private final Real nx;
	private final Real ny;
	private final Real nz;
	private final Real normalLength;

	public Plane(Point origin, Real nx, Real ny, Real nz) {
		if (origin.dimension() != 3) {
			throw new IllegalArgumentException("Il punto di riferimento di un piano deve essere 3D.");
		}
		double len = Math.sqrt(nx.getValue() * nx.getValue() + ny.getValue() * ny.getValue() + nz.getValue() * nz.getValue());
		if (len < MathConstants.EPSILON) {
			throw new IllegalArgumentException("Il vettore normale non puo' essere nullo.");
		}
		this.origin = origin;
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
		this.normalLength = new Real(len);
	}

	/** Piano passante per tre punti non collineari: normale = (b-a) x (c-a). */
	public static Plane through(Point a, Point b, Point c) {
		Vector<Real> ab = b.displacementTo(a); // b - a
		Vector<Real> ac = c.displacementTo(a); // c - a

		Real nx = R.subtract(R.multiply(ab.get(1), ac.get(2)), R.multiply(ab.get(2), ac.get(1)));
		Real ny = R.subtract(R.multiply(ab.get(2), ac.get(0)), R.multiply(ab.get(0), ac.get(2)));
		Real nz = R.subtract(R.multiply(ab.get(0), ac.get(1)), R.multiply(ab.get(1), ac.get(0)));

		return new Plane(a, nx, ny, nz);
	}

	@Override public int getEntityDimension() { return 2; }

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

	/** f(P) = (normale . (P-origine)) / |normale| -- distanza con segno. */
	public Real implicitFunctionAt(Point point) {
		if (point.dimension() != 3) {
			throw new IllegalArgumentException("Il punto deve essere 3D.");
		}
		Vector<Real> displacement = point.displacementTo(origin);
		Real dot = R.add(
			R.add(R.multiply(nx, displacement.get(0)), R.multiply(ny, displacement.get(1))),
			R.multiply(nz, displacement.get(2)));
		return R.divide(dot, normalLength);
	}

	@Override
	public Mapping<Vector<Real>, Vector<Real>> getGradient() {
		// f e' affine: gradiente costante = normale normalizzata
		Real gx = R.divide(nx, normalLength);
		Real gy = R.divide(ny, normalLength);
		Real gz = R.divide(nz, normalLength);
		Vector<Real> gradient = V3.of(new Real[] { gx, gy, gz });
		return v -> gradient;
	}

	public Real distanceTo(Point point) {
		return implicitFunctionAt(point).abs();
	}

	public Point getOrigin() { return origin; }
	public Real getNormalX() { return nx; }
	public Real getNormalY() { return ny; }
	public Real getNormalZ() { return nz; }

	@Override
	public String toString() {
		return String.format("Plane(origin=%s, normal=(%s, %s, %s))", origin, nx, ny, nz);
	}
}
