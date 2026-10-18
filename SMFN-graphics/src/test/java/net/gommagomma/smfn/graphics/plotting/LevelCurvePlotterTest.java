package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/** Test per {@link LevelCurvePlotter}, con un renderer che registra i segmenti e il loro colore. */
@DisplayName("LevelCurvePlotter: livelli equidistanti, curve di f = x, colori, argomenti non validi")
class LevelCurvePlotterTest
{
	private static final class RecordingRenderer implements Renderer
	{
		final List<int[]> lines = new ArrayList<>();
		final List<RgbColor> lineColors = new ArrayList<>();
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
		@Override public void drawLine(int x1, int y1, int x2, int y2) {
			lines.add(new int[] { x1, y1, x2, y2 });
			lineColors.add(color);
		}
	}

	private static final Mapping<Complex, Real> F_IS_X = c -> new Real(c.getRe());
	private static final Viewport2D VIEW = new Viewport2D(-1, 1, -1, 1, 200, 200);

	private static RecordingRenderer plot(double[] levels, int cells) {
		RecordingRenderer r = new RecordingRenderer();
		LevelCurvePlotter.plotLevelCurves(r, VIEW, F_IS_X, Complex::new, Real::getValue, levels, level -> RgbColor.WHITE, cells, cells);
		return r;
	}

	@Test
	@DisplayName("evenLevels: count valori che dividono l'intervallo in count + 1 parti, estremi esclusi")
	void evenLevels() {
		assertArrayEquals(new double[] { 0.5 }, LevelCurvePlotter.evenLevels(0, 1, 1), 1e-12);
		assertArrayEquals(new double[] { 0.25, 0.5, 0.75 }, LevelCurvePlotter.evenLevels(0, 1, 3), 1e-12);
		assertArrayEquals(new double[] { -1.0, 1.0 }, LevelCurvePlotter.evenLevels(-3, 3, 2), 1e-12);
	}

	@Test
	@DisplayName("evenLevels: zero livelli o intervallo non valido: eccezione")
	void evenLevelsInvalid() {
		assertThrows(IllegalArgumentException.class, () -> LevelCurvePlotter.evenLevels(0, 1, 0));
		assertThrows(IllegalArgumentException.class, () -> LevelCurvePlotter.evenLevels(1, 1, 3));
		assertThrows(IllegalArgumentException.class, () -> LevelCurvePlotter.evenLevels(1, 0, 3));
		assertThrows(IllegalArgumentException.class, () -> LevelCurvePlotter.evenLevels(0, Double.NaN, 3));
	}

	@Test
	@DisplayName("f = x al livello 0.5: una retta verticale, un segmento per ogni riga di celle")
	void verticalLine() {
		RecordingRenderer r = plot(new double[] { 0.5 }, 8);
		assertEquals(8, r.lines.size());
		int x = VIEW.convertMathXToPixelX(0.5);
		for (int[] l : r.lines) {
			assertEquals(x, l[0]);
			assertEquals(x, l[2]);
		}
	}

	@Test
	@DisplayName("Un livello fuori dall'intervallo dei valori non disegna nulla")
	void levelOutsideRange() {
		assertEquals(0, plot(new double[] { 5.0, -5.0 }, 8).lines.size());
	}

	@Test
	@DisplayName("Ogni livello ha il proprio colore, scelto da levelColor")
	void colorPerLevel() {
		RecordingRenderer r = new RecordingRenderer();
		LevelCurvePlotter.plotLevelCurves(r, VIEW, F_IS_X, Complex::new, Real::getValue, new double[] { -0.5, 0.5 },
			level -> level < 0 ? RgbColor.BLUE : RgbColor.RED, 4, 4);
		assertEquals(8, r.lines.size());
		for (int k = 0; k < 4; k++) assertEquals(RgbColor.BLUE, r.lineColors.get(k));
		for (int k = 4; k < 8; k++) assertEquals(RgbColor.RED, r.lineColors.get(k));
	}

	@Test
	@DisplayName("Nessun livello: nessun segmento; celle meno di 1 o argomenti nulli: eccezione")
	void edgeCasesAndValidation() {
		assertEquals(0, plot(new double[0], 4).lines.size());
		assertThrows(IllegalArgumentException.class, () -> plot(new double[] { 0 }, 0));
		RecordingRenderer r = new RecordingRenderer();
		assertThrows(IllegalArgumentException.class,
			() -> LevelCurvePlotter.plotLevelCurves(r, VIEW, F_IS_X, Complex::new, Real::getValue, null, l -> RgbColor.WHITE, 4, 4));
		assertThrows(IllegalArgumentException.class,
			() -> LevelCurvePlotter.plotLevelCurves(r, VIEW, F_IS_X, Complex::new, Real::getValue, new double[] { 0 }, null, 4, 4));
	}

	@Test
	@DisplayName("Un campione non finito: le celle che lo toccano non hanno curva, le altre si")
	void nonFiniteSamples() {
		Mapping<Complex, Real> pole = c -> new Real(c.getRe() == 0.0 && c.getIm() == 0.0 ? Double.NaN : c.getRe());
		RecordingRenderer r = new RecordingRenderer();
		LevelCurvePlotter.plotLevelCurves(r, VIEW, pole, Complex::new, Real::getValue, new double[] { 0.5 }, l -> RgbColor.WHITE, 8, 8);
		// il campione (0, 0) e' un vertice (8 celle per lato): 4 celle perdute ma solo quelle che toccano il livello 0.5 contano
		assertEquals(8, r.lines.size());
		assertTrue(r.lines.stream().allMatch(l -> l[0] == VIEW.convertMathXToPixelX(0.5)));
	}

	@Test
	@DisplayName("Forma semplice: i livelli stanno fra minimo e massimo dei valori. f = x, 3 livelli: x = -0.5, 0, 0.5")
	void simpleFormSpacesLevelsInTheValueRange() {
		RecordingRenderer r = new RecordingRenderer();
		LevelCurvePlotter.plotLevelCurves(r, VIEW, F_IS_X, Complex::new, Real::getValue, RgbColor.WHITE, 3, 8, 8);
		assertEquals(24, r.lines.size());
		java.util.Set<Integer> xs = new java.util.TreeSet<>();
		for (int[] l : r.lines) xs.add(l[0]);
		assertEquals(java.util.Set.of(VIEW.convertMathXToPixelX(-0.5), VIEW.convertMathXToPixelX(0.0), VIEW.convertMathXToPixelX(0.5)), xs);
	}

	@Test
	@DisplayName("Forma semplice: funzione costante: nessuna curva; zero livelli: eccezione")
	void simpleFormEdgeCases() {
		RecordingRenderer r = new RecordingRenderer();
		LevelCurvePlotter.plotLevelCurves(r, VIEW, c -> new Real(3.0), Complex::new, Real::getValue, RgbColor.WHITE, 5, 8, 8);
		assertEquals(0, r.lines.size());
		assertThrows(IllegalArgumentException.class,
			() -> LevelCurvePlotter.plotLevelCurves(r, VIEW, F_IS_X, Complex::new, Real::getValue, RgbColor.WHITE, 0, 8, 8));
	}
}
