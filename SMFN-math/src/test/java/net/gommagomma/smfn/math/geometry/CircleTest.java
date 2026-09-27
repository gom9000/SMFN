package net.gommagomma.smfn.math.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Test puntuali per {@link Circle}: costruzione, funzione implicita f(P) = dist(P,C) - r,
 * gradiente e caso degenere nel centro. Il catalogo di punti usa un cerchio di raggio 5
 * centrato nell'origine e il punto (3,4), sulla circonferenza per la tripla pitagorica
 * (3,4,5): il valore atteso di f() e' quindi 0 per costruzione, non un calcolo a mano.
 */
@DisplayName("Circle: costruzione, funzione implicita, gradiente, caso degenere nel centro")
class CircleTest
{
	private static final double EPSILON = 1e-9;
	private final Point center = new Point(0.0, 0.0);
	private final Circle circle = new Circle(center, new Real(5.0));

	@Test
	@DisplayName("Il costruttore rifiuta centro non 2D e raggio negativo")
	void constructorRejectsInvalidInputs() {
		assertThrows(IllegalArgumentException.class, () -> new Circle(new Point(0.0, 0.0, 0.0), new Real(1.0)));
		assertThrows(IllegalArgumentException.class, () -> new Circle(center, new Real(-1.0)));
	}

	@Test
	@DisplayName("Raggio zero e' accettato (cerchio degenere in un punto)")
	void zeroRadiusIsAccepted() {
		Circle degenerate = new Circle(center, new Real(0.0));
		assertTrue(degenerate.isOnEntity(center));
	}

	@Test
	@DisplayName("Punto sulla circonferenza (tripla pitagorica 3-4-5): f(P) = 0 esattamente")
	void pointOnCircumferenceGivesZero() {
		Point p = new Point(3.0, 4.0);
		assertEquals(0.0, circle.implicitFunctionAt(p).getValue(), EPSILON);
		assertTrue(circle.isOnEntity(p));
	}

	@Test
	@DisplayName("Punto interno: f(P) < 0; punto esterno: f(P) > 0")
	void insideIsNegativeOutsideIsPositive() {
		Point inside = new Point(1.0, 0.0);
		Point outside = new Point(10.0, 0.0);
		assertTrue(circle.implicitFunctionAt(inside).getValue() < 0);
		assertTrue(circle.implicitFunctionAt(outside).getValue() > 0);
	}

	@Test
	@DisplayName("Il centro stesso e' interno: f(centro) = -raggio")
	void centerItselfGivesNegativeRadius() {
		assertEquals(-5.0, circle.implicitFunctionAt(center).getValue(), EPSILON);
	}

	@Test
	@DisplayName("apply() e implicitFunctionAt() sono coerenti (contratto GeometryEntity)")
	void applyAgreesWithImplicitFunctionAt() {
		Point p = new Point(3.0, 4.0);
		assertEquals(circle.implicitFunctionAt(p).getValue(), circle.apply(p.asVector()).getValue(), EPSILON);
		assertEquals(circle.isOnEntity(p), circle.isOnEntity(p.asVector()));
	}

	@Test
	@DisplayName("getEntityDimension() di un cerchio e' 1")
	void entityDimensionIsOne() {
		assertEquals(1, circle.getEntityDimension());
	}

	@Test
	@DisplayName("Il gradiente su un punto della circonferenza e' il versore radiale uscente, di norma 1")
	void gradientOnCircumferenceIsOutwardUnitRadial() {
		Point p = new Point(3.0, 4.0); // sulla circonferenza
		var gradient = circle.getGradient().apply(p.asVector());
		double gx = gradient.get(0).getValue();
		double gy = gradient.get(1).getValue();

		// Norma 1
		assertEquals(1.0, Math.sqrt(gx * gx + gy * gy), EPSILON);
		// Direzione radiale uscente: (gx,gy) == (p - centro)/raggio == (3/5, 4/5)
		assertEquals(3.0 / 5.0, gx, EPSILON);
		assertEquals(4.0 / 5.0, gy, EPSILON);
	}

	@Test
	@DisplayName("Il gradiente nel centro esatto non e' definito e lancia")
	void gradientAtCenterThrows() {
		var gradientMapping = circle.getGradient();
		assertThrows(ArithmeticException.class, () -> gradientMapping.apply(center.asVector()));
	}

	@Test
	@DisplayName("I getter espongono centro e raggio passati al costruttore")
	void gettersExposeConstructorArguments() {
		assertEquals(center, circle.getCenter());
		assertEquals(5.0, circle.getRadius().getValue(), EPSILON);
	}

	@Test
	@DisplayName("implicitFunctionAt lancia su un punto non 2D")
	void nonTwoDimensionalPointThrows() {
		Point p3d = new Point(1.0, 2.0, 3.0);
		assertThrows(IllegalArgumentException.class, () -> circle.implicitFunctionAt(p3d));
	}
}
