package net.gommagomma.smfn.math.geometry;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Un punto nello spazio affine N-dimensionale (tipicamente 2D o 3D).
 *
 */
public final class Point implements AlgebraicElement<Point>
{
	private static final RealField R = RealField.INSTANCE;

	private final Real[] coordinates;

	public Point(Real... coordinates) {
		if (coordinates == null || coordinates.length == 0) {
			throw new IllegalArgumentException("Un punto richiede almeno una coordinata.");
		}
		this.coordinates = coordinates.clone();
	}

	public Point(double... coordinates) {
		this(toReals(coordinates));
	}

	private static Real[] toReals(double[] values) {
		Real[] result = new Real[values.length];
		for (int i = 0; i < values.length; i++) result[i] = new Real(values[i]);
		return result;
	}

	public int dimension() { return coordinates.length; }

	public Real get(int index) {
		if (index < 0 || index >= coordinates.length) {
			throw new IndexOutOfBoundsException("Indice " + index + " fuori dai limiti per un punto " + coordinates.length + "D.");
		}
		return coordinates[index];
	}

	public Real getX() { return get(0); }

	public Real getY() {
		if (dimension() < 2) throw new IndexOutOfBoundsException("Il punto e' 1D, nessuna coordinata Y.");
		return get(1);
	}

	public Real getZ() {
		if (dimension() < 3) throw new IndexOutOfBoundsException("Il punto ha meno di 3 dimensioni, nessuna coordinata Z.");
		return get(2);
	}

	/** Sottrazione di due punti: P1 - P2 = spostamento, come componenti. */
	public Real[] displacementTo(Point other) {
		requireSameDimension(other.dimension());
		Real[] result = new Real[dimension()];
		for (int i = 0; i < dimension(); i++) {
			result[i] = R.subtract(this.coordinates[i], other.coordinates[i]);
		}
		return result;
	}

	/** Distanza euclidea tra due punti: ||P1 - P2||. */
	public Real distanceTo(Point other) {
		Real[] displacement = displacementTo(other);
		Real sumOfSquares = R.zero();
		for (Real component : displacement) {
			sumOfSquares = R.add(sumOfSquares, R.multiply(component, component));
		}
		return sumOfSquares.sqrt();
	}

	/** Traslazione di un punto: P + spostamento = P'. */
	public Point translate(Real... displacement) {
		requireSameDimension(displacement.length);
		Real[] result = new Real[dimension()];
		for (int i = 0; i < dimension(); i++) {
			result[i] = R.add(this.coordinates[i], displacement[i]);
		}
		return new Point(result);
	}

	private void requireSameDimension(int otherDimension) {
		if (dimension() != otherDimension) {
			throw new IllegalArgumentException("Dimensioni incompatibili: " + dimension() + " contro " + otherDimension + ".");
		}
	}

	@Override
	public Point copy() {
		return new Point(coordinates.clone());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Point)) return false;
		return Arrays.equals(this.coordinates, ((Point) o).coordinates);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(coordinates);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("P(");
		for (int i = 0; i < coordinates.length; i++) {
			sb.append(coordinates[i]);
			if (i < coordinates.length - 1) sb.append(", ");
		}
		return sb.append(")").toString();
	}
}
