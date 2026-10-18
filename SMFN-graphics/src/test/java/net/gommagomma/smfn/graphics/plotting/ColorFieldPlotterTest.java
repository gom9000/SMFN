package net.gommagomma.smfn.graphics.plotting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.RgbColor;
import net.gommagomma.smfn.graphics.core.Viewport2D;
import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;

/** Test per {@link ColorFieldPlotter}: un punto per pixel, e la variante parallela uguale a quella sequenziale. */
@DisplayName("ColorFieldPlotter: un colore per pixel, versione parallela uguale alla sequenziale")
class ColorFieldPlotterTest
{
	private static final Viewport2D VIEW = new Viewport2D(-1, 1, -1, 1, 40, 30);
	private static final Mapping<Complex, Real> MODULUS = z -> new Real(Math.hypot(z.getRe(), z.getIm()));
	private static final ColorMapper<Real> GRAY = v -> {
		int level = (int) Math.max(0, Math.min(255, Math.round(v.getValue() * 200)));
		return new RgbColor(level, level, level);
	};

	@Test
	@DisplayName("Un punto per ogni pixel della superficie, ciascuno con il colore della funzione in quel punto")
	void onePointPerPixel() {
		RecordingRenderer r = new RecordingRenderer(40, 30);
		ColorFieldPlotter.plotFunction(r, VIEW, MODULUS, Complex::new, GRAY);
		assertEquals(40 * 30, r.points.size());
		for (int k = 0; k < r.points.size(); k++) {
			int[] p = r.points.get(k);
			double x = VIEW.convertPixelXToMathX(p[0]);
			double y = VIEW.convertPixelYToMathY(p[1]);
			assertEquals(GRAY.map(MODULUS.apply(new Complex(x, y))), r.pointColors.get(k));
		}
	}

	@Test
	@DisplayName("La versione parallela disegna gli stessi punti con gli stessi colori, nello stesso ordine")
	void parallelEqualsSequential() {
		RecordingRenderer sequential = new RecordingRenderer(40, 30);
		ColorFieldPlotter.plotFunction(sequential, VIEW, MODULUS, Complex::new, GRAY);
		RecordingRenderer parallel = new RecordingRenderer(40, 30);
		ColorFieldPlotter.plotFunctionParallel(parallel, VIEW, MODULUS, Complex::new, GRAY);

		assertEquals(sequential.points.size(), parallel.points.size());
		for (int k = 0; k < sequential.points.size(); k++) {
			assertEquals(sequential.points.get(k)[0], parallel.points.get(k)[0]);
			assertEquals(sequential.points.get(k)[1], parallel.points.get(k)[1]);
			assertEquals(sequential.pointColors.get(k), parallel.pointColors.get(k));
		}
	}
}
