package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Light;
import net.gommagomma.smfn.graphics.core.Renderer;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport3D;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Test per {@link SurfacePlotter}, con un renderer che registra le primitive invece di disegnare.
 * La funzione di test e' f(x, y) = y (un piano inclinato): l'altezza e il colore dipendono solo
 * da y, quindi ordine di disegno e colori delle facce si leggono direttamente dalle chiamate.
 */
@DisplayName("SurfacePlotter: facce, ordine di disegno, wireframe, campioni non finiti")
class SurfacePlotterTest
{
	/** Registra poligoni (con il colore corrente) e segmenti. */
	private static final class RecordingRenderer implements Renderer
	{
		final List<int[][]> polygons = new ArrayList<>();
		final List<RgbColor> polygonColors = new ArrayList<>();
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
		@Override public void drawLine(int x1, int y1, int x2, int y2) {
			lines.add(new int[] { x1, y1, x2, y2 });
			lineColors.add(color);
		}
		@Override public void fillPolygon(int[] xs, int[] ys) {
			polygons.add(new int[][] { xs.clone(), ys.clone() });
			polygonColors.add(color);
		}
	}

	private static final Mapping<Complex, Real> HEIGHT_IS_Y = c -> new Real(c.getIm());
	private static final Mapping<Complex, Real> HEIGHT_IS_ZERO = c -> new Real(0.0);
	/** Il rosso codifica y in [-1,1] -> [0,200], cosi' il colore di una faccia dice dove sta. */
	private static final ColorMapper<Real> RED_BY_Y = v -> new RgbColor((int) Math.max(0, Math.min(255, Math.round(100 + 100 * v.getValue()))), 0, 0);

