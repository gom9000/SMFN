package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.ColorMapper;
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
				int[] prev = null;
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
}
