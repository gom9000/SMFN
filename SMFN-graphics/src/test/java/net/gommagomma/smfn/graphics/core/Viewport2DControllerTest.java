package net.gommagomma.smfn.graphics.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test per {@link Viewport2DController}: panning, zoom a rettangolo, zoom con la rotellina, limiti. */
@DisplayName("Viewport2DController: panning, zoom selettivo, rotellina, rifiuto di riquadri non rappresentabili")
class Viewport2DControllerTest
{
	private static final double EPSILON = 1e-9;

	private final List<Viewport2D> updates = new ArrayList<>();
	private final List<int[]> rectangles = new ArrayList<>();

	private Viewport2DController controller(Viewport2D start) {
		updates.clear();
		rectangles.clear();
		return new Viewport2DController(start, new Viewport2DController.ViewportUpdateHandler() {
			@Override public void onViewportUpdated(Viewport2D v) { updates.add(v); }
			@Override public void onTemporaryDraw(int x, int y, int w, int h) { rectangles.add(new int[] { x, y, w, h }); }
		});
	}

	@Test
	@DisplayName("Panning: la scena segue il puntatore (trascinando a destra di 10 px, il riquadro va a sinistra di 10 px)")
	void panFollowsPointer() {
		Viewport2DController c = controller(new Viewport2D(0, 10, 0, 10, 100, 100));
		c.onDragStart(50, 50, false);
		c.onDrag(60, 50, true);
		assertEquals(-1.0, c.getCurrentViewport().minX, EPSILON);
		assertEquals(9.0, c.getCurrentViewport().maxX, EPSILON);
		assertEquals(0.0, c.getCurrentViewport().minY, EPSILON);
		// trascinando in basso la scena scende, quindi il riquadro sale
		c.onDrag(60, 60, true);
		assertEquals(1.0, c.getCurrentViewport().minY, EPSILON);
		assertEquals(2, updates.size());
	}

	@Test
	@DisplayName("Panning con proporzioni diverse da quelle dello schermo: stessa scala sui due assi")
	void panUsesSameScaleOnBothAxes() {
		// riquadro 10 x 1 su schermo 100 x 100: 10 pixel per unita' su entrambi gli assi
		Viewport2DController c = controller(new Viewport2D(0, 10, 0, 1, 100, 100));
		c.onDragStart(50, 50, false);
		c.onDrag(60, 60, true);
		assertEquals(-1.0, c.getCurrentViewport().minX, EPSILON);   // 10 pixel = 1 unita'
		assertEquals(1.0, c.getCurrentViewport().minY, EPSILON);    // anche in verticale
	}

	@Test
	@DisplayName("Panning ignorato a pulsante rilasciato")
	void panNeedsButton() {
		Viewport2DController c = controller(new Viewport2D(0, 10, 0, 10, 100, 100));
		c.onDragStart(50, 50, false);
		c.onDrag(60, 60, false);
		assertEquals(0, updates.size());
	}

	@Test
	@DisplayName("Zoom selettivo: il rettangolo trascinato diventa il nuovo riquadro; durante il trascinamento si segnala il rettangolo")
	void selectionZoom() {
		Viewport2DController c = controller(new Viewport2D(0, 10, 0, 10, 100, 100));
		c.onDragStart(10, 10, true);
		c.onDrag(30, 40, true);
		assertEquals(1, rectangles.size());
		assertEquals(10, rectangles.get(0)[0]);
		assertEquals(10, rectangles.get(0)[1]);
		assertEquals(20, rectangles.get(0)[2]);
		assertEquals(30, rectangles.get(0)[3]);
		assertEquals(0, updates.size());

		c.onDragEnd(30, 40);
		assertEquals(1.0, c.getCurrentViewport().minX, EPSILON);
		assertEquals(3.0, c.getCurrentViewport().maxX, EPSILON);
		assertEquals(6.0, c.getCurrentViewport().minY, EPSILON);
		assertEquals(9.0, c.getCurrentViewport().maxY, EPSILON);
		// il rettangolo temporaneo viene cancellato
		assertEquals(0, rectangles.get(rectangles.size() - 1)[2]);
	}

