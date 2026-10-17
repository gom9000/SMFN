package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport2D;

/** Test per {@link CartesianAxisPlotter}: assi, tacche a valori tondi, terminazione a coordinate estreme. */
@DisplayName("CartesianAxisPlotter: assi, tacche, intervalli estremi")
class CartesianAxisPlotterTest
{
	@Test
	@DisplayName("Senza tacche: i due assi attraverso lo zero")
	void justAxes() {
		RecordingRenderer r = new RecordingRenderer(100, 100);
		Viewport2D v = new Viewport2D(-10, 10, -10, 10, 100, 100);
		CartesianAxisPlotter.plotAxes(r, v, RgbColor.WHITE, false);
		assertEquals(2, r.lines.size());
		assertEquals(0, r.texts.size());
	}

	@Test
	@DisplayName("Asse fuori dallo schermo (zero non visibile): non viene disegnato")
	void offscreenAxis() {
		RecordingRenderer r = new RecordingRenderer(100, 100);
		Viewport2D v = new Viewport2D(5, 15, 5, 15, 100, 100);
		CartesianAxisPlotter.plotAxes(r, v, RgbColor.WHITE, false);
		assertEquals(0, r.lines.size());
	}

	@Test
	@DisplayName("Tacche a valori tondi nell'intervallo, senza lo zero; ogni tacca ha il suo valore scritto")
	void ticksAtRoundValues() {
		RecordingRenderer r = new RecordingRenderer(200, 200);
		Viewport2D v = new Viewport2D(-10, 10, -10, 10, 200, 200);
		CartesianAxisPlotter.plotAxes(r, v, RgbColor.WHITE, true);
		// l'intervallo 20 con circa 10 tacche da' un passo grezzo di 2, arrotondato per eccesso a 5: -10, -5, 5, 10
		assertTrue(r.texts.contains("5") && r.texts.contains("-5") && r.texts.contains("10") && r.texts.contains("-10"), r.texts.toString());
		assertEquals(false, r.texts.contains("0"));
		// due assi + una linea per tacca; le tacche sono tante quante le etichette
		assertEquals(2 + r.texts.size(), r.lines.size());
	}

	@Test
	@DisplayName("Intervallo piccolo vicino all'origine: le tacche ci sono (lo zero non si scambia con un valore minuscolo)")
	void tinyRange() {
		RecordingRenderer r = new RecordingRenderer(200, 200);
		Viewport2D v = new Viewport2D(-1e-12, 1e-12, -1e-12, 1e-12, 200, 200);
		CartesianAxisPlotter.plotAxes(r, v, RgbColor.WHITE, true);
		assertTrue(r.texts.size() >= 6, r.texts.toString());
	}

	@Test
	@DisplayName("Coordinate enormi con un intervallo minuscolo: il disegno termina (il passo non cambierebbe piu' il valore)")
	void hugeOffsetTerminates() {
		RecordingRenderer r = new RecordingRenderer(200, 200);
		Viewport2D v = new Viewport2D(1e15, 1e15 + 8.0, 1e15, 1e15 + 8.0, 200, 200);
		CartesianAxisPlotter.plotAxes(r, v, RgbColor.WHITE, true);
		assertTrue(r.texts.size() < 100);
	}
}
