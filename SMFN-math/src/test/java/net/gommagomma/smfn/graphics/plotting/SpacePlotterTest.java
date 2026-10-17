package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.math.geometry.Point;

/** Test per {@link SpacePlotter}, con un renderer che registra segmenti e riempimenti. */
@DisplayName("SpacePlotter: curve e punti nello spazio, campioni non finiti, validazione")
class SpacePlotterTest
{
	private static final class RecordingRenderer implements Renderer
	{
		final List<int[]> lines = new ArrayList<>();
		final List<int[]> rects = new ArrayList<>();
		final List<RgbColor> colors = new ArrayList<>();
		private RgbColor color = RgbColor.BLACK;

		@Override public void startDrawing() {}
		@Override public void endDrawingAndFlush() {}
		@Override public int getWidth() { return 200; }
		@Override public int getHeight() { return 200; }
		@Override public void clear(RgbColor c) {}
		@Override public void setColor(RgbColor c) { color = c; }
		@Override public void drawPoint(int x, int y) {}
		@Override public void drawText(String text, int x, int y) {}
		@Override public void drawOverlayText(String text, int x, int y, RgbColor c) {}
		@Override public void fillPolygon(int[] xs, int[] ys) {}
		@Override public void drawLine(int x1, int y1, int x2, int y2) { lines.add(new int[] { x1, y1, x2, y2 }); colors.add(color); }
		@Override public void fillRect(int x, int y, int width, int height) { rects.add(new int[] { x, y, width, height }); colors.add(color); }
	}

	private static final Viewport3D VIEW = new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 0, 0);

	@Test
	@DisplayName("Curva: samples punti danno samples - 1 segmenti concatenati, dal primo punto all'ultimo")
	void curveIsAChainOfSegments() {
		RecordingRenderer r = new RecordingRenderer();
		SpacePlotter.plotCurve(r, VIEW, t -> new Point(t, 0, 0), -1, 1, 5, RgbColor.GREEN);
		assertEquals(4, r.lines.size());
		for (int k = 0; k < 4; k++) {
			if (k > 0) {
				assertEquals(r.lines.get(k - 1)[2], r.lines.get(k)[0]);
				assertEquals(r.lines.get(k - 1)[3], r.lines.get(k)[1]);
			}
		}
		// azimut 0, elevazione 0: x sta a destra, la retta e' orizzontale e va da 10 a 190
		assertEquals(10, r.lines.get(0)[0]);
		assertEquals(190, r.lines.get(3)[2]);
		for (int[] l : r.lines) assertEquals(l[1], l[3]);
		for (RgbColor c : r.colors) assertEquals(RgbColor.GREEN, c);
	}

	@Test
	@DisplayName("Curva: un punto non finito interrompe la spezzata, senza segmenti verso di esso")
	void nonFiniteBreaksTheCurve() {
		RecordingRenderer r = new RecordingRenderer();
		// 5 campioni in t = -1, -0.5, 0, 0.5, 1: quello in t = 0 e' NaN
		SpacePlotter.plotCurve(r, VIEW, t -> new Point(t, t == 0.0 ? Double.NaN : 0, 0), -1, 1, 5, RgbColor.GREEN);
		assertEquals(2, r.lines.size());
	}

	@Test
	@DisplayName("Curva: z sposta il punto sullo schermo solo se si guarda da una direzione che la vede")
	void zMovesTheCurveOnScreen() {
		RecordingRenderer r = new RecordingRenderer();
		SpacePlotter.plotCurve(r, VIEW, t -> new Point(0, 0, t), -1, 1, 3, RgbColor.GREEN);
		// azimut 0, elevazione 0: z e' la verticale, dal basso (190) verso l'alto (10)
		assertEquals(190, r.lines.get(0)[1]);
		assertEquals(10, r.lines.get(1)[3]);
	}

	@Test
	@DisplayName("Curva: argomenti non validi vengono rifiutati")
	void curveValidation() {
		RecordingRenderer r = new RecordingRenderer();
		assertThrows(IllegalArgumentException.class, () -> SpacePlotter.plotCurve(r, VIEW, t -> new Point(0, 0, 0), -1, 1, 1, RgbColor.GREEN));
		assertThrows(IllegalArgumentException.class, () -> SpacePlotter.plotCurve(r, VIEW, t -> new Point(0, 0, 0), 1, 1, 5, RgbColor.GREEN));
		assertThrows(IllegalArgumentException.class, () -> SpacePlotter.plotCurve(r, VIEW, t -> new Point(0, 0, 0), 2, 1, 5, RgbColor.GREEN));
		assertThrows(IllegalArgumentException.class, () -> SpacePlotter.plotCurve(r, VIEW, t -> new Point(0, 0, 0), 0, Double.NaN, 5, RgbColor.GREEN));
		assertThrows(IllegalArgumentException.class, () -> SpacePlotter.plotCurve(r, VIEW, t -> new Point(0.0, 0.0), 0, 1, 5, RgbColor.GREEN));
		assertThrows(IllegalArgumentException.class, () -> SpacePlotter.plotCurve(r, VIEW, t -> null, 0, 1, 5, RgbColor.GREEN));
	}

	@Test
	@DisplayName("Punti: un quadratino centrato su ciascun punto finito, del colore dato")
	void pointsAreSquares() {
		RecordingRenderer r = new RecordingRenderer();
		List<Point> points = List.of(new Point(0, 0, 0), new Point(1, 0, 0), new Point(Double.NaN, 0, 0));
		SpacePlotter.plotPoints(r, VIEW, points, RgbColor.RED);
		assertEquals(2, r.rects.size());
		// il centro e' (100, 100): il quadrato 3 x 3 parte da (99, 99)
		assertTrue(java.util.Arrays.equals(new int[] { 99, 99, 3, 3 }, r.rects.get(0)), java.util.Arrays.toString(r.rects.get(0)));
		for (RgbColor c : r.colors) assertEquals(RgbColor.RED, c);
	}

	@Test
	@DisplayName("Punti: nessun punto non disegna nulla; un punto senza tre coordinate e' rifiutato")
	void pointsEdgeCases() {
		RecordingRenderer r = new RecordingRenderer();
		SpacePlotter.plotPoints(r, VIEW, List.of(), RgbColor.RED);
		assertEquals(0, r.rects.size());
		assertThrows(IllegalArgumentException.class, () -> SpacePlotter.plotPoints(r, VIEW, List.of(new Point(1, 2)), RgbColor.RED));
	}
}
