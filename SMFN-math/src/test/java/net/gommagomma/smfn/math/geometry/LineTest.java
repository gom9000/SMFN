package net.gommagomma.smfn.math.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Test puntuali per {@link Line}: costruzione, funzione implicita (distanza con segno),
 * gradiente e casi degeneri. I casi numerici usano rette assiali (orizzontale/verticale) e
 * una tripla pitagorica (3,4,5), cosi' le distanze attese sono esatte e non calcolate a mano
 * su una geometria arbitraria.
 */
@DisplayName("Line: costruzione, funzione implicita, gradiente, casi degeneri")
class LineTest
{
	private static final double EPSILON = 1e-9;

	@Test
	@DisplayName("Il costruttore rifiuta origine non 2D e vettore direzione nullo")
	void constructorRejectsInvalidInputs() {
		assertThrows(IllegalArgumentException.class, () -> new Line(new Point(0.0, 0.0, 0.0), new Real(1.0), new Real(0.0)));
		assertThrows(IllegalArgumentException.class, () -> new Line(new Point(0.0, 0.0), new Real(0.0), new Real(0.0)));
	}

	@Test
	@DisplayName("through(a,b) costruisce la retta passante per due punti distinti")
	void throughBuildsLineFromTwoPoints() {
		Line line = Line.through(new Point(0.0, 0.0), new Point(1.0, 1.0));
		assertTrue(line.isOnEntity(new Point(0.0, 0.0)));
		assertTrue(line.isOnEntity(new Point(1.0, 1.0)));
		assertTrue(line.isOnEntity(new Point(5.0, 5.0)));
		assertTrue(line.isOnEntity(new Point(-2.0, -2.0)));
	}

	@Test
	@DisplayName("through(a,a) con punti coincidenti lancia (direzione nulla)")
	void throughWithCoincidentPointsThrows() {
		Point p = new Point(1.0, 1.0);
		assertThrows(IllegalArgumentException.class, () -> Line.through(p, p));
	}

	@Test
	@DisplayName("Retta orizzontale y=0: distanza con segno coincide esattamente con la coordinata Y")
	void horizontalLineSignedDistanceEqualsY() {
		Line xAxis = new Line(new Point(0.0, 0.0), new Real(1.0), new Real(0.0));
		assertEquals(0.0, xAxis.implicitFunctionAt(new Point(3.0, 0.0)).getValue(), EPSILON);
		assertEquals(5.0, Math.abs(xAxis.implicitFunctionAt(new Point(3.0, 5.0)).getValue()), EPSILON);
		assertEquals(5.0, Math.abs(xAxis.implicitFunctionAt(new Point(3.0, -5.0)).getValue()), EPSILON);
		assertTrue(xAxis.isOnEntity(new Point(-100.0, 0.0)));
	}

	@Test
	@DisplayName("distanceTo: tripla pitagorica (3,4,5) rispetto a una retta verticale")
	void distanceToUsesPythagoreanTriple() {
		// Retta verticale x=0 (asse Y): distanza di (3,4) dalla retta e' esattamente 3, non 5
		// (5 e' la distanza dall'origine, un errore facile in cui non cadere qui).
		Line yAxis = new Line(new Point(0.0, 0.0), new Real(0.0), new Real(1.0));
		assertEquals(3.0, yAxis.distanceTo(new Point(3.0, 4.0)).getValue(), EPSILON);
		assertEquals(0.0, yAxis.distanceTo(new Point(0.0, 4.0)).getValue(), EPSILON);
	}

	@Test
	@DisplayName("distanceTo non e' mai negativa, anche quando implicitFunctionAt lo e'")
	void distanceToIsAlwaysNonNegative() {
		Line xAxis = new Line(new Point(0.0, 0.0), new Real(1.0), new Real(0.0));
		Real signed = xAxis.implicitFunctionAt(new Point(0.0, -7.0));
		assertTrue(signed.getValue() < 0);
		assertEquals(7.0, xAxis.distanceTo(new Point(0.0, -7.0)).getValue(), EPSILON);
	}

	@Test
	@DisplayName("apply() e implicitFunctionAt() sono coerenti (contratto GeometryEntity)")
	void applyAgreesWithImplicitFunctionAt() {
		Line line = Line.through(new Point(0.0, 0.0), new Point(2.0, 1.0));
		Point p = new Point(1.0, 3.0);
		assertEquals(line.implicitFunctionAt(p).getValue(), line.apply(p.asVector()).getValue(), EPSILON);
		assertEquals(line.isOnEntity(p), line.isOnEntity(p.asVector()));
	}

	@Test
	@DisplayName("getEntityDimension() di una retta e' 1")
	void entityDimensionIsOne() {
		Line line = new Line(new Point(0.0, 0.0), new Real(1.0), new Real(0.0));
		assertEquals(1, line.getEntityDimension());
	}

	@Test
	@DisplayName("Il gradiente e' costante (retta affine), ha norma 1 ed e' perpendicolare alla direzione")
	void gradientIsConstantUnitAndPerpendicularToDirection() {
		Real dx = new Real(3.0);
		Real dy = new Real(4.0); // lunghezza 5, cosi' la normalizzazione e' verificabile a mano
		Line line = new Line(new Point(0.0, 0.0), dx, dy);

		var gradientAtOrigin = line.getGradient().apply(new Point(0.0, 0.0).asVector());
		var gradientElsewhere = line.getGradient().apply(new Point(100.0, -50.0).asVector());

		// Costante: lo stesso vettore in ogni punto
		assertEquals(gradientAtOrigin.get(0).getValue(), gradientElsewhere.get(0).getValue(), EPSILON);
		assertEquals(gradientAtOrigin.get(1).getValue(), gradientElsewhere.get(1).getValue(), EPSILON);

		// Norma 1
		double gx = gradientAtOrigin.get(0).getValue();
		double gy = gradientAtOrigin.get(1).getValue();
		assertEquals(1.0, Math.sqrt(gx * gx + gy * gy), EPSILON);

		// Perpendicolare alla direzione (dx,dy): prodotto scalare nullo
		assertEquals(0.0, gx * dx.getValue() + gy * dy.getValue(), EPSILON);
	}

	@Test
	@DisplayName("I getter espongono origine e direzione passate al costruttore")
	void gettersExposeConstructorArguments() {
		Point origin = new Point(1.0, 2.0);
		Line line = new Line(origin, new Real(5.0), new Real(-2.0));
		assertEquals(origin, line.getOrigin());
		assertEquals(5.0, line.getDirectionX().getValue(), EPSILON);
		assertEquals(-2.0, line.getDirectionY().getValue(), EPSILON);
	}

	@Test
	@DisplayName("implicitFunctionAt/isOnEntity lanciano su un punto non 2D")
	void nonTwoDimensionalPointThrows() {
		Line line = new Line(new Point(0.0, 0.0), new Real(1.0), new Real(0.0));
		Point p3d = new Point(1.0, 2.0, 3.0);
		assertThrows(IllegalArgumentException.class, () -> line.implicitFunctionAt(p3d));
	}
}