	private static Viewport3D view(double azimuth, double elevation) {
		return new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, azimuth, elevation);
	}

	private static void surface(RecordingRenderer r, Viewport3D v, Mapping<Complex, Real> f, int cx, int cy) {
		SurfacePlotter.plotSurface(r, v, f, Complex::new, Real::getValue, RED_BY_Y, cx, cy);
	}

	@Test
	@DisplayName("Una faccia per cella, ciascuna con quattro vertici")
	void oneQuadPerCell() {
		RecordingRenderer r = new RecordingRenderer();
		surface(r, view(30, 30), HEIGHT_IS_Y, 7, 5);
		assertEquals(35, r.polygons.size());
		for (int[][] p : r.polygons) {
			assertEquals(4, p[0].length);
			assertEquals(4, p[1].length);
		}
	}

	@Test
	@DisplayName("Una sola cella: un solo poligono, colorato dal valore nel centro (y = 0)")
	void singleCellUsesCenterValue() {
		RecordingRenderer r = new RecordingRenderer();
		surface(r, view(0, 90), HEIGHT_IS_Y, 1, 1);
		assertEquals(1, r.polygons.size());
		assertEquals(new RgbColor(100, 0, 0), r.polygonColors.get(0));
		// dall'alto con azimut 0 la faccia e' il quadrato 10..190 (y dello schermo invertito)
		int[][] p = r.polygons.get(0);
		assertEquals(10, Math.min(Math.min(p[0][0], p[0][1]), Math.min(p[0][2], p[0][3])));
		assertEquals(190, Math.max(Math.max(p[0][0], p[0][1]), Math.max(p[0][2], p[0][3])));
	}

	@Test
	@DisplayName("Ordine: azimut 0 -> si parte dal lato lontano (y alto) e si finisce al vicino (y basso)")
	void drawingOrderAzimuth0() {
		RecordingRenderer r = new RecordingRenderer();
		surface(r, view(0, 30), HEIGHT_IS_Y, 6, 6);
		assertNonIncreasingRed(r);
	}

	@Test
	@DisplayName("Ordine: azimut 180 -> il lato lontano e' y basso, l'ordine si inverte")
	void drawingOrderAzimuth180() {
		RecordingRenderer r = new RecordingRenderer();
		surface(r, view(180, 30), HEIGHT_IS_Y, 6, 6);
		assertNonDecreasingRed(r);
	}

	@Test
	@DisplayName("Ordine: a ogni cella le facce piu' lontane precedono le piu' vicine, per azimut e elevazioni estremi")
	void drawingOrderIsConsistentWithDepth() {
		double[] azimuths = { 0, 45, 90, 135, 180, 225, 270, -45, 360 };
		double[] elevations = { -90, -45, 0, 30, 90 };
		for (double az : azimuths) {
			for (double el : elevations) {
				Viewport3D v = view(az, el);
				RecordingRenderer r = new RecordingRenderer();
				surface(r, v, HEIGHT_IS_ZERO, 5, 4);
				// con z=0 le facce piatte si ordinano per profondita' orizzontale del centro:
				// ogni faccia deve essere piu' vicina (o pari) alle precedenti lungo ciascun asse
				// di griglia. Verificato ricostruendo l'indice di cella dal centro del poligono.
				assertEquals(20, r.polygons.size());
				boolean farHighX = v.horizontalDepth(1, -1) > v.horizontalDepth(-1, -1);
				boolean farHighY = v.horizontalDepth(-1, 1) > v.horizontalDepth(-1, -1);
				int k = 0;
				for (int a = 0; a < 5; a++) {
					int i = farHighX ? 4 - a : a;
					for (int b = 0; b < 4; b++) {
						int j = farHighY ? 3 - b : b;
						// vertice (i,j) della cella k-esima disegnata
						double x = -1 + 2.0 * i / 5;
						double y = -1 + 2.0 * j / 4;
						int px = (int) Math.round(v.projectToPixelX(x, y, 0));
						int py = (int) Math.round(v.projectToPixelY(x, y, 0));
						int[][] poly = r.polygons.get(k++);
						assertEquals(px, poly[0][0], "az=" + az + " el=" + el + " cella " + i + "," + j);
						assertEquals(py, poly[1][0], "az=" + az + " el=" + el + " cella " + i + "," + j);
					}
				}
			}
		}
	}

	@Test
	@DisplayName("Un campione non finito scarta le sole facce che lo toccano")
	void nonFiniteSampleDropsTouchingFaces() {
		// f = 1/x: infinito sulla colonna x = 0, che con 6 celle e' un vertice (i = 3)
		Mapping<Complex, Real> pole = c -> new Real(1.0 / c.getRe());
		RecordingRenderer r = new RecordingRenderer();
		surface(r, view(30, 30), pole, 6, 4);
		// restano 6*4 - 2*4 = 16 facce: le colonne di celle 2 e 3 toccano la colonna di vertici 3
		assertEquals(16, r.polygons.size());
	}

	@Test
	@DisplayName("NaN in un angolo scarta le sole 1..4 facce adiacenti")
	void nanAtSingleVertex() {
		// NaN solo in (0,0): 0.0/0.0, il vertice (i=3, j=2) con 6x4 celle
		Mapping<Complex, Real> hole = c -> new Real(c.getRe() == 0.0 && c.getIm() == 0.0 ? Double.NaN : 0.0);
		RecordingRenderer r = new RecordingRenderer();
		surface(r, view(30, 30), hole, 6, 4);
		assertEquals(24 - 4, r.polygons.size());
	}

	@Test
	@DisplayName("Funzione ovunque non finita: nessun disegno, nessuna eccezione")
	void allNonFiniteDrawsNothing() {
		Mapping<Complex, Real> nan = c -> new Real(Double.NaN);
		RecordingRenderer r = new RecordingRenderer();
		surface(r, view(30, 30), nan, 3, 3);
		SurfacePlotter.plotWireframe(r, view(30, 30), nan, Complex::new, Real::getValue, RgbColor.WHITE, 3, 3);
		assertEquals(0, r.polygons.size());
		assertEquals(0, r.lines.size());
	}

	@Test
	@DisplayName("Wireframe: (cx)(cy+1) + (cy)(cx+1) segmenti, tutti del colore dato")
	void wireframeSegmentCount() {
		RecordingRenderer r = new RecordingRenderer();
		SurfacePlotter.plotWireframe(r, view(30, 30), HEIGHT_IS_Y, Complex::new, Real::getValue, RgbColor.GREEN, 6, 4);
		assertEquals(6 * 5 + 4 * 7, r.lines.size());
		for (RgbColor c : r.lineColors) assertEquals(RgbColor.GREEN, c);
		assertEquals(0, r.polygons.size());
	}

	@Test
	@DisplayName("Wireframe: i segmenti che toccano un campione non finito vengono saltati")
	void wireframeSkipsNonFinite() {
		Mapping<Complex, Real> pole = c -> new Real(1.0 / c.getRe());
		RecordingRenderer r = new RecordingRenderer();
		SurfacePlotter.plotWireframe(r, view(30, 30), pole, Complex::new, Real::getValue, RgbColor.WHITE, 6, 4);
		// righe: 5 vertici-riga x (6 segmenti - 2 che toccano la colonna 3) = 5*4 ; colonne: 4 colonne finite... 
		// colonne di vertici x != 3: 6 colonne * 4 segmenti = 24 ; colonna x = 3 non ha segmenti
		assertEquals(5 * 4 + 6 * 4, r.lines.size());
	}

	@Test
	@DisplayName("La parte immaginaria del risultato puo' guidare il colore, l'altezza resta quella scelta")
	void colorMapperReceivesWholeValue() {
		// f(x, y) = (0, x): altezza = parte reale = 0, colore dalla parte immaginaria = x del centro
		Mapping<Complex, Complex> f = c -> new Complex(0.0, c.getRe());
		ColorMapper<Complex> byIm = v -> new RgbColor((int) Math.round(100 + 100 * v.getIm()), 0, 0);
		RecordingRenderer r = new RecordingRenderer();
		SurfacePlotter.plotSurface(r, view(0, 30), f, Complex::new, Complex::getRe, byIm, 2, 1);
		assertEquals(2, r.polygons.size());
		// centri a x = -0.5 e x = 0.5 -> rosso 50 e 150, in qualche ordine
		List<Integer> reds = new ArrayList<>();
		for (RgbColor c : r.polygonColors) reds.add(c.r);
		assertTrue(reds.contains(50) && reds.contains(150), "rossi: " + reds);
	}

	@Test
	@DisplayName("Risoluzione non valida: IllegalArgumentException")
	void invalidResolution() {
		RecordingRenderer r = new RecordingRenderer();
		assertThrows(IllegalArgumentException.class, () -> surface(r, view(0, 0), HEIGHT_IS_Y, 0, 4));
		assertThrows(IllegalArgumentException.class, () -> surface(r, view(0, 0), HEIGHT_IS_Y, 4, 0));
		assertThrows(IllegalArgumentException.class, () -> surface(r, view(0, 0), HEIGHT_IS_Y, -1, -1));
		assertThrows(IllegalArgumentException.class, () ->
			SurfacePlotter.plotWireframe(r, view(0, 0), HEIGHT_IS_Y, Complex::new, Real::getValue, RgbColor.WHITE, 0, 1));
		assertEquals(0, r.polygons.size());
	}

	@Test
	@DisplayName("Valori z fuori da [minZ, maxZ] sono tracciati comunque, senza eccezioni")
	void outOfRangeHeightIsStillDrawn() {
		Mapping<Complex, Real> tall = c -> new Real(1e6);
		RecordingRenderer r = new RecordingRenderer();
		surface(r, view(30, 30), tall, 3, 3);
		assertEquals(9, r.polygons.size());
	}

	// Le facce sono emesse una colonna di celle alla volta (6 per colonna nei test): dentro ogni
	// colonna il colore, cioe' y, deve scendere (o salire) in modo stretto.
	private static void assertNonIncreasingRed(RecordingRenderer r) {
		for (int k = 1; k < r.polygonColors.size(); k++) {
			if (k % 6 == 0) continue;
			assertTrue(r.polygonColors.get(k).r < r.polygonColors.get(k - 1).r, "posizione " + k);
		}
	}

	private static void assertNonDecreasingRed(RecordingRenderer r) {
		for (int k = 1; k < r.polygonColors.size(); k++) {
			if (k % 6 == 0) continue;
			assertTrue(r.polygonColors.get(k).r > r.polygonColors.get(k - 1).r, "posizione " + k);
		}
	}

	// --- Illuminazione ---

	private static final ColorMapper<Real> WHITE_FACES = v -> RgbColor.WHITE;

	private static void lit(RecordingRenderer r, Viewport3D v, Mapping<Complex, Real> f, Light light, int cx, int cy) {
		SurfacePlotter.plotSurface(r, v, f, Complex::new, Real::getValue, WHITE_FACES, light, cx, cy);
	}

	@Test
	@DisplayName("Luce dall'alto su una superficie piana: ogni faccia al colore pieno")
	void flatSurfaceUnderOverheadLight() {
		RecordingRenderer r = new RecordingRenderer();
		lit(r, view(30, 40), HEIGHT_IS_ZERO, new Light(0, 90, 0.2), 4, 4);
		assertEquals(16, r.polygons.size());
		for (RgbColor c : r.polygonColors) assertEquals(RgbColor.WHITE, c);
	}

	@Test
	@DisplayName("Luce radente su una superficie piana: ogni faccia al solo ambiente")
	void flatSurfaceUnderGrazingLight() {
		RecordingRenderer r = new RecordingRenderer();
		lit(r, view(30, 40), HEIGHT_IS_ZERO, new Light(0, 0, 0.4), 3, 3);
		for (RgbColor c : r.polygonColors) assertEquals(new RgbColor(102, 102, 102), c);
	}

	@Test
	@DisplayName("Un pendio e' piu' chiaro dal lato rivolto alla luce che dal lato opposto")
	void slopeIsBrighterTowardLight() {
		Mapping<Complex, Real> risingWithX = c -> new Real(c.getRe()); // sale verso x massimo
		// luce dal lato di x minimo (azimut 90) e luce dal lato di x massimo (azimut -90)
		RecordingRenderer fromLeft = new RecordingRenderer();
		RecordingRenderer fromRight = new RecordingRenderer();
		lit(fromLeft, view(0, 40), risingWithX, new Light(90, 20, 0.1), 3, 3);
		lit(fromRight, view(0, 40), risingWithX, new Light(-90, 20, 0.1), 3, 3);
		// la normale del pendio e' inclinata verso x minimo: la luce da li' la illumina di piu'
		assertTrue(fromLeft.polygonColors.get(0).r > fromRight.polygonColors.get(0).r,
			fromLeft.polygonColors.get(0) + " vs " + fromRight.polygonColors.get(0));
	}

	@Test
	@DisplayName("La luce non cambia la geometria: stessi poligoni, nello stesso ordine, con e senza luce")
	void lightDoesNotChangeGeometry() {
		RecordingRenderer plain = new RecordingRenderer();
		RecordingRenderer shaded = new RecordingRenderer();
		surface(plain, view(70, 30), HEIGHT_IS_Y, 5, 4);
		lit(shaded, view(70, 30), HEIGHT_IS_Y, new Light(0, 45, 0.3), 5, 4);
		assertEquals(plain.polygons.size(), shaded.polygons.size());
		for (int k = 0; k < plain.polygons.size(); k++) {
			assertTrue(java.util.Arrays.equals(plain.polygons.get(k)[0], shaded.polygons.get(k)[0]), "x faccia " + k);
			assertTrue(java.util.Arrays.equals(plain.polygons.get(k)[1], shaded.polygons.get(k)[1]), "y faccia " + k);
		}
	}

	@Test
	@DisplayName("Visto da sotto, una luce che sta sopra non illumina la faccia che si vede (solo ambiente)")
	void viewedFromBelowOverheadLightGivesAmbientOnly() {
		RecordingRenderer r = new RecordingRenderer();
		lit(r, view(0, -40), HEIGHT_IS_ZERO, new Light(0, 90, 0.5), 3, 3);
		for (RgbColor c : r.polygonColors) assertEquals(new RgbColor(128, 128, 128), c);
	}

	@Test
	@DisplayName("Con luce, il colore del mapper viene scalato e l'alfa resta quello del mapper")
	void colorMapperAlphaSurvivesShading() {
		ColorMapper<Real> translucent = v -> new RgbColor(200, 100, 50, 90);
		RecordingRenderer r = new RecordingRenderer();
		SurfacePlotter.plotSurface(r, view(0, 40), HEIGHT_IS_ZERO, Complex::new, Real::getValue, translucent,
			new Light(0, 0, 0.5), 2, 2);
		for (RgbColor c : r.polygonColors) assertEquals(new RgbColor(100, 50, 25, 90), c);
	}

	@Test
	@DisplayName("Luce nulla e risoluzione non valida: IllegalArgumentException; i campioni non finiti scartano le facce anche con luce")
	void lightedValidationAndNonFinite() {
		RecordingRenderer r = new RecordingRenderer();
		assertThrows(IllegalArgumentException.class, () ->
			SurfacePlotter.plotSurface(r, view(0, 0), HEIGHT_IS_Y, Complex::new, Real::getValue, WHITE_FACES, null, 4, 4));
		assertThrows(IllegalArgumentException.class, () -> lit(r, view(0, 0), HEIGHT_IS_Y, new Light(0, 0, 0.5), 0, 4));

		Mapping<Complex, Real> pole = c -> new Real(1.0 / c.getRe());
		lit(r, view(30, 30), pole, new Light(0, 45, 0.3), 6, 4);
		assertEquals(16, r.polygons.size());
	}

	@Test
	@DisplayName("Linee nascoste: ogni cella e' riempita con lo sfondo e poi bordata, nello stesso ordine delle facce")
	void hiddenLineWireframeFillsThenOutlinesInFaceOrder() {
		RecordingRenderer faces = new RecordingRenderer();
		surface(faces, view(30, 30), HEIGHT_IS_Y, 4, 3);

		RecordingRenderer r = new RecordingRenderer();
		RgbColor line = RgbColor.CYAN;
		RgbColor background = RgbColor.BLACK;
		SurfacePlotter.plotHiddenLineWireframe(r, view(30, 30), HEIGHT_IS_Y, Complex::new, Real::getValue, line, background, 4, 3);

		assertEquals(12, r.polygons.size());
		assertEquals(48, r.lines.size());
		for (int k = 0; k < 12; k++) {
			assertEquals(background, r.polygonColors.get(k));
			assertTrue(java.util.Arrays.equals(faces.polygons.get(k)[0], r.polygons.get(k)[0]));
			assertTrue(java.util.Arrays.equals(faces.polygons.get(k)[1], r.polygons.get(k)[1]));
		}
		for (RgbColor c : r.lineColors) assertEquals(line, c);
	}

	@Test
	@DisplayName("Linee nascoste: celle con un vertice non finito non vengono disegnate; celle meno di 1: eccezione")
	void hiddenLineWireframeSkipsNonFiniteAndValidates() {
		Mapping<Complex, Real> hole = c -> new Real(c.getRe() == 0.0 && c.getIm() == 0.0 ? Double.NaN : 0.0);
		RecordingRenderer r = new RecordingRenderer();
		SurfacePlotter.plotHiddenLineWireframe(r, view(30, 30), hole, Complex::new, Real::getValue, RgbColor.CYAN, RgbColor.BLACK, 4, 4);
		assertEquals(12, r.polygons.size());

		assertThrows(IllegalArgumentException.class, () -> SurfacePlotter.plotHiddenLineWireframe(
			new RecordingRenderer(), view(30, 30), HEIGHT_IS_Y, Complex::new, Real::getValue, RgbColor.CYAN, RgbColor.BLACK, 0, 4));
	}

	@Test
	@DisplayName("Curve di livello: facce piene piu' un segmento per ogni cella attraversata, a quota costante")
	void contoursOnSurface() {
		// h = y: con 4 righe i vertici stanno a y = -1, -0.5, 0, 0.5, 1; il livello 0.25 attraversa la sola riga j = 3
		RecordingRenderer r = new RecordingRenderer();
		SurfacePlotter.plotLevelCurves(r, view(0, 90), HEIGHT_IS_Y, Complex::new, Real::getValue, RED_BY_Y,
			new double[] { 0.25 }, level -> RgbColor.WHITE, 5, 4);
		assertEquals(20, r.polygons.size());
		assertEquals(5, r.lines.size());
		// dall'alto le curve di livello di un piano inclinato sono orizzontali sullo schermo
		int y = r.lines.get(0)[1];
		for (int[] l : r.lines) {
			assertEquals(y, l[1]);
			assertEquals(y, l[3]);
		}
	}

	@Test
	@DisplayName("Curve di livello: colore per livello, livelli nulli o celle meno di 1: eccezione")
	void contoursColorsAndValidation() {
		RecordingRenderer r = new RecordingRenderer();
		SurfacePlotter.plotLevelCurves(r, view(0, 90), HEIGHT_IS_Y, Complex::new, Real::getValue, RED_BY_Y,
			new double[] { 0.25 }, level -> RgbColor.GREEN, 5, 4);
		for (RgbColor c : r.lineColors) assertEquals(RgbColor.GREEN, c);

		assertThrows(IllegalArgumentException.class, () -> SurfacePlotter.plotLevelCurves(r, view(0, 90), HEIGHT_IS_Y,
			Complex::new, Real::getValue, RED_BY_Y, null, level -> RgbColor.WHITE, 5, 4));
		assertThrows(IllegalArgumentException.class, () -> SurfacePlotter.plotLevelCurves(r, view(0, 90), HEIGHT_IS_Y,
			Complex::new, Real::getValue, RED_BY_Y, new double[] { 0 }, level -> RgbColor.WHITE, 0, 4));
	}

	@Test
	@DisplayName("Curve di livello, forma semplice: facce di un colore, un livello al centro del campo di altezze")
	void levelCurvesSimpleForm() {
		// h = y su [-1, 1]: un solo livello cade a z = 0, che attraversa la riga di celle fra y = -0.25 e 0.25 con 5 righe di vertici
		RecordingRenderer r = new RecordingRenderer();
		SurfacePlotter.plotLevelCurves(r, view(0, 90), HEIGHT_IS_Y, Complex::new, Real::getValue,
			RgbColor.BLUE, RgbColor.WHITE, 1, 5, 4);
		assertEquals(20, r.polygons.size());
		for (RgbColor c : r.polygonColors) assertEquals(RgbColor.BLUE, c);
		assertEquals(5, r.lines.size());
		for (RgbColor c : r.lineColors) assertEquals(RgbColor.WHITE, c);
		assertThrows(IllegalArgumentException.class, () -> SurfacePlotter.plotLevelCurves(r, view(0, 90), HEIGHT_IS_Y,
			Complex::new, Real::getValue, RgbColor.BLUE, RgbColor.WHITE, 0, 5, 4));
	}

	// ---- Colore per vertice ----

	private static void smooth(RecordingRenderer r, Viewport3D v, Mapping<Complex, Real> f, ColorMapper<Real> cm, int cx, int cy) {
		SurfacePlotter.plotSmoothSurface(r, v, f, Complex::new, Real::getValue, cm, cx, cy);
	}

	private static Viewport3D bigView(double azimuth, double elevation) {
		return new Viewport3D(-1, 1, -1, 1, -1, 1, 800, 800, azimuth, elevation);
	}

	@Test
	@DisplayName("Colore per vertice, colore uniforme: una faccia sola per cella, come plotSurface")
	void smoothUniformColorIsOnePolygonPerCell() {
		RecordingRenderer r = new RecordingRenderer();
		smooth(r, bigView(30, 30), HEIGHT_IS_Y, v -> RgbColor.BLUE, 6, 4);
		assertEquals(24, r.polygons.size());
		for (RgbColor c : r.polygonColors) assertEquals(RgbColor.BLUE, c);
	}

	@Test
	@DisplayName("Colore per vertice: una cella grande e a colori diversi si divide in riquadri, con colori fra quelli dei vertici")
	void smoothSubdividesAndInterpolates() {
		RecordingRenderer r = new RecordingRenderer();
		smooth(r, bigView(0, 90), HEIGHT_IS_Y, RED_BY_Y, 1, 1);
		assertTrue(r.polygons.size() > 1, "riquadri: " + r.polygons.size());
		assertTrue(r.polygons.size() <= 1 + 64);
		int minRed = 255, maxRed = 0;
		// il primo poligono e' la faccia intera a colore medio: i riquadri sono gli altri
		for (RgbColor c : r.polygonColors.subList(1, r.polygonColors.size())) {
			// RED_BY_Y: da 0 (y = -1) a 200 (y = 1); il colore del vertice sta sempre in questo intervallo
			assertTrue(c.r >= 0 && c.r <= 200, "r=" + c.r);
			minRed = Math.min(minRed, c.r);
			maxRed = Math.max(maxRed, c.r);
		}
		// i riquadri coprono quasi tutto l'intervallo di colori della faccia
		assertTrue(minRed < 40 && maxRed > 160, minRed + ".." + maxRed);
	}

	@Test
	@DisplayName("Colore per vertice: i riquadri di una cella ne ricoprono la superficie")
	void smoothTilesTheCell() {
		// dall'alto la cella e' un quadrato qualunque sia l'altezza; i colori ai vertici differiscono (rosso dipende da y)
		RecordingRenderer one = new RecordingRenderer();
		surface(one, bigView(0, 90), HEIGHT_IS_Y, 1, 1);
		RecordingRenderer varied = new RecordingRenderer();
		smooth(varied, bigView(0, 90), HEIGHT_IS_Y, RED_BY_Y, 1, 1);
		assertTrue(varied.polygons.size() > 2);

		double whole = area(one.polygons.get(0));
		// il primo poligono e' la faccia intera, a colore medio; gli altri sono i riquadri che la ricoprono
		assertEquals(whole, area(varied.polygons.get(0)), 1e-9);
		double sum = 0;
		for (int k = 1; k < varied.polygons.size(); k++) sum += area(varied.polygons.get(k));
		assertEquals(whole, sum, whole * 0.02);

		// stesso colore ai vertici: la faccia non si divide
		RecordingRenderer same = new RecordingRenderer();
		smooth(same, bigView(0, 90), HEIGHT_IS_Y, v -> RgbColor.BLUE, 1, 1);
		assertEquals(1, same.polygons.size());
	}

	private static double area(int[][] p) {
		double a = 0;
		int n = p[0].length;
		for (int k = 0; k < n; k++) {
			int m = (k + 1) % n;
			a += (double) p[0][k] * p[1][m] - (double) p[0][m] * p[1][k];
		}
		return Math.abs(a) / 2.0;
	}

	@Test
	@DisplayName("Colore per vertice: stesso ordine delle facce di plotSurface; con la luce i colori non superano quelli senza")
	void smoothOrderAndLight() {
		Light light = new Light(-40, 45, 0.25);
		RecordingRenderer lit = new RecordingRenderer();
		SurfacePlotter.plotSmoothSurface(lit, bigView(30, 30), HEIGHT_IS_Y, Complex::new, Real::getValue, RED_BY_Y, light, 3, 3);
		RecordingRenderer unlit = new RecordingRenderer();
		smooth(unlit, bigView(30, 30), HEIGHT_IS_Y, RED_BY_Y, 3, 3);
		assertEquals(unlit.polygons.size(), lit.polygons.size());
		for (int k = 0; k < lit.polygons.size(); k++) {
			assertTrue(lit.polygonColors.get(k).r <= unlit.polygonColors.get(k).r, "k=" + k);
		}
		assertThrows(IllegalArgumentException.class, () -> SurfacePlotter.plotSmoothSurface(
			new RecordingRenderer(), bigView(30, 30), HEIGHT_IS_Y, Complex::new, Real::getValue, RED_BY_Y, (Light) null, 3, 3));
	}

	@Test
	@DisplayName("Colore per vertice: campioni non finiti saltati e celle meno di 1 rifiutate")
	void smoothSkipsNonFiniteAndValidates() {
		Mapping<Complex, Real> hole = c -> new Real(c.getRe() == 0.0 && c.getIm() == 0.0 ? Double.NaN : 0.0);
		RecordingRenderer r = new RecordingRenderer();
		smooth(r, view(30, 30), hole, v -> RgbColor.BLUE, 4, 4);
		assertEquals(12, r.polygons.size());
		assertThrows(IllegalArgumentException.class, () -> smooth(new RecordingRenderer(), view(30, 30), HEIGHT_IS_Y, RED_BY_Y, 0, 4));
	}

	// ---- Forma con campioni gia' presi ----

	@Test
	@DisplayName("Con la griglia si disegna come con la funzione, da punti di vista diversi, con una sola serie di valutazioni")
	void gridFormMatchesFunctionFormAndSamplesOnce() {
		int[] evaluations = { 0 };
		Mapping<Complex, Real> counted = c -> { evaluations[0]++; return new Real(c.getIm()); };
		SurfaceGrid<Real> grid = SurfaceGrid.sample(view(0, 0), counted, Complex::new, Real::getValue, 6, 4);
		assertEquals(7 * 5, evaluations[0]);

		for (double[] angles : new double[][] { { 30, 30 }, { 100, 50 }, { -45, 10 } }) {
			RecordingRenderer viaGrid = new RecordingRenderer();
			SurfacePlotter.plotWireframe(viaGrid, view(angles[0], angles[1]), grid, RgbColor.CYAN);
			RecordingRenderer viaFunction = new RecordingRenderer();
			SurfacePlotter.plotWireframe(viaFunction, view(angles[0], angles[1]), HEIGHT_IS_Y, Complex::new, Real::getValue, RgbColor.CYAN, 6, 4);
			assertEquals(viaFunction.lines.size(), viaGrid.lines.size());
			for (int k = 0; k < viaGrid.lines.size(); k++) assertTrue(java.util.Arrays.equals(viaFunction.lines.get(k), viaGrid.lines.get(k)));
		}
		assertEquals(7 * 5, evaluations[0], "il wireframe non deve rivalutare la funzione");

		// Le facce piene servono anche i centri, calcolati una volta sola
		RecordingRenderer r = new RecordingRenderer();
		SurfacePlotter.plotSurface(r, view(10, 20), grid, RED_BY_Y);
		SurfacePlotter.plotSurface(r, view(80, 20), grid, RED_BY_Y);
		assertEquals(7 * 5 + 6 * 4, evaluations[0]);
	}

	@Test
	@DisplayName("Una griglia si disegna solo con una viewport dello stesso riquadro")
	void gridRejectsDifferentBox() {
		SurfaceGrid<Real> grid = SurfaceGrid.sample(view(0, 0), HEIGHT_IS_Y, Complex::new, Real::getValue, 4, 4);
		Viewport3D other = new Viewport3D(-2, 1, -1, 1, -1, 1, 200, 200, 0, 0);
		assertThrows(IllegalArgumentException.class, () -> SurfacePlotter.plotWireframe(new RecordingRenderer(), other, grid, RgbColor.CYAN));
		assertThrows(IllegalArgumentException.class, () -> SurfacePlotter.plotSurface(new RecordingRenderer(), other, grid, RED_BY_Y));
		// zoom e spostamento non cambiano il riquadro
		Viewport3D zoomed = new Viewport3D(-1, 1, -1, 1, -1, 1, 200, 200, 15, 25, 3.0, 10, 10);
		SurfacePlotter.plotWireframe(new RecordingRenderer(), zoomed, grid, RgbColor.CYAN);
	}
}
