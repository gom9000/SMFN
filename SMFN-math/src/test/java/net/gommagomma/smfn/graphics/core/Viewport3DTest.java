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
}
