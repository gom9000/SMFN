package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/** Test per {@link CurvePlotter}: una retta, valori non finiti, salti fuori schermo. */
@DisplayName("CurvePlotter: curve y = f(x), valori non finiti, salti")
class CurvePlotterTest
{
	private static final Viewport2D VIEW = new Viewport2D(0, 100, 0, 100, 100, 100);

	@Test
	@DisplayName("y = x: un segmento per ogni coppia di pixel adiacenti, sulla diagonale")
	void diagonal() {
		RecordingRenderer r = new RecordingRenderer(100, 100);
		CurvePlotter.plotFunction(r, VIEW, x -> x, Real::new, Real::getValue);
		assertEquals(99, r.lines.size());
		for (int[] l : r.lines) {
			assertEquals(l[0] + l[1], 100, 1);          // sulla diagonale (y pixel = 100 - x pixel)
			assertEquals(l[0] + 1, l[2]);
		}
	}

	@Test
	@DisplayName("Un valore non finito (NaN, infinito) interrompe la curva: nessun segmento verso un punto inventato")
	void nonFiniteBreaksTheCurve() {
		RecordingRenderer r = new RecordingRenderer(100, 100);
		// NaN solo al pixel 50: spezza la retta in due tratti, 0..49 (49 segmenti) e 51..99 (48 segmenti)
		CurvePlotter.plotFunction(r, VIEW, x -> new Real(Math.abs(x.getValue() - 50) < 0.5 ? Double.NaN : x.getValue()),
			Real::new, Real::getValue);
		assertEquals(49 + 48, r.lines.size());
		for (int[] l : r.lines) {
			// la retta ha pendenza 1: nessun segmento verso un valore inventato (il vecchio codice ne tracciava uno verso il fondo)
			assertTrue(Math.abs(l[3] - l[1]) <= 2, java.util.Arrays.toString(l));
		}

		RecordingRenderer inf = new RecordingRenderer(100, 100);
		CurvePlotter.plotFunction(inf, VIEW, x -> new Real(x.getValue() < 30 ? Double.POSITIVE_INFINITY : 1.0),
			Real::new, Real::getValue);
		assertEquals(69, inf.lines.size());
	}

	@Test
	@DisplayName("Salto piu' alto del doppio dello schermo (un polo): nessun segmento attraverso il salto")
	void hugeJumpIsNotConnected() {
		RecordingRenderer r = new RecordingRenderer(100, 100);
		CurvePlotter.plotFunction(r, VIEW, x -> new Real(x.getValue() < 50 ? 1.0 : 1e6), Real::new, Real::getValue);
		assertEquals(98, r.lines.size());
	}
}
