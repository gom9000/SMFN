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
	@DisplayName("Etichette: il nome di ogni asse, piu' i valori delle tacche")
	void labels() {
		RecordingRenderer r = new RecordingRenderer();
		Viewport3D v = new Viewport3D(0, 2.5, -1, 1, 0, 1, 200, 200, 0, 30);
		SurfaceAxisPlotter.plotAxes(r, v, RgbColor.WHITE, true);
		assertTrue(r.texts.contains("x"), r.texts.toString());
		assertTrue(r.texts.contains("y"), r.texts.toString());
		assertTrue(r.texts.contains("z"), r.texts.toString());
	}

	@Test
	@DisplayName("Tacche: valori tondi dentro il range, e una tacca in piu' del conteggio dei valori")
	void ticksAtNiceValues() {
		RecordingRenderer r = new RecordingRenderer();
		SurfaceAxisPlotter.plotAxes(r, new Viewport3D(0, 10, 0, 10, 0, 10, 600, 600, 0, 30), RgbColor.WHITE, true);
		// assi lunghi: i valori 0, 2, 4, ... o simili, tutti multipli di uno stesso passo tondo e in [0, 10]
		java.util.List<Double> values = new java.util.ArrayList<>();
		for (String t : r.texts) {
			if (t.length() > 1 || Character.isDigit(t.charAt(0))) values.add(Double.parseDouble(t));
		}
		assertTrue(values.size() >= 6, values.toString());
		for (double v : values) {
			assertTrue(v >= 0 && v <= 10, values.toString());
			assertEquals(v, Math.round(v), 1e-9, values.toString()); // multipli interi: passo 1, 2 o 5
		}
		// ogni tacca e' un segmento, oltre ai tre assi
		assertEquals(3 + values.size(), r.lines.size());
	}

	@Test
	@DisplayName("Tacche: valori negativi e frazionari, formattati senza errori di virgola mobile")
	void ticksFractional() {
		RecordingRenderer r = new RecordingRenderer();
		SurfaceAxisPlotter.plotAxes(r, new Viewport3D(-1, 1, -1, 1, 0, 1, 600, 600, 0, 30), RgbColor.WHITE, true);
		assertTrue(r.texts.contains("-0.5"), r.texts.toString());
		assertTrue(r.texts.contains("0"), r.texts.toString());
		assertTrue(r.texts.contains("0.5"), r.texts.toString());
		for (String t : r.texts) assertTrue(t.length() <= 5, t);
	}

	@Test
	@DisplayName("Tacche: un asse visto di punta (z dall'alto) e' un punto e non porta tacche; senza etichette nessuna tacca")
	void noTicksWhenAxisIsFlatOrLabelsOff() {
		RecordingRenderer top = new RecordingRenderer();
		SurfaceAxisPlotter.plotAxes(top, new Viewport3D(-1, 1, -1, 1, 0, 1, 600, 600, 0, 90), RgbColor.WHITE, true);
		// x e y hanno le stesse tacche (riquadro simmetrico), z nessuna: il numero di tacche e' pari
		int ticks = top.lines.size() - 3;
		assertTrue(ticks > 0 && ticks % 2 == 0, "tacche: " + ticks);
		assertEquals(ticks + 3, top.texts.size());

		RecordingRenderer off = new RecordingRenderer();
		SurfaceAxisPlotter.plotAxes(off, new Viewport3D(-1, 1, -1, 1, 0, 1, 600, 600, 0, 30), RgbColor.WHITE, false);
		assertEquals(3, off.lines.size());
	}

	@Test
	@DisplayName("Pareti: tre bordi rettangolari piu' le linee di griglia sugli stessi valori delle tacche di ogni asse")
	void wallsHaveBordersAndGridLines() {
		Viewport3D v = new Viewport3D(-1, 1, -1, 1, -1, 1, 600, 600, 30, 30);
		RecordingRenderer axes = new RecordingRenderer();
		SurfaceAxisPlotter.plotAxes(axes, v, RgbColor.WHITE, true);
		int ticks = axes.lines.size() - 3;        // tacche dei tre assi: |xs| + |ys| + |zs|

		RecordingRenderer walls = new RecordingRenderer();
		SurfaceAxisPlotter.plotWalls(walls, v, RgbColor.DARK_GRAY);
		// 12 lati di bordo (3 rettangoli) + ogni valore compare in due pareti
		assertEquals(12 + 2 * ticks, walls.lines.size());
		for (RgbColor c : walls.lineColors) assertEquals(RgbColor.DARK_GRAY, c);
		assertEquals(0, walls.texts.size());
	}

	@Test
	@DisplayName("Pareti: guardando dall'alto il pavimento e' a minZ (in basso), dal basso il soffitto e' a maxZ (in alto)")
	void wallsFloorOrCeiling() {
		RecordingRenderer above = new RecordingRenderer();
		SurfaceAxisPlotter.plotWalls(above, new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 0, 0), RgbColor.WHITE);
		// elevazione 0: il pavimento e' visto di taglio, tutte le sue linee stanno alla stessa quota, in basso (190)
		int[] first = above.lines.get(0);
		assertEquals(190, first[1]);
		assertEquals(190, first[3]);

		RecordingRenderer below = new RecordingRenderer();
		SurfaceAxisPlotter.plotWalls(below, new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 0, -30), RgbColor.WHITE);
		// da sotto la prima linea e' del soffitto: sta nella meta' alta dell'immagine
		int[] ceiling = below.lines.get(0);
		assertTrue(ceiling[1] < 100 && ceiling[3] < 100, java.util.Arrays.toString(ceiling));
	}

	@Test
	@DisplayName("Pareti: per ogni punto di vista non lanciano e disegnano almeno i tre bordi")
	void wallsForEveryView() {
		for (double az : new double[] { 0, 45, 90, 180, 270, -90, 360 }) {
			for (double el : new double[] { -90, -30, 0, 30, 90 }) {
				RecordingRenderer r = new RecordingRenderer();
				SurfaceAxisPlotter.plotWalls(r, new Viewport3D(-1, 1, -1, 1, -1, 1, 400, 400, az, el), RgbColor.WHITE);
				assertTrue(r.lines.size() >= 12, "az=" + az + " el=" + el);
			}
		}
	}

	@Test
	@DisplayName("Tacche: il centro della scena dopo uno spostamento resta il riferimento per l'esterno")
	void ticksFollowPannedScene() {
		RecordingRenderer plain = new RecordingRenderer();
		SurfaceAxisPlotter.plotAxes(plain, new Viewport3D(-1, 1, -1, 1, 0, 1, 600, 600, 0, 30), RgbColor.WHITE, true);
		RecordingRenderer moved = new RecordingRenderer();
		SurfaceAxisPlotter.plotAxes(moved, new Viewport3D(-1, 1, -1, 1, 0, 1, 600, 600, 0, 30, 1.0, 40, -25), RgbColor.WHITE, true);
		assertEquals(plain.lines.size(), moved.lines.size());
		for (int k = 0; k < plain.lines.size(); k++) {
			int[] a = plain.lines.get(k);
			int[] b = moved.lines.get(k);
			for (int c = 0; c < 4; c++) assertEquals(a[c] + (c % 2 == 0 ? 40 : -25), b[c], 1, "linea " + k);
		}
	}
}
