package net.gommagomma.smfn.math.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Test puntuali per {@link Plane}: costruzione, funzione implicita (distanza con segno),
 * through(a,b,c) e gradiente. Il piano di riferimento e' z=0 (il piano xy), costruito con
 * through() sui tre punti canonici origine/asse-X/asse-Y: la normale attesa (0,0,+-1) e le
 * distanze sono quindi note per costruzione, non calcolate a mano su un piano arbitrario.
 */
@DisplayName("Plane: costruzione, funzione implicita, through(), gradiente")
class PlaneTest
{
	private static final double EPSILON = 1e-9;

	@Test
	@DisplayName("Il costruttore rifiuta origine non 3D e vettore normale nullo")
	void constructorRejectsInvalidInputs() {
		assertThrows(IllegalArgumentException.class, () -> new Plane(new Point(0.0, 0.0), new Real(0.0), new Real(0.0), new Real(1.0)));
		assertThrows(IllegalArgumentException.class, () -> new Plane(new Point(0.0, 0.0, 0.0), new Real(0.0), new Real(0.0), new Real(0.0)));
	}

	@Test
	@DisplayName("through(): il piano xy passa esattamente per i tre punti che lo definiscono")
	void throughBuildsPlanePassingThroughItsThreePoints() {
		Point origin = new Point(0.0, 0.0, 0.0);
		Point xAxisPoint = new Point(1.0, 0.0, 0.0);
		Point yAxisPoint = new Point(0.0, 1.0, 0.0);
		Plane xyPlane = Plane.through(origin, xAxisPoint, yAxisPoint);

		assertTrue(xyPlane.isOnEntity(origin));
		assertTrue(xyPlane.isOnEntity(xAxisPoint));
		assertTrue(xyPlane.isOnEntity(yAxisPoint));
		// Un quarto punto qualunque del piano xy (combinazione dei precedenti)
		assertTrue(xyPlane.isOnEntity(new Point(3.0, -2.0, 0.0)));
	}

	@Test
	@DisplayName("through() con tre punti collineari lancia (normale nulla)")
	void throughWithCollinearPointsThrows() {
		Point a = new Point(0.0, 0.0, 0.0);
		Point b = new Point(1.0, 0.0, 0.0);
		Point c = new Point(2.0, 0.0, 0.0); // sulla stessa retta di a e b
		assertThrows(IllegalArgumentException.class, () -> Plane.through(a, b, c));
	}

	@Test
	@DisplayName("distanceTo: un punto sull'asse Z e' a distanza pari alla sua quota dal piano xy")
	void distanceToEqualsHeightAboveXyPlane() {
		Plane xyPlane = Plane.through(new Point(0.0, 0.0, 0.0), new Point(1.0, 0.0, 0.0), new Point(0.0, 1.0, 0.0));
		assertEquals(5.0, xyPlane.distanceTo(new Point(0.0, 0.0, 5.0)).getValue(), EPSILON);
		assertEquals(5.0, xyPlane.distanceTo(new Point(0.0, 0.0, -5.0)).getValue(), EPSILON); // distanza sempre non negativa
	}

	@Test
	@DisplayName("implicitFunctionAt cambia segno attraversando il piano")
	void implicitFunctionChangesSignAcrossPlane() {
		Plane xyPlane = Plane.through(new Point(0.0, 0.0, 0.0), new Point(1.0, 0.0, 0.0), new Point(0.0, 1.0, 0.0));
		double above = xyPlane.implicitFunctionAt(new Point(0.0, 0.0, 3.0)).getValue();
		double below = xyPlane.implicitFunctionAt(new Point(0.0, 0.0, -3.0)).getValue();
		assertTrue(above * below < 0);
		assertEquals(Math.abs(above), Math.abs(below), EPSILON);
	}

	@Test
	@DisplayName("apply() e implicitFunctionAt() sono coerenti (contratto GeometryEntity)")
	void applyAgreesWithImplicitFunctionAt() {
		Plane plane = new Plane(new Point(0.0, 0.0, 0.0), new Real(0.0), new Real(0.0), new Real(1.0));
		Point p = new Point(1.0, 1.0, 4.0);
		assertEquals(plane.implicitFunctionAt(p).getValue(), plane.apply(p.asVector()).getValue(), EPSILON);
		assertEquals(plane.isOnEntity(p), plane.isOnEntity(p.asVector()));
	}

	@Test
	@DisplayName("getEntityDimension() di un piano e' 2")
	void entityDimensionIsTwo() {
		Plane plane = new Plane(new Point(0.0, 0.0, 0.0), new Real(0.0), new Real(0.0), new Real(1.0));
		assertEquals(2, plane.getEntityDimension());
	}

	@Test
	@DisplayName("Il gradiente e' costante (piano affine), ha norma 1 ed e' parallelo alla normale")
	void gradientIsConstantUnitAndParallelToNormal() {
		Real nx = new Real(0.0);
		Real ny = new Real(0.0);
		Real nz = new Real(2.0); // normale non normalizzata: lunghezza 2
		Plane plane = new Plane(new Point(0.0, 0.0, 0.0), nx, ny, nz);

		var gradientAtOrigin = plane.getGradient().apply(new Point(0.0, 0.0, 0.0).asVector());
		var gradientElsewhere = plane.getGradient().apply(new Point(50.0, -30.0, 7.0).asVector());

		// Costante
		for (int i = 0; i < 3; i++) {
			assertEquals(gradientAtOrigin.get(i).getValue(), gradientElsewhere.get(i).getValue(), EPSILON);
		}

		// Norma 1
		double gx = gradientAtOrigin.get(0).getValue();
		double gy = gradientAtOrigin.get(1).getValue();
		double gz = gradientAtOrigin.get(2).getValue();
		assertEquals(1.0, Math.sqrt(gx * gx + gy * gy + gz * gz), EPSILON);

		// Parallelo alla normale (0,0,2): stessa direzione (0,0,1)
		assertEquals(0.0, gx, EPSILON);
		assertEquals(0.0, gy, EPSILON);
		assertEquals(1.0, gz, EPSILON);
	}

	@Test
	@DisplayName("I getter espongono origine e normale passate al costruttore")
	void gettersExposeConstructorArguments() {
		Point origin = new Point(1.0, 2.0, 3.0);
		Plane plane = new Plane(origin, new Real(0.0), new Real(1.0), new Real(0.0));
		assertEquals(origin, plane.getOrigin());
		assertEquals(0.0, plane.getNormalX().getValue(), EPSILON);
		assertEquals(1.0, plane.getNormalY().getValue(), EPSILON);
		assertEquals(0.0, plane.getNormalZ().getValue(), EPSILON);
	}

	@Test
	@DisplayName("implicitFunctionAt lancia su un punto non 3D")
	void nonThreeDimensionalPointThrows() {
		Plane plane = new Plane(new Point(0.0, 0.0, 0.0), new Real(0.0), new Real(0.0), new Real(1.0));
		Point p2d = new Point(1.0, 2.0);
		assertThrows(IllegalArgumentException.class, () -> plane.implicitFunctionAt(p2d));
	}
}
