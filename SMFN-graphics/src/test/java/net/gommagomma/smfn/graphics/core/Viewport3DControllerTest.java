package net.gommagomma.smfn.graphics.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test per {@link Viewport3DController}: gli eventi sono sintetici, quindi ogni rotazione attesa
 * si calcola dai pixel trascinati (0.5 gradi per pixel).
 */
@DisplayName("ViewportController3D: rotazione col trascinamento, limiti, eventi ignorati")
class Viewport3DControllerTest
{
	private static final double EPSILON = 1e-9;

	private final List<Viewport3D> notified = new ArrayList<>();

	private Viewport3DController controller(double azimuth, double elevation) {
		Viewport3D start = new Viewport3D(-2, 3, -4, 5, 0, 6, 640, 480, azimuth, elevation);
		return new Viewport3DController(start, notified::add);
	}

	@Test
	@DisplayName("Trascinare a destra di 40 px aumenta l'azimut di 20 gradi; a sinistra lo diminuisce")
	void horizontalDragChangesAzimuth() {
		notified.clear();
		Viewport3DController c = controller(10, 30);
		c.onDragStart(100, 100);
		c.onDrag(140, 100, true);
		assertEquals(30.0, c.getCurrentViewport().azimuth, EPSILON);
		assertEquals(30.0, c.getCurrentViewport().elevation, EPSILON);
		c.onDrag(100, 100, true);
		assertEquals(10.0, c.getCurrentViewport().azimuth, EPSILON);
		assertEquals(2, notified.size());
	}

	@Test
	@DisplayName("Trascinare in basso di 20 px aumenta l'elevazione di 10 gradi; in alto la diminuisce")
	void verticalDragChangesElevation() {
		notified.clear();
		Viewport3DController c = controller(0, 30);
		c.onDragStart(50, 50);
		c.onDrag(50, 70, true);
		assertEquals(40.0, c.getCurrentViewport().elevation, EPSILON);
		assertEquals(0.0, c.getCurrentViewport().azimuth, EPSILON);
		c.onDrag(50, 10, true); // -60 px dal punto precedente: -30 gradi
		assertEquals(10.0, c.getCurrentViewport().elevation, EPSILON);
	}

	@Test
	@DisplayName("Il trascinamento e' incrementale: due mosse da 10 px equivalgono a una da 20")
	void dragIsIncremental() {
		notified.clear();
		Viewport3DController twoSteps = controller(0, 0);
		twoSteps.onDragStart(0, 0);
		twoSteps.onDrag(10, 6, true);
		twoSteps.onDrag(20, 12, true);

		Viewport3DController oneStep = controller(0, 0);
		oneStep.onDragStart(0, 0);
		oneStep.onDrag(20, 12, true);

		assertEquals(oneStep.getCurrentViewport().azimuth, twoSteps.getCurrentViewport().azimuth, EPSILON);
		assertEquals(oneStep.getCurrentViewport().elevation, twoSteps.getCurrentViewport().elevation, EPSILON);
	}

	@Test
	@DisplayName("L'elevazione si ferma a +90 e a -90, e tornare indietro riparte dal puntatore")
	void elevationIsClamped() {
		notified.clear();
		Viewport3DController c = controller(0, 80);
		c.onDragStart(0, 0);
		c.onDrag(0, 1000, true);
		assertEquals(90.0, c.getCurrentViewport().elevation, EPSILON);
		int notifications = notified.size();

		c.onDrag(0, 1200, true); // ancora oltre il limite: niente cambia, niente notifica
		assertEquals(90.0, c.getCurrentViewport().elevation, EPSILON);
		assertEquals(notifications, notified.size());

		c.onDrag(0, 1180, true); // 20 px indietro: 10 gradi, subito
		assertEquals(80.0, c.getCurrentViewport().elevation, EPSILON);

		Viewport3DController down = controller(0, -80);
		down.onDragStart(0, 0);
		down.onDrag(0, -5000, true);
		assertEquals(-90.0, down.getCurrentViewport().elevation, EPSILON);
	}

