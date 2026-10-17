package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

/**
 * Un piano nello spazio 3D, definito da un punto di riferimento e una
 * normale: f(P) = distanza con segno di P dal piano.
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
		// Passa da RealField.isZero() invece di confrontare il double grezzo contro
		// MathConstants.EPSILON, cosi' eredita la tolleranza ibrida assoluta+relativa
		// invece di un epsilon assoluto fisso.
		if (R.isZero(new Real(len))) {
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
		return R.isZero(implicitFunctionAt(point));
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
		return Mapping.constant(gradient);
	}

	public Real distanceTo(Point point) {
		return implicitFunctionAt(point).abs();
	}

	/**
	 * Forma esplicita del piano: la funzione (x, y) -> z tale che il punto (x, y, z) sta sul piano,
	 * z = oz - (nx*(x-ox) + ny*(y-oy)) / nz. E' la rappresentazione "grafico di una funzione",
	 * quella che serve a disegnare il piano come superficie; la funzione implicita ({@link #apply})
	 * serve invece a decidere se un punto 3D sta sul piano.
	 * <p>
	 * Esiste solo se il piano non e' verticale (nz != 0): un piano verticale non e' il grafico di
	 * una funzione di (x, y).
	 *
	 * @return la funzione che a un vettore 2D (x, y) associa la quota z del piano
	 * @throws ArithmeticException se il piano e' verticale
	 */
	public Mapping<Vector<Real>, Real> explicitForm() {
		if (R.isZero(nz)) {
			throw new ArithmeticException("Il piano e' verticale (nz = 0): non ha forma esplicita z = f(x, y).");
		}
		return xy -> {
			if (xy.size() != 2) {
				throw new IllegalArgumentException("Il punto deve essere 2D (x, y).");
			}
			Real dx = R.subtract(xy.get(0), origin.getX());
			Real dy = R.subtract(xy.get(1), origin.getY());
			Real numerator = R.add(R.multiply(nx, dx), R.multiply(ny, dy));
			return R.subtract(origin.getZ(), R.divide(numerator, nz));
		};
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
