package net.gommagomma.smfn.math.analysis.fractals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;

@DisplayName("MandelbrotFunction: il conteggio delle iterazioni viene letto correttamente da SolverResult")
class MandelbrotFunctionTest
{
	@Test
	@DisplayName("c = 0: punto interno noto (il centro della cardioide), l'orbita non diverge mai")
	void interiorPointNeverEscapesAndReturnsBudget() {
		MandelbrotFunction mandelbrot = new MandelbrotFunction(100);

		Natural iterations = mandelbrot.apply(new Complex(0, 0));

		assertEquals(new Natural(100), iterations);
	}

	@Test
	@DisplayName("c = 2: punto esterno noto, l'orbita diverge ben prima del budget massimo")
	void exteriorPointEscapesWellBeforeBudget() {
		MandelbrotFunction mandelbrot = new MandelbrotFunction(1000);

		Natural iterations = mandelbrot.apply(new Complex(2, 0));

		assertTrue(iterations.getValue() < 1000, "un punto chiaramente esterno deve fuggire ben prima del budget");
		assertTrue(iterations.getValue() > 0);
	}
}
