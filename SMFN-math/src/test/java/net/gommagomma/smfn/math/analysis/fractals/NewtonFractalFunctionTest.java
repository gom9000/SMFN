package net.gommagomma.smfn.math.analysis.fractals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;

@DisplayName("NewtonFractalFunction: il conteggio delle iterazioni si legge da SolverResult, non e' piu' sempre il budget massimo")
class NewtonFractalFunctionTest
{
	@Test
	@DisplayName("z0 = 1: e' gia' esattamente una radice di z^3 - 1, converge in una sola iterazione")
	void startingExactlyAtARootConvergesImmediately() {
		NewtonFractalFunction newtonFractal = NewtonFractalFunction.forCubicMinusOne(50);

		Natural iterations = newtonFractal.apply(new Complex(1, 0));

		assertEquals(new Natural(1), iterations);
	}

	@Test
	@DisplayName("z0 = 1.5: converge alla radice reale in poche iterazioni, ben prima del budget massimo")
	void startingNearARootConvergesWellBeforeBudget() {
		NewtonFractalFunction newtonFractal = NewtonFractalFunction.forCubicMinusOne(50);

		Natural iterations = newtonFractal.apply(new Complex(1.5, 0));

		assertTrue(iterations.getValue() > 0);
		assertTrue(iterations.getValue() < 50, "la convergenza quadratica di Newton non dovrebbe richiedere l'intero budget");
	}

	@Test
	@DisplayName("Prima della correzione, apply() restituiva sempre il budget massimo qualunque fosse l'esito: non e' piu' cosi'")
	void doesNotAlwaysReturnTheMaximumBudget() {
		NewtonFractalFunction newtonFractal = NewtonFractalFunction.forCubicMinusOne(50);

		Natural fastConvergence = newtonFractal.apply(new Complex(1, 0));
		Natural slowerConvergence = newtonFractal.apply(new Complex(1.5, 0));

		assertTrue(fastConvergence.getValue() < slowerConvergence.getValue()
			|| fastConvergence.getValue() < 50, "il conteggio deve riflettere la reale storia di convergenza, non un valore costante");
	}
}
