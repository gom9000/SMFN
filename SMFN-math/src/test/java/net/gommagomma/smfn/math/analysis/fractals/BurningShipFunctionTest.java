package net.gommagomma.smfn.math.analysis.fractals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;

@DisplayName("BurningShipFunction: stesso solutore EscapeTimeSolver, MetricSpace non piu' nullo")
class BurningShipFunctionTest
{
	@Test
	@DisplayName("c = 0: |0|+i|0| = 0, il punto fisso non diverge mai, il budget si esaurisce")
	void originNeverEscapesAndReturnsBudget() {
		BurningShipFunction burningShip = new BurningShipFunction(100);

		Natural iterations = burningShip.apply(new Complex(0, 0));

		assertEquals(new Natural(100), iterations);
	}

	@Test
	@DisplayName("c = 2: punto chiaramente esterno, l'orbita diverge ben prima del budget massimo")
	void exteriorPointEscapesWellBeforeBudget() {
		BurningShipFunction burningShip = new BurningShipFunction(1000);

		Natural iterations = burningShip.apply(new Complex(2, 0));

		assertTrue(iterations.getValue() < 1000);
		assertTrue(iterations.getValue() > 0);
	}
}
