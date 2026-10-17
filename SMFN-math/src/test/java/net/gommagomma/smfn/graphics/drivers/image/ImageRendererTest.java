package net.gommagomma.smfn.graphics.drivers.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.RgbColor;

/**
 * Test per {@link ImageRenderer}: il suo risultato e' un'immagine, quindi si verifica leggendo i
 * pixel. Ogni primitiva e' provata anche con coordinate sul bordo e fuori dalla superficie.
 */
@DisplayName("ImageRenderer: primitive, clipping, trasparenza, salvataggio PNG")
class ImageRendererTest
{
	private static final int OPAQUE_BLACK = 0xFF000000;
	private static final int OPAQUE_WHITE = 0xFFFFFFFF;
	private static final int OPAQUE_RED = 0xFFFF0000;

	private static int pixel(ImageRenderer r, int x, int y) {
		return r.getImage().getRGB(x, y);
	}

	@Test
	@DisplayName("Le dimensioni sono quelle richieste; dimensioni non valide vengono rifiutate")
	void dimensions() {
		ImageRenderer r = new ImageRenderer(30, 20);
		assertEquals(30, r.getWidth());
		assertEquals(20, r.getHeight());
		assertEquals(30, r.getImage().getWidth());
		assertEquals(20, r.getImage().getHeight());
		assertThrows(IllegalArgumentException.class, () -> new ImageRenderer(0, 10));
		assertThrows(IllegalArgumentException.class, () -> new ImageRenderer(10, 0));
		assertThrows(IllegalArgumentException.class, () -> new ImageRenderer(-1, -1));
		new ImageRenderer(1, 1); // il minimo valido
	}

	@Test
	@DisplayName("clear riempie tutta la superficie, anche con un colore trasparente (sostituisce)")
	void clearFillsEverything() {
		ImageRenderer r = new ImageRenderer(5, 4);
		r.clear(RgbColor.WHITE);
		for (int y = 0; y < 4; y++) for (int x = 0; x < 5; x++) assertEquals(OPAQUE_WHITE, pixel(r, x, y));

		r.clear(new RgbColor(10, 20, 30, 0));
		assertEquals(0x000A141E, pixel(r, 2, 2)); // alfa 0, colore sostituito, non composto
	}

	@Test
	@DisplayName("drawPoint scrive il colore corrente sul pixel giusto e solo su quello")
	void drawPointWritesOnePixel() {
		ImageRenderer r = new ImageRenderer(10, 10);
		r.clear(RgbColor.BLACK);
		r.setColor(RgbColor.RED);
		r.drawPoint(3, 7);
		assertEquals(OPAQUE_RED, pixel(r, 3, 7));
		assertEquals(OPAQUE_BLACK, pixel(r, 7, 3));
		assertEquals(OPAQUE_BLACK, pixel(r, 4, 7));
		assertEquals(OPAQUE_BLACK, pixel(r, 3, 6));
	}

	@Test
	@DisplayName("drawPoint agli angoli scrive; subito fuori dai bordi non scrive e non lancia")
	void drawPointClipping() {
		ImageRenderer r = new ImageRenderer(4, 3);
		r.clear(RgbColor.BLACK);
		r.setColor(RgbColor.RED);
		r.drawPoint(0, 0);
		r.drawPoint(3, 2);
		assertEquals(OPAQUE_RED, pixel(r, 0, 0));
		assertEquals(OPAQUE_RED, pixel(r, 3, 2));

		int[][] outside = { { -1, 0 }, { 0, -1 }, { 4, 0 }, { 0, 3 }, { -1, -1 }, { 4, 3 }, { Integer.MIN_VALUE, 0 }, { 0, Integer.MAX_VALUE } };
		for (int[] p : outside) r.drawPoint(p[0], p[1]);
		int red = 0;
		for (int y = 0; y < 3; y++) for (int x = 0; x < 4; x++) if (pixel(r, x, y) == OPAQUE_RED) red++;
		assertEquals(2, red); // nessun pixel in piu' per i punti esterni
	}

