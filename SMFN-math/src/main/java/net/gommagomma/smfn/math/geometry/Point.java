package net.gommagomma.smfn.math.geometry;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.vectors.InnerProductVectorSpace;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Un punto nello spazio affine N-dimensionale (tipicamente 2D o 3D).
 */
public final class Point
implements AlgebraicElement<Point>
{
	private final Vector<Real> position;

	public Point(Vector<Real> position) {
		if (position == null || position.size() == 0) {
			throw new IllegalArgumentException("Un punto richiede almeno una coordinata.");
		}
		this.position = position;
	}

	public Point(Real... coordinates) {
		this(spaceFor(coordinates.length).of(coordinates));
	}

	public Point(double... coordinates) {
		this(toReals(coordinates));
	}

	private static Real[] toReals(double[] values) {
		Real[] result = new Real[values.length];
		for (int i = 0; i < values.length; i++) result[i] = new Real(values[i]);
		return result;
	}

	private static InnerProductVectorSpace<Real, RealField> spaceFor(int dimension) {
		return new InnerProductVectorSpace<>(RealField.INSTANCE, dimension);
	}

	public int dimension() { return (int) position.size(); }

	public Real get(int index) { return position.get(index); }

	public Real getX() { return get(0); }

	public Real getY() {
		if (dimension() < 2) throw new IndexOutOfBoundsException("Il punto e' 1D, nessuna coordinata Y.");
		return get(1);
	}

	public Real getZ() {
		if (dimension() < 3) throw new IndexOutOfBoundsException("Il punto ha meno di 3 dimensioni, nessuna coordinata Z.");
		return get(2);
	}

	/** Il punto come Vector<Real> -- per chi deve collegarsi a linearalgebra/analysis. */
	public Vector<Real> asVector() { return position; }

	/** Sottrazione di due punti: P1 - P2 = spostamento. */
	public Vector<Real> displacementTo(Point other) {
		requireSameDimension(other.dimension());
		return spaceFor(dimension()).subtract(this.position, other.position);
	}

	/** Distanza euclidea tra due punti: ||P1 - P2||. */
	public Real distanceTo(Point other) {
		return spaceFor(dimension()).norm(displacementTo(other));
	}

	/** Traslazione di un punto: P + spostamento = P'. */
	public Point translate(Real... displacement) {
		requireSameDimension(displacement.length);
		InnerProductVectorSpace<Real, RealField> space = spaceFor(dimension());
		Vector<Real> disp = space.of(displacement);
		return new Point(space.add(this.position, disp));
	}

	private void requireSameDimension(int otherDimension) {
		if (dimension() != otherDimension) {
			throw new IllegalArgumentException("Dimensioni incompatibili: " + dimension() + " contro " + otherDimension + ".");
		}
	}

	@Override
	public Point copy() {
		return new Point(position.copy());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Point)) return false;
		return this.position.equals(((Point) o).position);
	}

	@Override
	public int hashCode() {
		return position.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("P(");
		for (int i = 0; i < dimension(); i++) {
			sb.append(get(i));
			if (i < dimension() - 1) sb.append(", ");
		}
		return sb.append(")").toString();
	}
}
