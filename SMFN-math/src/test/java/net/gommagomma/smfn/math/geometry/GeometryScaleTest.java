package net.gommagomma.smfn.math.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;

@DisplayName("Geometria: l'appartenenza a un'entita' e' relativa alla grandezza dei dati")
class GeometryScaleTest
{
	private static Point p(double... c) {
		Real[] r = new Real[c.length];
		for (int i = 0; i < c.length; i++) r[i] = new Real(c[i]);
		return new Point(r);
	}

	@Test
	@DisplayName("Cerchio: lo scarto ammesso dalla circonferenza e' proporzionale al raggio")
	void circleMembershipIsRelative() {
		assertFalse(new Circle(p(0, 0), new Real(1e-2)).isOnEntity(p(1.1e-2, 0)));
		assertTrue(new Circle(p(0, 0), new Real(1e6)).isOnEntity(p(1e6 + 1e-7, 0)));
		assertFalse(new Circle(p(0, 0), new Real(1e6)).isOnEntity(p(1e6 + 1.0, 0)));
		assertTrue(new Circle(p(0, 0), new Real(1e-7)).isOnEntity(p(0, 1e-7)));
		assertFalse(new Circle(p(0, 0), new Real(1e-7)).isOnEntity(p(0, 2e-7)));
	}

	@Test
	@DisplayName("Ellisse con semiassi 1e-7: i punti vicini ma non sul bordo sono esclusi")
	void ellipseSmallSemiAxes() {
		Ellipse e = new Ellipse(p(0, 0), new Real(1e-7), new Real(2e-7));
		assertTrue(e.isOnEntity(p(1e-7, 0)));
		assertFalse(e.isOnEntity(p(1.1e-7, 0)));
		assertFalse(e.isOnEntity(p(0, 0)));
	}

	@Test
	@DisplayName("Retta per due punti distanti 1e-13 e' valida; l'appartenenza e' relativa alla distanza dal riferimento")
	void lineThroughCloseAndFarPoints() {
		Line tiny = Line.through(p(0, 0), p(1e-13, 1e-13));
		assertTrue(tiny.isOnEntity(p(5e-13, 5e-13)));
		assertFalse(tiny.isOnEntity(p(5e-13, 0)));
		Line big = new Line(p(0, 0), new Real(1), new Real(0));
		assertTrue(big.isOnEntity(p(1e9, 1e-4)));
		assertFalse(big.isOnEntity(p(1.0, 1e-4)));
		assertThrows(IllegalArgumentException.class, () -> new Line(p(0, 0), new Real(0), new Real(0)));
	}

	@Test
	@DisplayName("Piano: normale piccola ma non nulla e' valida, appartenenza relativa alla distanza dal riferimento")
	void planeScale() {
		Plane plane = new Plane(p(0, 0, 0), new Real(0), new Real(0), new Real(1e-15));
		assertEquals(1.0, plane.getNormalZ().getValue() / 1e-15, 1e-12);
		assertTrue(plane.isOnEntity(p(1e3, 1e3, 1e-10)));
		assertFalse(plane.isOnEntity(p(1.0, 1.0, 1e-3)));
		assertThrows(IllegalArgumentException.class, () -> new Plane(p(0, 0, 0), new Real(0), new Real(0), new Real(0)));
	}
}