	@Test
	@DisplayName("drawPoint trasparente: alfa 0 non cambia nulla, alfa meta' compone sul fondo")
	void drawPointAlpha() {
		ImageRenderer r = new ImageRenderer(3, 1);
		r.clear(RgbColor.WHITE);

		r.setColor(new RgbColor(255, 0, 0, 0));
		r.drawPoint(0, 0);
		assertEquals(OPAQUE_WHITE, pixel(r, 0, 0));

		r.setColor(new RgbColor(0, 0, 0, 128));
		r.drawPoint(1, 0);
		int composed = pixel(r, 1, 0);
		assertEquals(255, composed >>> 24); // fondo opaco: resta opaco
		int grey = (composed >> 16) & 0xFF;
		assertTrue(grey >= 126 && grey <= 128, "grigio atteso ~127, trovato " + grey);
		assertEquals(grey, (composed >> 8) & 0xFF);
		assertEquals(grey, composed & 0xFF);

		// su un fondo trasparente il pixel prende colore e alfa della sorgente
		r.setColor(new RgbColor(0, 0, 255, 100));
		r.drawPoint(2, 0);
		r.clear(new RgbColor(0, 0, 0, 0));
		r.drawPoint(2, 0);
		assertEquals(0x640000FF, pixel(r, 2, 0));
	}

	@Test
	@DisplayName("drawPoint e fillRect danno lo stesso risultato con la stessa trasparenza")
	void drawPointAgreesWithFillRectOnAlpha() {
		RgbColor translucentBlue = new RgbColor(0, 0, 255, 100);
		ImageRenderer viaPoint = new ImageRenderer(2, 2);
		ImageRenderer viaRect = new ImageRenderer(2, 2);
		viaPoint.clear(RgbColor.WHITE);
		viaRect.clear(RgbColor.WHITE);
		viaPoint.setColor(translucentBlue);
		viaRect.setColor(translucentBlue);
		viaPoint.drawPoint(0, 0);
		viaRect.fillRect(0, 0, 1, 1);
		int a = pixel(viaPoint, 0, 0);
		int b = pixel(viaRect, 0, 0);
		for (int shift = 0; shift <= 24; shift += 8) {
			int diff = Math.abs(((a >>> shift) & 0xFF) - ((b >>> shift) & 0xFF));
			assertTrue(diff <= 1, "canale a shift " + shift + ": " + a + " vs " + b);
		}
	}

	@Test
	@DisplayName("drawLine orizzontale e verticale copre gli estremi")
	void drawLine() {
		ImageRenderer r = new ImageRenderer(10, 10);
		r.clear(RgbColor.BLACK);
		r.setColor(RgbColor.RED);
		r.drawLine(1, 5, 8, 5);
		for (int x = 1; x <= 8; x++) assertEquals(OPAQUE_RED, pixel(r, x, 5));
		assertEquals(OPAQUE_BLACK, pixel(r, 0, 5));
		assertEquals(OPAQUE_BLACK, pixel(r, 9, 5));
		r.drawLine(-50, 2, 500, 2); // molto oltre i bordi: ritagliata senza eccezioni
		assertEquals(OPAQUE_RED, pixel(r, 0, 2));
		assertEquals(OPAQUE_RED, pixel(r, 9, 2));
	}

