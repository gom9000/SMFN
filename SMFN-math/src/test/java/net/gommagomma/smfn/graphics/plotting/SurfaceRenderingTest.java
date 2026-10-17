package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Light;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.graphics.drivers.image.ImageRenderer;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Test sull'immagine finale, disegnata con {@link ImageRenderer}: quello che gli altri test dei
 * plotter non vedono, perche' controllano le chiamate al renderer e non i pixel. Due tipi:
 * <ul>
 *   <li>proprieta' dei pixel (una faccia vicina copre una lontana, le facce non lasciano fessure);</li>
 *   <li>confronto con immagini di riferimento in {@code src/test/resources}, per accorgersi di
 *       qualunque cambiamento del disegno. Le scene non contengono testo (i caratteri dipendono dai
 *       font della macchina) e si tollera una piccola frazione di pixel diversi.</li>
 * </ul>
 * Se un cambiamento del disegno e' voluto, le immagini di riferimento si rigenerano lanciando i test
 * con {@code -Dsmfn.updateGolden=<cartella src/test/resources/net/gommagomma/smfn/graphics/plotting>}
 * e guardandole prima di tenerle.
 */
@DisplayName("Disegno finale: occlusione, assenza di fessure, immagini di riferimento")
class SurfaceRenderingTest
{
	private static final RgbColor BACKGROUND = RgbColor.BLACK;

	private static final Mapping<Complex, Real> SINC = p -> {
		double r = Math.hypot(p.getRe(), p.getIm());
		return new Real(r == 0.0 ? 1.0 : Math.sin(r) / r);
	};
	private static final ColorMapper<Real> BY_HEIGHT = value -> {
		double t = Math.max(0.0, Math.min(1.0, (value.getValue() + 0.3) / 1.3));
		return RgbColor.ofHsb((float) (0.66 - 0.66 * t), 0.8f, 0.95f);
	};

	/** Un muro alto davanti (y < -0.5) e pianura dietro: la faccia ripida del muro copre la pianura lontana. */
	private static final Mapping<Complex, Real> WALL_IN_FRONT = p -> new Real(p.getIm() < -0.5 ? 1.0 : 0.0);
	private static final ColorMapper<Real> RED_IF_HIGH = value -> value.getValue() > 0.5 ? RgbColor.RED : RgbColor.BLUE;

	private static ImageRenderer canvas(int width, int height) {
		ImageRenderer r = new ImageRenderer(width, height);
		r.clear(BACKGROUND);
		return r;
	}

	private static int rgb(ImageRenderer r, int x, int y) {
		return r.getImage().getRGB(x, y) & 0xFFFFFF;
	}

	private static int rgb(RgbColor c) {
		return (c.r << 16) | (c.g << 8) | c.b;
	}

	// ---- Proprieta' dei pixel ----

	@Test
	@DisplayName("Facce piene, a colore per vertice, illuminate: nessun pixel di sfondo circondato da pixel della superficie")
	void facesLeaveNoGaps() {
		Viewport3D v = new Viewport3D(-12, 12, -12, 12, -0.3, 1, 400, 300, 35, 40);
		SurfaceGrid<Real> grid = SurfaceGrid.sample(v, SINC, Complex::new, Real::getValue, 60, 60);
		Light light = new Light(-40, 45, 0.25);

		for (int mode = 0; mode < 3; mode++) {
			ImageRenderer r = canvas(400, 300);
			if (mode == 0) SurfacePlotter.plotSurface(r, v, grid, BY_HEIGHT);
			else if (mode == 1) SurfacePlotter.plotSmoothSurface(r, v, grid, BY_HEIGHT);
			else SurfacePlotter.plotSmoothSurface(r, v, grid, BY_HEIGHT, light);

			int gaps = 0;
			for (int y = 1; y < 299; y++) {
				for (int x = 1; x < 399; x++) {
					if (rgb(r, x, y) == 0 && rgb(r, x - 1, y) != 0 && rgb(r, x + 1, y) != 0 && rgb(r, x, y - 1) != 0 && rgb(r, x, y + 1) != 0) {
						gaps++;
					}
				}
			}
			assertEquals(0, gaps, "modo " + mode);
		}
	}