	@Test
	@DisplayName("L'azimut resta in [-180, 180): attraversare il bordo lo riporta dentro")
	void azimuthIsWrapped() {
		notified.clear();
		Viewport3DController c = controller(170, 0);
		c.onDragStart(0, 0);
		c.onDrag(40, 0, true); // +20 gradi -> 190 -> -170
		assertEquals(-170.0, c.getCurrentViewport().azimuth, EPSILON);

		c.onDrag(0, 0, true); // -20 gradi -> -190 -> 170
		assertEquals(170.0, c.getCurrentViewport().azimuth, EPSILON);

		Viewport3DController full = controller(0, 0);
		full.onDragStart(0, 0);
		full.onDrag(720, 0, true); // un giro completo: 360 gradi
		assertEquals(0.0, full.getCurrentViewport().azimuth, EPSILON);
		for (Viewport3D v : notified) {
			assertEquals(true, v.azimuth >= -180.0 && v.azimuth < 180.0, "azimut " + v.azimuth);
		}
	}

	@Test
	@DisplayName("Eventi ignorati: nessun inizio, pulsante rilasciato, nessun movimento, dopo il rilascio")
	void ignoredEvents() {
		notified.clear();
		Viewport3DController c = controller(10, 10);
		Viewport3D start = c.getCurrentViewport();

		c.onDrag(50, 50, true); // senza onDragStart
		assertEquals(0, notified.size());

		c.onDragStart(0, 0);
		c.onDrag(50, 50, false); // pulsante non premuto
		assertEquals(0, notified.size());

		c.onDrag(0, 0, true); // nessun movimento
		assertEquals(0, notified.size());

		c.onDragEnd();
		c.onDrag(80, 80, true); // dopo il rilascio
		assertEquals(0, notified.size());
		assertSame(start, c.getCurrentViewport());

		c.onDragStart(5, 5); // si puo' ripartire
		c.onDrag(15, 5, true);
		assertEquals(1, notified.size());
	}

	@Test
	@DisplayName("La rotazione conserva riquadro e dimensioni, e la viewport notificata e' quella corrente")
	void rotationKeepsBoxAndPixels() {
		notified.clear();
		Viewport3DController c = controller(0, 0);
		c.onDragStart(0, 0);
		c.onDrag(30, 30, true);
		Viewport3D v = c.getCurrentViewport();
		assertSame(v, notified.get(notified.size() - 1));
		assertEquals(-2.0, v.minX, 0.0);
		assertEquals(3.0, v.maxX, 0.0);
		assertEquals(-4.0, v.minY, 0.0);
		assertEquals(5.0, v.maxY, 0.0);
		assertEquals(0.0, v.minZ, 0.0);
		assertEquals(6.0, v.maxZ, 0.0);
		assertEquals(640, v.pixelWidth);
		assertEquals(480, v.pixelHeight);
	}

	@Test
	@DisplayName("Argomenti nulli: IllegalArgumentException")
	void nullArguments() {
		notified.clear();
		Viewport3D start = new Viewport3D(-1, 1, -1, 1, -1, 1, 100, 100, 0, 0);
		assertThrows(IllegalArgumentException.class, () -> new Viewport3DController(null, v -> {}));
		assertThrows(IllegalArgumentException.class, () -> new Viewport3DController(start, null));
	}

	@Test
	@DisplayName("Rotellina: ogni scatto moltiplica lo zoom per 1.1 (indietro, lo divide) e notifica")
	void wheelZoomsByFixedFactor() {
		notified.clear();
		Viewport3DController c = controller(10, 30);
		c.onWheelZoom(320, 240, false);
		assertEquals(Viewport3DController.ZOOM_FACTOR, c.getCurrentViewport().zoom, EPSILON);
		c.onWheelZoom(320, 240, true);
		assertEquals(1.0, c.getCurrentViewport().zoom, EPSILON);
		assertEquals(2, notified.size());
		assertEquals(10.0, c.getCurrentViewport().azimuth, EPSILON);
		assertEquals(30.0, c.getCurrentViewport().elevation, EPSILON);
	}

	@Test
	@DisplayName("Rotellina: lo zoom si ferma ai limiti e al limite non notifica")
	void wheelZoomIsClamped() {
		notified.clear();
		Viewport3DController c = controller(0, 0);
		for (int k = 0; k < 200; k++) c.onWheelZoom(320, 240, false);
		assertEquals(Viewport3DController.MAX_ZOOM, c.getCurrentViewport().zoom, EPSILON);
		int count = notified.size();
		c.onWheelZoom(320, 240, false);
		assertEquals(count, notified.size());
		for (int k = 0; k < 400; k++) c.onWheelZoom(320, 240, true);
		assertEquals(Viewport3DController.MIN_ZOOM, c.getCurrentViewport().zoom, EPSILON);
	}

