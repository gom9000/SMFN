package net.gommagomma.smfn.math.analysis.fractals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableScalarProblem;
import net.gommagomma.smfn.math.analysis.numerical.solvers.roots.NewtonRaphsonSolver;

/**
 * Invarianti delle funzioni frattali (Mandelbrot, Julia, Burning Ship, Newton) sul catalogo di
 * FractalTestValues.
 * <p>
 * A differenza dei cataloghi di root-finding/ODE, qui non si verifica la convergenza a un valore
 * noto ma proprieta' matematicamente inequivocabili: punti notoriamente interni/esterni
 * all'insieme di Mandelbrot (che restano tali per qualunque budget ragionevole di iterazioni), la
 * simmetria dell'insieme rispetto all'asse reale (c e il suo coniugato hanno lo stesso tempo di
 * fuga, perche' l'orbita del coniugato e' il coniugato dell'orbita), e un'identita' algebrica
 * fra Burning Ship e Mandelbrot quando la costante e' reale (per c e z reali, |Re(z)|^2 == z^2,
 * quindi le due ricorrenze coincidono esattamente).
 */
@DisplayName("Fractals (Mandelbrot/Julia/BurningShip/Newton): invarianti sul catalogo di punti con appartenenza inequivocabile")
class FractalInvariantsTest
{
	private static final int MAX_ITERATIONS = 200;
	private static final ComplexField C = ComplexField.INSTANCE;

	@Test
	@DisplayName("Punti notoriamente interni all'insieme di Mandelbrot non fuggono mai entro il budget: iterazioni == budget")
	void interiorPointsNeverEscape() {
		MandelbrotFunction mandelbrot = new MandelbrotFunction(MAX_ITERATIONS);
		for (Complex c : FractalTestValues.mandelbrotInteriorPoints()) {
			Natural iterations = mandelbrot.apply(c);
			assertEquals(MAX_ITERATIONS, iterations.getValue(), "c=" + c + " dovrebbe restare limitato per tutto il budget");
		}
	}

	@Test
	@DisplayName("Punti notoriamente esterni all'insieme di Mandelbrot fuggono rapidamente (ben prima di esaurire il budget)")
	void exteriorPointsEscapeQuickly() {
		MandelbrotFunction mandelbrot = new MandelbrotFunction(MAX_ITERATIONS);
		for (Complex c : FractalTestValues.mandelbrotExteriorPoints()) {
			Natural iterations = mandelbrot.apply(c);
			assertTrue(iterations.getValue() < MAX_ITERATIONS, "c=" + c + " dovrebbe fuggire, non esaurire il budget");
			assertTrue(iterations.getValue() < 10, "c=" + c + " ha |c|>2: dovrebbe fuggire in pochissime iterazioni, non " + iterations.getValue());
		}
	}

	@Test
	@DisplayName("Simmetria di Mandelbrot rispetto all'asse reale: tempo di fuga di c e del suo coniugato coincidono")
	void mandelbrotIsSymmetricAboutRealAxis() {
		MandelbrotFunction mandelbrot = new MandelbrotFunction(MAX_ITERATIONS);
		for (Complex c : concatAll()) {
			Natural direct = mandelbrot.apply(c);
			Natural conjugated = mandelbrot.apply(new Complex(c.getRe(), -c.getIm()));
			assertEquals(direct.getValue(), conjugated.getValue(), "c=" + c + " e il suo coniugato dovrebbero avere lo stesso tempo di fuga");
		}
	}

	@Test
	@DisplayName("Simmetria di Julia (costante reale): il tempo di fuga di z0 e del suo coniugato coincidono")
	void juliaWithRealConstantIsSymmetricAboutRealAxis() {
		for (Complex constant : FractalTestValues.realJuliaConstants()) {
			JuliaFunction julia = new JuliaFunction(constant, MAX_ITERATIONS);
			for (Complex z0 : FractalTestValues.genericPointsForSymmetryCheck()) {
				Natural direct = julia.apply(z0);
				Natural conjugated = julia.apply(new Complex(z0.getRe(), -z0.getIm()));
				assertEquals(direct.getValue(), conjugated.getValue(),
					"costante=" + constant + " z0=" + z0 + " e il suo coniugato dovrebbero avere lo stesso tempo di fuga");
			}
		}
	}

