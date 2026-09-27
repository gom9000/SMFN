package net.gommagomma.smfn.math.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Test puntuali per {@link Ellipse}: costruzione, funzione implicita
 * f(P) = (x-cx)^2/a^2 + (y-cy)^2/b^2 - 1, e gradiente. Il catalogo usa i quattro vertici
 * dell'ellisse (dove f(P)=0 e' garantito algebricamente dalla definizione stessa, non da un
 * calcolo a mano) piu' un cerchio (a==b) come caso speciale per un confronto incrociato con
 * {@link Circle}.
 */
@DisplayName("Ellisse: costruzione, funzione implicita, gradiente, casi degeneri")
class EllipseTest
{
	private static final double EPSILON = 1e-9;
	private final Point center = new Point(1.0, -2.0);
	private final Real a = new Real(3.0);
	private final Real b = new Real(2.0);
	private final Ellipse ellipse = new Ellipse(center, a, b);

	@Test
	@DisplayName("Il costruttore rifiuta centro non 2D e semiassi non positivi")
	void constructorRejectsInvalidInputs() {
		assertThrows(IllegalArgumentException.class, () -> new Ellipse(new Point(0.0, 0.0, 0.0), a, b));
		assertThrows(IllegalArgumentException.class, () -> new Ellipse(center, new Real(0.0), b));
		assertThrows(IllegalArgumentException.class, () -> new Ellipse(center, a, new Real(-1.0)));
	}

	@Test
	@DisplayName("I quattro vertici dell'ellisse soddisfano f(P) = 0 per costruzione")
	void verticesSatisfyImplicitEquationByConstruction() {
		double cx = center.getX().getValue();
		double cy = center.getY().getValue();
		Point[] vertices = {
			new Point(cx + a.getValue(), cy),
			new Point(cx - a.getValue(), cy),
			new Point(cx, cy + b.getValue()),
			new Point(cx, cy - b.getValue())
		};
		for (Point v : vertices) {
			assertEquals(0.0, ellipse.implicitFunctionAt(v).getValue(), EPSILON, "vertice: " + v);
			assertTrue(ellipse.isOnEntity(v), "vertice: " + v);
		}
	}

	@Test
	@DisplayName("Il centro e' interno: f(centro) = -1 esattamente")
	void centerGivesMinusOne() {
		assertEquals(-1.0, ellipse.implicitFunctionAt(center).getValue(), EPSILON);
	}

	@Test
	@DisplayName("Punto ben oltre il semiasse maggiore e' esterno: f(P) > 0")
	void pointFarBeyondSemiAxisIsOutside() {
		double cx = center.getX().getValue();
		double cy = center.getY().getValue();
		Point farOutside = new Point(cx + 10 * a.getValue(), cy);
		assertTrue(ellipse.implicitFunctionAt(farOutside).getValue() > 0);
	}

	@Test
	@DisplayName("Con semiassi uguali, l'ellisse coincide con un cerchio dello stesso raggio (stesso segno di f)")
	void equalSemiAxesAgreeWithCircle() {
		Real radius = new Real(4.0);
		Ellipse circularEllipse = new Ellipse(center, radius, radius);
		Circle circle = new Circle(center, radius);

		Point[] samplePoints = { new Point(3.0, 1.0), new Point(-6.0, -5.0), new Point(1.0, -2.0), new Point(20.0, 20.0) };
		for (Point p : samplePoints) {
			boolean ellipseSaysInside = circularEllipse.implicitFunctionAt(p).getValue() < 0;
			boolean circleSaysInside = circle.implicitFunctionAt(p).getValue() < 0;
			assertEquals(circleSaysInside, ellipseSaysInside, "punto: " + p);
		}
	}

	@Test
	@DisplayName("apply() e implicitFunctionAt() sono coerenti (contratto GeometryEntity)")
	void applyAgreesWithImplicitFunctionAt() {
		Point p = new Point(2.0, -1.0);
		assertEquals(ellipse.implicitFunctionAt(p).getValue(), ellipse.apply(p.asVector()).getValue(), EPSILON);
		assertEquals(ellipse.isOnEntity(p), ellipse.isOnEntity(p.asVector()));
	}

	@Test
	@DisplayName("getEntityDimension() di un'ellisse e' 1")
	void entityDimensionIsOne() {
		assertEquals(1, ellipse.getEntityDimension());
	}

	@Test
	@DisplayName("Il gradiente nel vertice (cx+a, cy) e' orizzontale e punta verso l'esterno (2/a, 0)")
	void gradientAtMajorVertexIsHorizontalOutward() {
		double cx = center.getX().getValue();
		double cy = center.getY().getValue();
		Point vertex = new Point(cx + a.getValue(), cy);
		var gradient = ellipse.getGradient().apply(vertex.asVector());

		assertEquals(2.0 / a.getValue(), gradient.get(0).getValue(), EPSILON);
		assertEquals(0.0, gradient.get(1).getValue(), EPSILON);
	}

	@Test
	@DisplayName("Il gradiente nel centro e' il vettore nullo")
	void gradientAtCenterIsZero() {
		var gradient = ellipse.getGradient().apply(center.asVector());
		assertEquals(0.0, gradient.get(0).getValue(), EPSILON);
		assertEquals(0.0, gradient.get(1).getValue(), EPSILON);
	}

	@Test
	@DisplayName("I getter espongono centro e semiassi passati al costruttore")
	void gettersExposeConstructorArguments() {
		assertEquals(center, ellipse.getCenter());
		assertEquals(3.0, ellipse.getSemiAxisA().getValue(), EPSILON);
		assertEquals(2.0, ellipse.getSemiAxisB().getValue(), EPSILON);
	}

	@Test
	@DisplayName("implicitFunctionAt lancia su un punto non 2D")
	void nonTwoDimensionalPointThrows() {
		Point p3d = new Point(1.0, 2.0, 3.0);
		assertThrows(IllegalArgumentException.class, () -> ellipse.implicitFunctionAt(p3d));
	}
}