	/** Pixel della pianura lontana, dietro la faccia ripida del muro, e il cui pixel ne e' coperto. */
	private static int[] farFloorPixel(Viewport3D v) {
		return new int[] { (int) Math.round(v.projectToPixelX(0.0, 0.9, 0.0)), (int) Math.round(v.projectToPixelY(0.0, 0.9, 0.0)) };
	}

	private static final Viewport3D WALL_VIEW = new Viewport3D(-1, 1, -1, 1, 0, 1, 400, 400, 0, 5);

	@Test
	@DisplayName("Occlusione: la faccia ripida del muro davanti copre la pianura lontana")
	void nearFaceHidesFarFloor() {
		ImageRenderer r = canvas(400, 400);
		// 19 celle in x: il punto x = 0 e' al centro di una cella, non su un lato; 20 in y: y = 0.9 e' un vertice
		SurfacePlotter.plotSurface(r, WALL_VIEW, WALL_IN_FRONT, Complex::new, Real::getValue, RED_IF_HIGH, 19, 20);
		int[] p = farFloorPixel(WALL_VIEW);
		assertEquals(rgb(RgbColor.RED), rgb(r, p[0], p[1]));
	}

	@Test
	@DisplayName("Linee nascoste: il reticolo trasparente mostra la riga lontana dietro il muro, quello a linee nascoste no")
	void hiddenLineWireframeHidesFarLines() {
		ImageRenderer transparent = canvas(400, 400);
		SurfacePlotter.plotWireframe(transparent, WALL_VIEW, WALL_IN_FRONT, Complex::new, Real::getValue, RgbColor.CYAN, 19, 20);
		ImageRenderer hidden = canvas(400, 400);
		SurfacePlotter.plotHiddenLineWireframe(hidden, WALL_VIEW, WALL_IN_FRONT, Complex::new, Real::getValue, RgbColor.CYAN, BACKGROUND, 19, 20);

		int[] p = farFloorPixel(WALL_VIEW);
		assertEquals(rgb(RgbColor.CYAN), rgb(transparent, p[0], p[1]));
		assertEquals(rgb(BACKGROUND), rgb(hidden, p[0], p[1]));
	}

	@Test
	@DisplayName("Le curve di livello stanno sulla superficie: dietro un muro non si vedono, senza il muro si")
	void levelCurvesAreHiddenToo() {
		// dietro y = -0.5 la superficie sale in pendenza e il livello 0.25 passa per y = 0
		Mapping<Complex, Real> slope = p -> new Real((p.getIm() + 0.5) * 0.5);
		Mapping<Complex, Real> slopeBehindWall = p -> new Real(p.getIm() < -0.5 ? 1.0 : (p.getIm() + 0.5) * 0.5);
		int x = (int) Math.round(WALL_VIEW.projectToPixelX(0.0, 0.0, 0.25));
		int y = (int) Math.round(WALL_VIEW.projectToPixelY(0.0, 0.0, 0.25));

		ImageRenderer open = canvas(400, 400);
		SurfacePlotter.plotLevelCurves(open, WALL_VIEW, slope, Complex::new, Real::getValue, v -> RgbColor.BLUE,
			new double[] { 0.25 }, level -> RgbColor.CYAN, 19, 20);
		assertEquals(rgb(RgbColor.CYAN), rgb(open, x, y));

		ImageRenderer walled = canvas(400, 400);
		SurfacePlotter.plotLevelCurves(walled, WALL_VIEW, slopeBehindWall, Complex::new, Real::getValue, v -> RgbColor.BLUE,
			new double[] { 0.25 }, level -> RgbColor.CYAN, 19, 20);
		assertTrue(rgb(walled, x, y) != rgb(RgbColor.CYAN));
	}

	// ---- Immagini di riferimento ----

