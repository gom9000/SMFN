package net.gommagomma.smfn.math.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Test puntuali per {@link Point}: costruzione, accesso alle coordinate, spostamento,
 * distanza e traslazione. I casi numerici usano una tripla pitagorica (3,4,5) per la
 * distanza, cosi' il valore atteso e' esatto e non frutto di un calcolo a mano fallibile.
 */
@DisplayName("Point: costruzione, coordinate, spostamento, distanza, traslazione")
class PointTest
{
	private static final double EPSILON = 1e-12;

	@Test
	@DisplayName("Costruzione da double... espone correttamente dimensione e coordinate")
	void constructionFromDoublesExposesCoordinates() {
		Point p = new Point(1.0, 2.0, 3.0);
		assertEquals(3, p.dimension());
		assertEquals(1.0, p.getX().getValue(), EPSILON);
		assertEquals(2.0, p.getY().getValue(), EPSILON);
		assertEquals(3.0, p.getZ().getValue(), EPSILON);
		assertEquals(2.0, p.get(1).getValue(), EPSILON);
	}

	@Test
	@DisplayName("Costruzione da Real... e da Vector<Real> producono punti equivalenti")
	void alternativeConstructorsAgree() {
		Point fromDoubles = new Point(1.0, 2.0);
		Point fromReals = new Point(new Real(1.0), new Real(2.0));
		Point fromVector = new Point(fromDoubles.asVector());
		assertEquals(fromDoubles, fromReals);
		assertEquals(fromDoubles, fromVector);
	}

	@Test
	@DisplayName("getY() e getZ() lanciano se il punto non ha abbastanza dimensioni")
	void insufficientDimensionsThrowOnAccessors() {
		Point p1d = new Point(1.0);
		assertThrows(IndexOutOfBoundsException.class, () -> p1d.getY());
		assertThrows(IndexOutOfBoundsException.class, () -> p1d.getZ());

		Point p2d = new Point(1.0, 2.0);
		assertThrows(IndexOutOfBoundsException.class, () -> p2d.getZ());
	}

	@Test
	@DisplayName("Il costruttore rifiuta coordinate nulle o vuote")
	void constructorRejectsNullOrEmptyCoordinates() {
		assertThrows(IllegalArgumentException.class, () -> new Point((Vector<Real>) null));
		assertThrows(IllegalArgumentException.class, () -> new Point(new double[0]));
	}

	@Test
	@DisplayName("displacementTo: P1 - P2 restituisce lo spostamento componente per componente")
	void displacementToComputesComponentwiseDifference() {
		Point p1 = new Point(5.0, 7.0);
		Point p2 = new Point(2.0, 3.0);
		var displacement = p1.displacementTo(p2);
		assertEquals(3.0, displacement.get(0).getValue(), EPSILON);
		assertEquals(4.0, displacement.get(1).getValue(), EPSILON);
	}

	@Test
	@DisplayName("distanceTo: tripla pitagorica (3,4,5) da' distanza esattamente 5")
	void distanceToUsesPythagoreanTriple() {
		Point origin = new Point(0.0, 0.0);
		Point p = new Point(3.0, 4.0);
		assertEquals(5.0, origin.distanceTo(p).getValue(), EPSILON);
		assertEquals(5.0, p.distanceTo(origin).getValue(), EPSILON); // simmetria
	}

	@Test
	@DisplayName("distanceTo(se stesso) e' zero, per qualunque punto")
	void distanceToSelfIsZero() {
		Point p = new Point(-3.5, 12.1, 0.7);
		assertEquals(0.0, p.distanceTo(p).getValue(), EPSILON);
	}

	@Test
	@DisplayName("displacementTo/distanceTo lanciano su dimensioni incompatibili")
	void mismatchedDimensionsThrow() {
		Point p2d = new Point(1.0, 2.0);
		Point p3d = new Point(1.0, 2.0, 3.0);
		assertThrows(IllegalArgumentException.class, () -> p2d.displacementTo(p3d));
		assertThrows(IllegalArgumentException.class, () -> p2d.distanceTo(p3d));
	}

	@Test
	@DisplayName("translate: P + spostamento poi - spostamento ritorna al punto originale")
	void translateIsInvertedByOppositeDisplacement() {
		Point p = new Point(1.0, 2.0);
		Point translated = p.translate(new Real(3.0), new Real(-1.0));
		assertEquals(4.0, translated.getX().getValue(), EPSILON);
		assertEquals(1.0, translated.getY().getValue(), EPSILON);

		Point back = translated.translate(new Real(-3.0), new Real(1.0));
		assertEquals(p, back);
	}

	@Test
	@DisplayName("translate lancia su dimensioni incompatibili")
	void translateRejectsMismatchedDimension() {
		Point p2d = new Point(1.0, 2.0);
		assertThrows(IllegalArgumentException.class, () -> p2d.translate(new Real(1.0)));
	}

	@Test
	@DisplayName("copy() produce un punto uguale ma e' un'istanza distinta")
	void copyProducesEqualButDistinctInstance() {
		Point p = new Point(1.0, 2.0, 3.0);
		Point c = p.copy();
		assertEquals(p, c);
		assertTrue(p != c);
	}

	@Test
	@DisplayName("equals/hashCode: coerenti su punti con le stesse coordinate, falsi su coordinate diverse")
	void equalsAndHashCodeAreConsistent() {
		Point a = new Point(1.0, 2.0);
		Point b = new Point(1.0, 2.0);
		Point c = new Point(1.0, 2.1);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertFalse(a.equals(c));
		assertFalse(a.equals("not a point"));
		assertFalse(a.equals(null));
	}

	@Test
	@DisplayName("toString() riporta tutte le coordinate del punto")
	void toStringContainsAllCoordinates() {
		Point p = new Point(1.0, 2.0, 3.0);
		String s = p.toString();
		assertTrue(s.contains("1.0"));
		assertTrue(s.contains("2.0"));
		assertTrue(s.contains("3.0"));
	}
}
