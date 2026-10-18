package net.gommagomma.smfn.graphics.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test per {@link Viewport3D}. Il riquadro [-1,1]^3 su una superficie 200x200 rende i valori
 * attesi esatti: centro a (100,100) e, con il 90% dello schermo occupato, scala 90 pixel per
 * unita' normalizzata.
 */
@DisplayName("Viewport3D: validazione, proiezione ortografica, profondita'")
class Viewport3DTest
{
	private static final double EPSILON = 1e-9;

	private static Viewport3D view(double azimuth, double elevation) {
		return new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, azimuth, elevation);
	}

	@Test
	@DisplayName("Il centro del riquadro cade al centro della superficie, per ogni punto di vista")
	void centerProjectsToScreenCenter() {
		double[] azimuths = { 0, 45, 90, 180, -90, 270, 1234.5 };
		double[] elevations = { -90, -30, 0, 1, 30, 90 };
		for (double az : azimuths) {
			for (double el : elevations) {
				Viewport3D v = view(az, el);
				assertEquals(100.0, v.projectToPixelX(0, 0, 0), EPSILON, "x az=" + az + " el=" + el);
				assertEquals(100.0, v.projectToPixelY(0, 0, 0), EPSILON, "y az=" + az + " el=" + el);
			}
		}
	}

	@Test
	@DisplayName("Azimut 0, elevazione 0: x a destra, z verso l'alto, y non sposta nulla")
	void frontalView() {
		Viewport3D v = view(0, 0);
		assertEquals(190.0, v.projectToPixelX(1, 0, 0), EPSILON);
		assertEquals(10.0, v.projectToPixelX(-1, 0, 0), EPSILON);
		assertEquals(10.0, v.projectToPixelY(0, 0, 1), EPSILON);
		assertEquals(190.0, v.projectToPixelY(0, 0, -1), EPSILON);
		assertEquals(v.projectToPixelX(0, -1, 0), v.projectToPixelX(0, 1, 0), EPSILON);
		assertEquals(v.projectToPixelY(0, -1, 0), v.projectToPixelY(0, 1, 0), EPSILON);
	}

	@Test
	@DisplayName("Dall'alto (elevazione 90): z non sposta nulla, y lontano sale sullo schermo")
	void topView() {
		Viewport3D v = view(0, 90);
		assertEquals(v.projectToPixelY(0.3, 0.3, -1), v.projectToPixelY(0.3, 0.3, 1), EPSILON);
		assertEquals(10.0, v.projectToPixelY(0, 1, 0), EPSILON);
		assertEquals(190.0, v.projectToPixelY(0, -1, 0), EPSILON);
	}

	@Test
	@DisplayName("Dal basso (elevazione -90): il verso di y sullo schermo si inverte")
	void bottomView() {
		Viewport3D v = view(0, -90);
		assertEquals(190.0, v.projectToPixelY(0, 1, 0), EPSILON);
		assertEquals(10.0, v.projectToPixelY(0, -1, 0), EPSILON);
	}

	@Test
	@DisplayName("Azimut 90: y diventa l'asse orizzontale (con segno opposto), x e' la profondita'")
	void azimuth90() {
		Viewport3D v = view(90, 0);
		assertEquals(10.0, v.projectToPixelX(0, 1, 0), EPSILON);
		assertEquals(190.0, v.projectToPixelX(0, -1, 0), EPSILON);
		assertEquals(100.0, v.projectToPixelX(1, 0, 0), EPSILON);
	}

	@Test
	@DisplayName("Azimut 180 e -180 sono la stessa vista; 360 equivale a 0")
	void azimuthPeriodicity() {
		Viewport3D a = view(180, 20);
		Viewport3D b = view(-180, 20);
		Viewport3D c = view(360, 20);
		Viewport3D d = view(0, 20);
		assertEquals(a.projectToPixelX(0.4, -0.7, 0.2), b.projectToPixelX(0.4, -0.7, 0.2), EPSILON);
		assertEquals(a.projectToPixelY(0.4, -0.7, 0.2), b.projectToPixelY(0.4, -0.7, 0.2), EPSILON);
		assertEquals(c.projectToPixelX(0.4, -0.7, 0.2), d.projectToPixelX(0.4, -0.7, 0.2), EPSILON);
		assertEquals(c.projectToPixelY(0.4, -0.7, 0.2), d.projectToPixelY(0.4, -0.7, 0.2), EPSILON);
	}

	@Test
	@DisplayName("Tutti gli otto vertici del riquadro stanno nella superficie, per ogni punto di vista")
	void cubeAlwaysFits() {
		for (double az = -180; az <= 180; az += 15) {
			for (double el = -90; el <= 90; el += 15) {
				Viewport3D v = view(az, el);
				for (double x = -1; x <= 1; x += 2) {
					for (double y = -1; y <= 1; y += 2) {
						for (double z = -1; z <= 1; z += 2) {
							double px = v.projectToPixelX(x, y, z);
							double py = v.projectToPixelY(x, y, z);
							assertTrue(px >= 0 && px <= 200 && py >= 0 && py <= 200,
								"fuori: az=" + az + " el=" + el + " -> " + px + ", " + py);
						}
					}
				}
			}
		}
	}

	@Test
	@DisplayName("Ogni asse e' normalizzato per conto suo: stesse proporzioni schermo con range diversi")
	void axesAreNormalizedIndependently() {
		Viewport3D unit = view(30, 25);
		Viewport3D scaled = new Viewport3D(0, 10, -5, 5, 100, 102, 200, 200, 30, 25);
		// il punto (0,0,0) del riquadro unitario e' il centro del riquadro scalato: (5, 0, 101)
		assertEquals(unit.projectToPixelX(0, 0, 0), scaled.projectToPixelX(5, 0, 101), EPSILON);
		assertEquals(unit.projectToPixelY(0, 0, 0), scaled.projectToPixelY(5, 0, 101), EPSILON);
		// e il vertice (1,1,1) corrisponde a (10, 5, 102)
		assertEquals(unit.projectToPixelX(1, 1, 1), scaled.projectToPixelX(10, 5, 102), EPSILON);
		assertEquals(unit.projectToPixelY(1, 1, 1), scaled.projectToPixelY(10, 5, 102), EPSILON);
	}

	@Test
	@DisplayName("horizontalDepth: con azimut 0 il lato lontano e' y alto; con 180 e' y basso; con 90 e' x alto")
	void horizontalDepthOrientation() {
		assertTrue(view(0, 30).horizontalDepth(0, 1) > view(0, 30).horizontalDepth(0, -1));
		assertTrue(view(180, 30).horizontalDepth(0, 1) < view(180, 30).horizontalDepth(0, -1));
		assertTrue(view(90, 30).horizontalDepth(1, 0) > view(90, 30).horizontalDepth(-1, 0));
		assertEquals(view(0, 30).horizontalDepth(0.2, 0.5), view(0, 30).horizontalDepth(-0.9, 0.5), EPSILON);
	}

	@Test
	@DisplayName("Parametri non validi vengono rifiutati con IllegalArgumentException")
	void invalidArguments() {
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(1, 1, -1, 1, -1, 1, 200, 200, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(-1, 1, 1, -1, -1, 1, 200, 200, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(-1, 1, -1, 1, 0, 0, 200, 200, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(Double.NaN, 1, -1, 1, -1, 1, 200, 200, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(-1, Double.POSITIVE_INFINITY, -1, 1, -1, 1, 200, 200, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(-1, 1, -1, 1, -1, 1, 0, 200, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(-1, 1, -1, 1, -1, 1, 200, -1, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, Double.NaN, 0));
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 0, 90.001));
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 0, -90.001));
	}

	@Test
	@DisplayName("directionToward: azimut 0 sta dal lato di y minimo; l'elevazione alza l'osservatore; direzione unitaria")
	void directionToward() {
		double[] front = Viewport3D.directionToward(0, 0);
		assertEquals(0.0, front[0], EPSILON);
		assertEquals(-1.0, front[1], EPSILON);
		assertEquals(0.0, front[2], EPSILON);

		double[] above = Viewport3D.directionToward(123, 90);
		assertEquals(0.0, above[0], EPSILON);
		assertEquals(0.0, above[1], EPSILON);
		assertEquals(1.0, above[2], EPSILON);

		double[] right = Viewport3D.directionToward(90, 0); // dal lato di x minimo
		assertEquals(-1.0, right[0], EPSILON);

		for (double az = -180; az <= 180; az += 45) {
			for (double el = -90; el <= 90; el += 30) {
				double[] d = Viewport3D.directionToward(az, el);
				assertEquals(1.0, Math.sqrt(d[0] * d[0] + d[1] * d[1] + d[2] * d[2]), EPSILON);
			}
		}
	}

	@Test
	@DisplayName("towardViewer e' la direzione verso il punto di vista; e' opposta alla profondita' crescente")
	void towardViewerAgreesWithDepth() {
		Viewport3D v = view(37, 20);
		double[] d = v.towardViewer();
		// spostarsi verso l'osservatore fa diminuire horizontalDepth
		double step = 0.01;
		assertTrue(v.horizontalDepth(d[0] * step, d[1] * step) < v.horizontalDepth(0, 0));
	}

	@Test
	@DisplayName("Coordinate normalizzate: -1 sul minimo, 0 al centro, +1 sul massimo, per ogni asse")
	void normalizedCoordinates() {
		Viewport3D v = new Viewport3D(10, 20, -4, 4, 0, 50, 200, 200, 0, 0);
		assertEquals(-1.0, v.normalizedX(10), EPSILON);
		assertEquals(0.0, v.normalizedX(15), EPSILON);
		assertEquals(1.0, v.normalizedX(20), EPSILON);
		assertEquals(-1.0, v.normalizedY(-4), EPSILON);
		assertEquals(0.0, v.normalizedY(0), EPSILON);
		assertEquals(1.0, v.normalizedY(4), EPSILON);
		assertEquals(-1.0, v.normalizedZ(0), EPSILON);
		assertEquals(0.0, v.normalizedZ(25), EPSILON);
		assertEquals(1.0, v.normalizedZ(50), EPSILON);
	}

	@Test
	@DisplayName("withView cambia solo il punto di vista: stesso riquadro e dimensioni; la proiezione coincide con una nuova viewport")
	void withView() {
		Viewport3D original = new Viewport3D(0, 10, -5, 5, 1, 3, 320, 240, 10, 10);
		Viewport3D rotated = original.withView(100, -20);
		assertEquals(100.0, rotated.azimuth, EPSILON);
		assertEquals(-20.0, rotated.elevation, EPSILON);
		assertEquals(original.minX, rotated.minX, 0.0);
		assertEquals(original.maxY, rotated.maxY, 0.0);
		assertEquals(original.minZ, rotated.minZ, 0.0);
		assertEquals(320, rotated.pixelWidth);
		assertEquals(240, rotated.pixelHeight);

		Viewport3D direct = new Viewport3D(0, 10, -5, 5, 1, 3, 320, 240, 100, -20);
		assertEquals(direct.projectToPixelX(3, 1, 2), rotated.projectToPixelX(3, 1, 2), EPSILON);
		assertEquals(direct.projectToPixelY(3, 1, 2), rotated.projectToPixelY(3, 1, 2), EPSILON);

		// l'originale non e' cambiata (immutabile)
		assertEquals(10.0, original.azimuth, EPSILON);
		assertThrows(IllegalArgumentException.class, () -> original.withView(0, 91));
	}

	@Test
	@DisplayName("Zoom: senza indicazione vale 1; con zoom 2 le distanze dal centro raddoppiano")
	void zoomScalesAroundCenter() {
		Viewport3D plain = new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 30, 20);
		Viewport3D zoomed = new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 30, 20, 2.0);
		assertEquals(1.0, plain.zoom, 0.0);
		for (double[] p : new double[][] { { 0, 0, 0 }, { 1, 1, 1 }, { -0.5, 0.3, 0.8 } }) {
			assertEquals(2.0 * (plain.projectToPixelX(p[0], p[1], p[2]) - 100.0) + 100.0, zoomed.projectToPixelX(p[0], p[1], p[2]), 1e-9);
			assertEquals(2.0 * (plain.projectToPixelY(p[0], p[1], p[2]) - 100.0) + 100.0, zoomed.projectToPixelY(p[0], p[1], p[2]), 1e-9);
		}
	}

	@Test
	@DisplayName("Zoom non valido (zero, negativo, NaN, infinito): eccezione")
	void invalidZoomThrows() {
		for (double zoom : new double[] { 0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY }) {
			assertThrows(IllegalArgumentException.class, () -> new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 0, 0, zoom));
		}
	}

	@Test
	@DisplayName("withView conserva lo zoom e withZoom conserva il punto di vista")
	void withViewAndWithZoomKeepTheOtherChannel() {
		Viewport3D v = new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 30, 20, 3.0);
		assertEquals(3.0, v.withView(50, 10).zoom, 0.0);
		Viewport3D z = v.withZoom(0.5);
		assertEquals(0.5, z.zoom, 0.0);
		assertEquals(30.0, z.azimuth, 0.0);
		assertEquals(20.0, z.elevation, 0.0);
	}

	@Test
	@DisplayName("Spostamento: sposta ogni punto proiettato dello stesso numero di pixel, per ogni punto di vista")
	void offsetShiftsEveryPoint() {
		for (double az : new double[] { 0, 33, 90, 180 }) {
			for (double el : new double[] { -90, 0, 40, 90 }) {
				Viewport3D plain = new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, az, el, 1.5);
				Viewport3D moved = new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, az, el, 1.5, 12.0, -7.0);
				assertEquals(plain.projectToPixelX(0.3, -0.4, 0.8) + 12.0, moved.projectToPixelX(0.3, -0.4, 0.8), 1e-9);
				assertEquals(plain.projectToPixelY(0.3, -0.4, 0.8) - 7.0, moved.projectToPixelY(0.3, -0.4, 0.8), 1e-9);
			}
		}
	}

	@Test
	@DisplayName("Spostamento non finito: eccezione; withOffset conserva il resto, withView e withZoom conservano lo spostamento")
	void offsetValidationAndWith() {
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 0, 0, 1.0, Double.NaN, 0));
		assertThrows(IllegalArgumentException.class, () -> new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 0, 0, 1.0, 0, Double.POSITIVE_INFINITY));
		Viewport3D v = new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 30, 20, 2.0, 5, 6);
		Viewport3D o = v.withOffset(-3, 4);
		assertEquals(-3.0, o.offsetX, 0.0);
		assertEquals(4.0, o.offsetY, 0.0);
		assertEquals(2.0, o.zoom, 0.0);
		assertEquals(30.0, o.azimuth, 0.0);
		assertEquals(5.0, v.withView(0, 0).offsetX, 0.0);
		assertEquals(6.0, v.withZoom(3).offsetY, 0.0);
	}
}
