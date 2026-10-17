package net.gommagomma.smfn.graphics.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test per {@link Viewport2D}: validazione, conversioni, proporzioni conservate. */
@DisplayName("Viewport2D: validazione, conversioni math <-> pixel, proporzioni")
class Viewport2DTest
{
	@Test
	@DisplayName("Riquadro quadrato su schermo quadrato: gli angoli vanno agli angoli, con Y invertita")
	void squareMapping() {
		Viewport2D v = new Viewport2D(0, 10, 0, 10, 100, 100);
		assertEquals(0, v.convertMathXToPixelX(0));
		assertEquals(100, v.convertMathXToPixelX(10));
		assertEquals(50, v.convertMathXToPixelX(5));
		assertEquals(100, v.convertMathYToPixelY(0));
		assertEquals(0, v.convertMathYToPixelY(10));
		assertEquals(5.0, v.convertPixelXToMathX(50), 1e-12);
		assertEquals(5.0, v.convertPixelYToMathY(50), 1e-12);
	}

	@Test
	@DisplayName("Proporzioni conservate: un riquadro largo su schermo quadrato lascia margini sopra e sotto")
	void aspectRatioKeptWithMargins() {
		Viewport2D v = new Viewport2D(0, 10, 0, 1, 100, 100);
		// 10 pixel per unita' su entrambi gli assi: l'altezza 1 occupa 10 pixel, centrati
		assertEquals(10, v.convertMathXToPixelX(1) - v.convertMathXToPixelX(0));
		assertEquals(10, v.convertMathYToPixelY(0) - v.convertMathYToPixelY(1));
		// il riquadro e' centrato in verticale: i suoi bordi stanno a 45 e 55
		assertEquals(55, v.convertMathYToPixelY(0), 1);
		assertEquals(45, v.convertMathYToPixelY(1), 1);
	}

	@Test
	@DisplayName("Conversioni inverse: pixel -> math -> pixel torna allo stesso pixel in X, entro un pixel in Y")
	void roundTrip() {
		Viewport2D v = new Viewport2D(-3, 7, -2, 5, 640, 480);
		for (int px = 0; px <= 640; px += 40) {
			assertEquals(px, v.convertMathXToPixelX(v.convertPixelXToMathX(px)), 1);
		}
		for (int py = 0; py <= 480; py += 40) {
			assertEquals(py, v.convertMathYToPixelY(v.convertPixelYToMathY(py)), 1);
		}
	}

	@Test
	@DisplayName("Parametri non validi: range degenere, invertito, non finito, o dimensioni pixel non positive")
	void validation() {
		assertThrows(IllegalArgumentException.class, () -> new Viewport2D(1, 1, 0, 1, 100, 100));
		assertThrows(IllegalArgumentException.class, () -> new Viewport2D(0, 1, 2, 1, 100, 100));
		assertThrows(IllegalArgumentException.class, () -> new Viewport2D(0, 1, 0, 0, 100, 100));
		assertThrows(IllegalArgumentException.class, () -> new Viewport2D(Double.NaN, 1, 0, 1, 100, 100));
		assertThrows(IllegalArgumentException.class, () -> new Viewport2D(0, Double.NaN, 0, 1, 100, 100));
		assertThrows(IllegalArgumentException.class, () -> new Viewport2D(0, 1, 0, Double.POSITIVE_INFINITY, 100, 100));
		assertThrows(IllegalArgumentException.class, () -> new Viewport2D(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 1, 100, 100));
		assertThrows(IllegalArgumentException.class, () -> new Viewport2D(0, 1, 0, 1, 0, 100));
		assertThrows(IllegalArgumentException.class, () -> new Viewport2D(0, 1, 0, 1, 100, -1));
	}
}