	@Test
	@DisplayName("Burning Ship coincide esattamente con Mandelbrot quando la costante c e' reale (identita' algebrica: |Re(z)|^2 == z^2 per z reale)")
	void burningShipMatchesMandelbrotForRealConstants() {
		MandelbrotFunction mandelbrot = new MandelbrotFunction(MAX_ITERATIONS);
		BurningShipFunction burningShip = new BurningShipFunction(MAX_ITERATIONS);

		for (double reC : new double[] { 0.0, -1.0, -0.5, 0.2, 3.0, -3.0 }) {
			Complex c = new Complex(reC, 0.0);
			assertEquals(mandelbrot.apply(c).getValue(), burningShip.apply(c).getValue(),
				"per c reale (c=" + c + ") Burning Ship e Mandelbrot devono coincidere esattamente");
		}
	}

	@Test
	@DisplayName("Il frattale di Newton su z^3-1 converge in pochissime iterazioni quando si parte esattamente su una radice nota")
	void newtonFractalConvergesImmediatelyAtKnownRoots() {
		NewtonFractalFunction newtonFractal = NewtonFractalFunction.forCubicMinusOne(MAX_ITERATIONS);

		Complex one = new Complex(1.0, 0.0);
		Complex omega = new Complex(-0.5, Math.sqrt(3.0) / 2.0);
		Complex omegaSquared = new Complex(-0.5, -Math.sqrt(3.0) / 2.0);

		for (Complex root : new Complex[] { one, omega, omegaSquared }) {
			Natural iterations = newtonFractal.apply(root);
			assertTrue(iterations.getValue() <= 2, "radice=" + root + ": dovrebbe convergere quasi immediatamente, non in " + iterations.getValue() + " iterazioni");
		}
	}

	@Test
	@DisplayName("Le radici del frattale di Newton su z^3-1 coincidono con quelle trovate da NewtonRaphsonSolver usato direttamente")
	void newtonFractalUsesTheSameConvergenceAsDirectSolver() {
		DifferentiableScalarProblem<Complex> cubicMinusOne = new DifferentiableScalarProblem<Complex>() {
			@Override public Complex apply(Complex z) { return C.subtract(C.multiply(C.multiply(z, z), z), C.one()); }
			@Override public Mapping<Complex, Complex> getDerivative() { return z -> C.multiply(new Complex(3.0, 0.0), C.multiply(z, z)); }
		};
		NewtonRaphsonSolver<Complex> solver = new NewtonRaphsonSolver<>(C, null);
		NewtonFractalFunction newtonFractal = new NewtonFractalFunction(cubicMinusOne, MAX_ITERATIONS);

		for (Complex z0 : FractalTestValues.genericPointsForSymmetryCheck()) {
			var directResult = solver.solve(cubicMinusOne, z0,
				(distance, p, it) -> distance.getValue() < p.getTolerance().getValue(),
				new net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters(new net.gommagomma.smfn.math.algebra.numerics.Real(1e-6), MAX_ITERATIONS),
				C);

			Natural viaFractalFunction = newtonFractal.apply(z0);
			assertEquals(directResult.getIterationsExecuted(), (int) viaFractalFunction.getValue(), "z0=" + z0);
		}
	}

	private static java.util.List<Complex> concatAll() {
		java.util.List<Complex> all = new java.util.ArrayList<>();
		all.addAll(FractalTestValues.mandelbrotInteriorPoints());
		all.addAll(FractalTestValues.mandelbrotExteriorPoints());
		all.addAll(FractalTestValues.genericPointsForSymmetryCheck());
		return all;
	}
}
