package net.gommagomma.smfn.math.analysis.fractals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;

@DisplayName("JuliaFunction: costante c fissa, punto iniziale z0 variabile per pixel")
class JuliaFunctionTest
{
	@Test
	@DisplayName("c = 0: l'orbita z0=0.5 -> 0.25 -> 0.0625 -> ... resta confinata e non diverge mai")
	void orbitBoundedTowardsZeroNeverEscapes() {
		JuliaFunction julia = new JuliaFunction(new Complex(0, 0), 100);

		Natural iterations = julia.apply(new Complex(0.5, 0));

		assertEquals(new Natural(100), iterations);
	}

	@Test
	@DisplayName("c = 2, z0 = 10: |z0|^2 = 100 supera gia' la soglia di fuga, diverge alla primissima valutazione (0 iterazioni)")
	void orbitStartingFarAwayEscapesImmediately() {
		JuliaFunction julia = new JuliaFunction(new Complex(2, 0), 1000);

		Natural iterations = julia.apply(new Complex(10, 0));

		assertEquals(new Natural(0), iterations);
	}

	@Test
	@DisplayName("c = 2, z0 = 1: dentro la soglia all'inizio ma diverge comunque ben prima del budget massimo")
	void orbitStartingInsideThresholdStillEscapesWellBeforeBudget() {
		JuliaFunction julia = new JuliaFunction(new Complex(2, 0), 1000);

		Natural iterations = julia.apply(new Complex(1, 0));

		assertTrue(iterations.getValue() < 1000);
		assertTrue(iterations.getValue() > 0);
	}
}