	@Test
	@DisplayName("fillRect copre [x, x+w) x [y, y+h); larghezza o altezza non positive non disegnano")
	void fillRect() {
		ImageRenderer r = new ImageRenderer(10, 10);
		r.clear(RgbColor.BLACK);
		r.setColor(RgbColor.RED);
		r.fillRect(2, 3, 4, 2);
		assertEquals(OPAQUE_RED, pixel(r, 2, 3));
		assertEquals(OPAQUE_RED, pixel(r, 5, 4));
		assertEquals(OPAQUE_BLACK, pixel(r, 6, 4));
		assertEquals(OPAQUE_BLACK, pixel(r, 5, 5));
		assertEquals(OPAQUE_BLACK, pixel(r, 1, 3));

		ImageRenderer empty = new ImageRenderer(4, 4);
		empty.clear(RgbColor.BLACK);
		empty.setColor(RgbColor.RED);
		empty.fillRect(0, 0, 0, 4);
		empty.fillRect(0, 0, 4, 0);
		empty.fillRect(0, 0, -3, -3);
		for (int y = 0; y < 4; y++) for (int x = 0; x < 4; x++) assertEquals(OPAQUE_BLACK, pixel(empty, x, y));
	}

	@Test
	@DisplayName("fillPolygon riempie l'interno, in entrambi i versi dei vertici; meno di 3 vertici non disegna")
	void fillPolygon() {
		int[] xs = { 1, 8, 8, 1 };
		int[] ys = { 1, 1, 8, 8 };
		int[] reversedXs = { 1, 8, 8, 1 };
		int[] reversedYs = { 8, 8, 1, 1 };

		ImageRenderer r = new ImageRenderer(10, 10);
		r.clear(RgbColor.BLACK);
		r.setColor(RgbColor.RED);
		r.fillPolygon(xs, ys);
		assertEquals(OPAQUE_RED, pixel(r, 4, 4));
		assertEquals(OPAQUE_BLACK, pixel(r, 0, 0));
		assertEquals(OPAQUE_BLACK, pixel(r, 9, 9));

		ImageRenderer reversed = new ImageRenderer(10, 10);
		reversed.clear(RgbColor.BLACK);
		reversed.setColor(RgbColor.RED);
		reversed.fillPolygon(reversedXs, reversedYs);
		for (int y = 0; y < 10; y++) for (int x = 0; x < 10; x++) assertEquals(pixel(r, x, y), pixel(reversed, x, y));

		ImageRenderer degenerate = new ImageRenderer(10, 10);
		degenerate.clear(RgbColor.BLACK);
		degenerate.setColor(RgbColor.RED);
		degenerate.fillPolygon(new int[] {}, new int[] {});
		degenerate.fillPolygon(new int[] { 1 }, new int[] { 1 });
		degenerate.fillPolygon(new int[] { 1, 8 }, new int[] { 1, 8 });
		for (int y = 0; y < 10; y++) for (int x = 0; x < 10; x++) assertEquals(OPAQUE_BLACK, pixel(degenerate, x, y));
	}

	@Test
	@DisplayName("fillPolygon fuori dalla superficie e' ritagliato; argomenti nulli o disuguali lanciano")
	void fillPolygonClippingAndValidation() {
		ImageRenderer r = new ImageRenderer(10, 10);
		r.clear(RgbColor.BLACK);
		r.setColor(RgbColor.RED);
		r.fillPolygon(new int[] { -100, 100, 100, -100 }, new int[] { -100, -100, 100, 100 }); // copre tutto
		assertEquals(OPAQUE_RED, pixel(r, 0, 0));
		assertEquals(OPAQUE_RED, pixel(r, 9, 9));
		r.fillPolygon(new int[] { 500, 600, 600 }, new int[] { 500, 500, 600 }); // tutto fuori: nessun effetto

		assertThrows(IllegalArgumentException.class, () -> r.fillPolygon(null, new int[] { 1, 2, 3 }));
		assertThrows(IllegalArgumentException.class, () -> r.fillPolygon(new int[] { 1, 2, 3 }, null));
		assertThrows(IllegalArgumentException.class, () -> r.fillPolygon(new int[] { 1, 2, 3 }, new int[] { 1, 2 }));
	}