	private static BufferedImage lit() {
		Viewport3D v = new Viewport3D(-12, 12, -12, 12, -0.3, 1, 240, 180, 35, 40);
		ImageRenderer r = canvas(240, 180);
		SurfaceAxisPlotter.plotWalls(r, v, RgbColor.DARK_GRAY);
		SurfacePlotter.plotSmoothSurface(r, v, SINC, Complex::new, Real::getValue, BY_HEIGHT, new Light(-40, 45, 0.25), 40, 40);
		SurfaceAxisPlotter.plotAxes(r, v, RgbColor.WHITE, false);
		return r.getImage();
	}

	private static BufferedImage hiddenLines() {
		Viewport3D v = new Viewport3D(-12, 12, -12, 12, -0.3, 1, 240, 180, 200, 30, 1.2, 10, -5);
		ImageRenderer r = canvas(240, 180);
		SurfacePlotter.plotHiddenLineWireframe(r, v, SINC, Complex::new, Real::getValue, RgbColor.CYAN, BACKGROUND, 30, 30);
		return r.getImage();
	}

	private static BufferedImage levelCurves() {
		Viewport3D v = new Viewport3D(-12, 12, -12, 12, -0.3, 1, 240, 180, 35, 40);
		ImageRenderer r = canvas(240, 180);
		SurfacePlotter.plotLevelCurves(r, v, SINC, Complex::new, Real::getValue, new RgbColor(30, 60, 90), RgbColor.CYAN, 8, 50, 50);
		return r.getImage();
	}

	private static BufferedImage planeLevelCurves() {
		Viewport2D v = new Viewport2D(-4, 4, -3, 3, 240, 180);
		ImageRenderer r = canvas(240, 180);
		LevelCurvePlotter.plotLevelCurves(r, v, SINC, Complex::new, Real::getValue, RgbColor.WHITE, 10, 120, 90);
		return r.getImage();
	}

	@Test @DisplayName("Riferimento: superficie liscia illuminata con pareti e assi")
	void goldenLit() throws IOException { compareWithGolden("lit.png", lit()); }

	@Test @DisplayName("Riferimento: reticolo a linee nascoste con zoom e spostamento")
	void goldenHiddenLines() throws IOException { compareWithGolden("hidden-lines.png", hiddenLines()); }

	@Test @DisplayName("Riferimento: curve di livello sulla superficie")
	void goldenLevelCurves() throws IOException { compareWithGolden("level-curves.png", levelCurves()); }

	@Test @DisplayName("Riferimento: curve di livello nel piano")
	void goldenPlaneLevelCurves() throws IOException { compareWithGolden("plane-level-curves.png", planeLevelCurves()); }

	/** Frazione massima di pixel che possono differire: copre piccole differenze di rasterizzazione fra JDK. */
	private static final double TOLERATED_DIFFERENT_PIXELS = 0.005;

	private static void compareWithGolden(String name, BufferedImage actual) throws IOException
	{
		String updateDir = System.getProperty("smfn.updateGolden");
		if (updateDir != null) {
			ImageIO.write(actual, "png", new File(updateDir, name));
			return;
		}

		try (InputStream in = SurfaceRenderingTest.class.getResourceAsStream(name)) {
			assertTrue(in != null, "immagine di riferimento mancante: " + name + " (si crea con -Dsmfn.updateGolden=<cartella>)");
			BufferedImage expected = ImageIO.read(in);
			assertEquals(expected.getWidth(), actual.getWidth(), name);
			assertEquals(expected.getHeight(), actual.getHeight(), name);

			int different = 0;
			for (int y = 0; y < actual.getHeight(); y++) {
				for (int x = 0; x < actual.getWidth(); x++) {
					if (expected.getRGB(x, y) != actual.getRGB(x, y)) different++;
				}
			}
			double fraction = (double) different / (actual.getWidth() * actual.getHeight());
			assertTrue(fraction <= TOLERATED_DIFFERENT_PIXELS,
				name + ": " + different + " pixel diversi (" + String.format("%.2f", 100.0 * fraction) + "%)");
		}
	}
}