	@Test
	@DisplayName("Dopo uno zoom il trascinamento ruota senza perdere lo zoom")
	void dragKeepsZoom() {
		notified.clear();
		Viewport3DController c = controller(0, 0);
		c.onWheelZoom(320, 240, false);
		c.onDragStart(0, 0);
		c.onDrag(20, 0, true);
		assertEquals(10.0, c.getCurrentViewport().azimuth, EPSILON);
		assertEquals(Viewport3DController.ZOOM_FACTOR, c.getCurrentViewport().zoom, EPSILON);
	}

	@Test
	@DisplayName("Zoom sul cursore: il centro della scena si allontana dal cursore del fattore di zoom")
	void wheelZoomKeepsPointUnderCursor() {
		notified.clear();
		Viewport3DController c = controller(20, 30);
		Viewport3D before = c.getCurrentViewport();
		double centerX = before.projectToPixelX(0.5, 0.5, 3);   // il centro del riquadro
		double centerY = before.projectToPixelY(0.5, 0.5, 3);
		c.onWheelZoom(100, 50, false);
		Viewport3D after = c.getCurrentViewport();
		double f = Viewport3DController.ZOOM_FACTOR;
		assertEquals(100 + (centerX - 100) * f, after.projectToPixelX(0.5, 0.5, 3), 1e-9);
		assertEquals(50 + (centerY - 50) * f, after.projectToPixelY(0.5, 0.5, 3), 1e-9);
		// un punto qualunque della scena rispetta la stessa regola
		assertEquals(100 + (before.projectToPixelX(-1, 4, 5) - 100) * f, after.projectToPixelX(-1, 4, 5), 1e-9);
	}

	@Test
	@DisplayName("Zoom sul cursore al centro dello schermo: nessuno spostamento")
	void wheelZoomAtScreenCenterDoesNotPan() {
		notified.clear();
		Viewport3DController c = controller(0, 0);
		c.onWheelZoom(320, 240, false);
		assertEquals(0.0, c.getCurrentViewport().offsetX, EPSILON);
		assertEquals(0.0, c.getCurrentViewport().offsetY, EPSILON);
	}

	@Test
	@DisplayName("Spostamento: la scena segue il puntatore pixel per pixel, in modo incrementale")
	void panFollowsPointer() {
		notified.clear();
		Viewport3DController c = controller(10, 30);
		c.onPanStart(100, 100);
		c.onPan(130, 90, true);
		assertEquals(30.0, c.getCurrentViewport().offsetX, EPSILON);
		assertEquals(-10.0, c.getCurrentViewport().offsetY, EPSILON);
		c.onPan(140, 90, true);
		assertEquals(40.0, c.getCurrentViewport().offsetX, EPSILON);
		assertEquals(-10.0, c.getCurrentViewport().offsetY, EPSILON);
		assertEquals(2, notified.size());
		assertEquals(10.0, c.getCurrentViewport().azimuth, EPSILON);
		assertEquals(30.0, c.getCurrentViewport().elevation, EPSILON);
	}

	@Test
	@DisplayName("Spostamento: ignorato senza inizio, a pulsante rilasciato, senza movimento e dopo la fine")
	void panIgnoredEvents() {
		notified.clear();
		Viewport3DController c = controller(0, 0);
		c.onPan(10, 10, true);              // nessun inizio
		c.onPanStart(0, 0);
		c.onPan(10, 10, false);             // pulsante rilasciato
		c.onPan(0, 0, true);                // nessun movimento
		c.onPanEnd();
		c.onPan(50, 50, true);              // dopo la fine
		assertEquals(0, notified.size());
	}

	@Test
	@DisplayName("Spostamento e rotazione sono indipendenti: ruotare non perde lo spostamento")
	void rotationKeepsPan() {
		notified.clear();
		Viewport3DController c = controller(0, 0);
		c.onPanStart(0, 0);
		c.onPan(5, 7, true);
		c.onPanEnd();
		c.onDragStart(0, 0);
		c.onDrag(20, 0, true);
		assertEquals(5.0, c.getCurrentViewport().offsetX, EPSILON);
		assertEquals(7.0, c.getCurrentViewport().offsetY, EPSILON);
		assertEquals(10.0, c.getCurrentViewport().azimuth, EPSILON);
	}
}