	@Test
	@DisplayName("drawOverlayText disegna pixel e non cambia il colore corrente")
	void textDoesNotChangeCurrentColor() {
		ImageRenderer r = new ImageRenderer(120, 40);
		r.clear(RgbColor.BLACK);
		r.setColor(RgbColor.RED);
		r.drawOverlayText("abc", 5, 20, RgbColor.WHITE);
		boolean anyWhite = false;
		for (int y = 0; y < 40; y++) for (int x = 0; x < 120; x++) if (pixel(r, x, y) == OPAQUE_WHITE) anyWhite = true;
		assertTrue(anyWhite);

		r.drawPoint(100, 35); // il colore corrente e' ancora rosso, non quello dell'overlay
		assertEquals(OPAQUE_RED, pixel(r, 100, 35));
	}

	@Test
	@DisplayName("save scrive un PNG che si rilegge identico")
	void saveRoundTrip() throws IOException {
		ImageRenderer r = new ImageRenderer(6, 4);
		r.clear(RgbColor.WHITE);
		r.setColor(RgbColor.RED);
		r.drawPoint(2, 1);

		File file = File.createTempFile("smfn-image-renderer", ".png");
		try {
			r.save(file);
			BufferedImage read = ImageIO.read(file);
			assertEquals(6, read.getWidth());
			assertEquals(4, read.getHeight());
			assertEquals(OPAQUE_RED, read.getRGB(2, 1));
			assertEquals(OPAQUE_WHITE, read.getRGB(0, 0));
			assertNotEquals(OPAQUE_RED, read.getRGB(3, 1));
		} finally {
			file.delete();
		}
	}

	/** Quanti pixel dell'immagine non sono ne' nero ne' bianco puri: i livelli intermedi dell'antialiasing. */
	private static int intermediatePixels(ImageRenderer r) {
		int count = 0;
		for (int y = 0; y < r.getHeight(); y++) {
			for (int x = 0; x < r.getWidth(); x++) {
				int p = pixel(r, x, y);
				if (p != OPAQUE_BLACK && p != OPAQUE_WHITE) count++;
			}
		}
		return count;
	}

	@Test
	@DisplayName("Antialiasing spento (default): una linea obliqua usa solo il colore pieno")
	void antialiasingOffByDefault() {
		ImageRenderer r = new ImageRenderer(40, 40);
		assertEquals(false, r.isAntialiasing());
		r.clear(RgbColor.BLACK);
		r.setColor(RgbColor.WHITE);
		r.drawLine(2, 3, 37, 20);
		assertEquals(0, intermediatePixels(r));
	}

	@Test
	@DisplayName("Antialiasing acceso: la linea obliqua ha pixel di livello intermedio; i riempimenti no")
	void antialiasingSmoothsLinesNotFills() {
		ImageRenderer r = new ImageRenderer(40, 40);
		r.setAntialiasing(true);
		r.clear(RgbColor.BLACK);
		r.setColor(RgbColor.WHITE);
		r.drawLine(2, 3, 37, 20);
		assertTrue(intermediatePixels(r) > 10, "pixel intermedi: " + intermediatePixels(r));

		r.clear(RgbColor.BLACK);
		r.fillPolygon(new int[] { 2, 37, 20 }, new int[] { 3, 20, 35 });
		assertEquals(0, intermediatePixels(r));
	}

	@Test
	@DisplayName("Antialiasing: linee orizzontali e verticali restano nitide; si puo' spegnere di nuovo")
	void antialiasingAxisAlignedAndToggle() {
		ImageRenderer r = new ImageRenderer(40, 40);
		r.setAntialiasing(true);
		r.clear(RgbColor.BLACK);
		r.setColor(RgbColor.WHITE);
		r.drawLine(2, 10, 37, 10);
		r.drawLine(5, 2, 5, 37);
		assertEquals(0, intermediatePixels(r));

		r.setAntialiasing(false);
		r.clear(RgbColor.BLACK);
		r.drawLine(2, 3, 37, 20);
		assertEquals(0, intermediatePixels(r));
	}
}