	@Test
	@DisplayName("Zoom selettivo con un rettangolo senza area (un clic): la viewport non cambia")
	void emptySelectionIsIgnored() {
		Viewport2DController c = controller(new Viewport2D(0, 10, 0, 10, 100, 100));
		c.onDragStart(10, 10, true);
		c.onDragEnd(10, 10);
		assertEquals(0, updates.size());
		assertEquals(0.0, c.getCurrentViewport().minX, 0.0);
	}

	@Test
	@DisplayName("Rotellina: ingrandisce del 10% tenendo fermo il punto sotto il cursore; indietro, l'inverso")
	void wheelZoomKeepsCursorPoint() {
		Viewport2D start = new Viewport2D(0, 10, 0, 10, 100, 100);
		Viewport2DController c = controller(start);
		double mathX = start.convertPixelXToMathX(25);
		double mathY = start.convertPixelYToMathY(75);

		c.onWheelZoom(25, 75, false);
		Viewport2D in = c.getCurrentViewport();
		assertEquals(10.0 / 1.1, in.rangeX, EPSILON);
		assertEquals(mathX, in.convertPixelXToMathX(25), 1e-6);
		assertEquals(mathY, in.convertPixelYToMathY(75), 1e-6);

		c.onWheelZoom(25, 75, true);
		assertEquals(10.0, c.getCurrentViewport().rangeX, 1e-9);
		assertEquals(0.0, c.getCurrentViewport().minX, 1e-9);
	}

	@Test
	@DisplayName("Rotellina con proporzioni diverse da quelle dello schermo: il punto sotto il cursore resta fermo")
	void wheelZoomWithMargins() {
		Viewport2D start = new Viewport2D(0, 10, 0, 1, 100, 100);
		Viewport2DController c = controller(start);
		double mathX = start.convertPixelXToMathX(30);
		double mathY = start.convertPixelYToMathY(52);
		c.onWheelZoom(30, 52, false);
		assertEquals(mathX, c.getCurrentViewport().convertPixelXToMathX(30), 1e-6);
		assertEquals(mathY, c.getCurrentViewport().convertPixelYToMathY(52), 1e-6);
	}

	@Test
	@DisplayName("Un riquadro piu' stretto della precisione dei double (relativa alla posizione) viene rifiutato")
	void unrepresentableRangeIsRejected() {
		// a coordinate di 1e6 una larghezza di 1e-7 e' sotto 1e-12 relativo: i double non la distinguono
		Viewport2D start = new Viewport2D(1e6, 1e6 + 1e-5, 1e6, 1e6 + 1e-5, 100, 100);
		Viewport2DController c = controller(start);
		for (int k = 0; k < 200; k++) c.onWheelZoom(50, 50, false);
		assertEquals(true, c.getCurrentViewport().rangeX > 1e6 * 1e-12);
		assertEquals(true, c.getCurrentViewport().rangeX > 0.0);
	}

	@Test
	@DisplayName("Un riquadro piccolo ma vicino all'origine si puo' ingrandire ben oltre il vecchio limite assoluto di 1e-10")
	void smallScaleNearOriginCanZoomDeep() {
		Viewport2D start = new Viewport2D(-1e-8, 1e-8, -1e-8, 1e-8, 100, 100);
		Viewport2DController c = controller(start);
		for (int k = 0; k < 40; k++) c.onWheelZoom(50, 50, false);
		assertEquals(true, c.getCurrentViewport().rangeX < 2e-8 / 30.0);
	}

	@Test
	@DisplayName("Argomenti nulli: eccezione")
	void nullArguments() {
		assertThrows(IllegalArgumentException.class, () -> new Viewport2DController(null, new Viewport2DController.ViewportUpdateHandler() {
			@Override public void onViewportUpdated(Viewport2D v) {}
			@Override public void onTemporaryDraw(int x, int y, int w, int h) {}
		}));
		assertThrows(IllegalArgumentException.class, () -> new Viewport2DController(new Viewport2D(0, 1, 0, 1, 10, 10), null));
	}
}
