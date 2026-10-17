package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;

/**
 * Test per {@link SurfaceAxisPlotter} e per l'angolo lontano di {@link Viewport3D}, con un
 * renderer che registra linee e testi.
 */
@DisplayName("SurfaceAxisPlotter: tre assi sull'angolo vicino, etichette, colore")
class SurfaceAxisPlotterTest
{
	private static final class RecordingRenderer implements Renderer
	{
		final List<int[]> lines = new ArrayList<>();
		final List<String> texts = new ArrayList<>();
		final List<RgbColor> lineColors = new ArrayList<>();
		private RgbColor color = RgbColor.BLACK;

		@Override public void startDrawing() {}
		@Override public void endDrawingAndFlush() {}
		@Override public int getWidth() { return 200; }
		@Override public int getHeight() { return 200; }
		@Override public void clear(RgbColor c) {}
		@Override public void setColor(RgbColor c) { color = c; }
		@Override public void drawPoint(int x, int y) {}
		@Override public void drawText(String text, int x, int y) { texts.add(text); }
		@Override public void drawOverlayText(String text, int x, int y, RgbColor c) {}
		@Override public void drawLine(int x1, int y1, int x2, int y2) { lines.add(new int[] { x1, y1, x2, y2 }); lineColors.add(color); }
		@Override public void fillPolygon(int[] xs, int[] ys) {}
	}

	private static Viewport3D view(double azimuth, double elevation) {
		return new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, azimuth, elevation);
	}

	@Test
	@DisplayName("Angolo lontano: azimut 0 -> y massimo; 180 -> y minimo; 90 -> x massimo; 270 -> x minimo")
	void farCorner() {
		assertEquals(true, view(0, 30).isFarSideMaxY());
		assertEquals(false, view(180, 30).isFarSideMaxY());
		assertEquals(true, view(90, 30).isFarSideMaxX());
		assertEquals(false, view(270, 30).isFarSideMaxX());
	}

	@Test
	@DisplayName("Tre assi sull'angolo vicino, del colore dato, per ogni punto di vista (estremi compresi)")
	void threeAxesWithColor() {
		double[] azimuths = { 0, 45, 90, 180, 270, -90, 360 };
		double[] elevations = { -90, 0, 30, 90 };
		for (double az : azimuths) {
			for (double el : elevations) {
				RecordingRenderer r = new RecordingRenderer();
				SurfaceAxisPlotter.plotAxes(r, view(az, el), RgbColor.GREEN, false);
				assertEquals(3, r.lines.size(), "az=" + az + " el=" + el);
				for (RgbColor c : r.lineColors) assertEquals(RgbColor.GREEN, c);
				assertEquals(0, r.texts.size());
			}
		}
	}

	@Test
	@DisplayName("Azimut 0, elevazione 0: asse x orizzontale, asse z verticale")
	void axesGeometryFrontal() {
		RecordingRenderer r = new RecordingRenderer();
		SurfaceAxisPlotter.plotAxes(r, view(0, 0), RgbColor.WHITE, false);
		int[] x = r.lines.get(0);
		int[] z = r.lines.get(2);
		assertEquals(x[1], x[3]);          // x: stessa quota sullo schermo
		assertEquals(10, Math.min(x[0], x[2]));
		assertEquals(190, Math.max(x[0], x[2]));
		assertEquals(z[0], z[2]);          // z: stessa colonna sullo schermo
		assertEquals(10, Math.min(z[1], z[3]));
		assertEquals(190, Math.max(z[1], z[3]));
	}

	@Test
	@DisplayName("Etichette: una per asse, con nome e range")
	void labels() {
		RecordingRenderer r = new RecordingRenderer();
		Viewport3D v = new Viewport3D(0, 2.5, -1, 1, 0, 1, 200, 200, 0, 30);
		SurfaceAxisPlotter.plotAxes(r, v, RgbColor.WHITE, true);
		assertEquals(3, r.texts.size());
		assertTrue(r.texts.contains("x [0, 2.5]"), r.texts.toString());
		assertTrue(r.texts.contains("y [-1, 1]"), r.texts.toString());
		assertTrue(r.texts.contains("z [0, 1]"), r.texts.toString());
	}
}
