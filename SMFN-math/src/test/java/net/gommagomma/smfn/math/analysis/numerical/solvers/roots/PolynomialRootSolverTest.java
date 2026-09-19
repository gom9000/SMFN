package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialElementFactory;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;

@DisplayName("PolynomialRootSolver: tutte le radici via Newton-Raphson + deflazione")
class PolynomialRootSolverTest
{
	private static final ComplexField C = ComplexField.INSTANCE;
	private static final MetricSpace<Complex> SPACE = (a, b) -> new Real(C.subtract(a, b).modulus());

	private PolynomialRootSolver<Complex, ComplexField> newSolver() {
		return new PolynomialRootSolver<>(
			C, new Complex(1e-6, 0), SPACE, new Complex(0.4, 0.9),
			new ConvergenceParameters(new Real(1e-10), 100));
	}

	@Test
	@DisplayName("(x-1)(x-2)(x-3): tre radici reali note, tutte trovate")
	void findsAllRealRootsOfCubic() {
		Polynomial<Complex> p = PolynomialElementFactory.of(C,
			new Complex(-6, 0), new Complex(11, 0), new Complex(-6, 0), new Complex(1, 0));

		List<Complex> roots = newSolver().findAllRoots(p);

		assertEquals(3, roots.size());
		assertContainsCloseTo(roots, new Complex(1, 0));
		assertContainsCloseTo(roots, new Complex(2, 0));
		assertContainsCloseTo(roots, new Complex(3, 0));
	}

	@Test
	@DisplayName("x^2 + 1: radici complesse note (i, -i), trovate partendo da coefficienti reali")
	void findsComplexRootsFromRealCoefficients() {
		Polynomial<Complex> p = PolynomialElementFactory.of(C, new Complex(1, 0), new Complex(0, 0), new Complex(1, 0));

		List<Complex> roots = newSolver().findAllRoots(p);

		assertEquals(2, roots.size());
		assertContainsCloseTo(roots, new Complex(0, 1));
		assertContainsCloseTo(roots, new Complex(0, -1));
	}

	@Test
	@DisplayName("Ogni radice trovata soddisfa davvero P(radice) = 0, verificato per valutazione diretta")
	void everyRootActuallySatisfiesThePolynomial() {
		// P(x) = x^2 - 5x + 6 = (x-2)(x-3)
		Polynomial<Complex> p = PolynomialElementFactory.of(C, new Complex(6, 0), new Complex(-5, 0), new Complex(1, 0));

		List<Complex> roots = newSolver().findAllRoots(p);

		for (Complex root : roots) {
			Complex value = evaluate(p, root);
			assertTrue(value.modulus() < 1e-6, "P(" + root + ") = " + value + " dovrebbe essere ~0");
		}
	}

	private Complex evaluate(Polynomial<Complex> p, Complex x) {
		Complex result = C.zero();
		for (int i = p.degree(); i >= 0; i--) {
			result = C.add(C.multiply(result, x), p.getCoefficient(i));
		}
		return result;
	}

	private void assertContainsCloseTo(List<Complex> roots, Complex expected) {
		for (Complex root : roots) {
			if (C.subtract(root, expected).modulus() < 1e-6) return;
		}
		throw new AssertionError("Nessuna radice trovata vicina a " + expected + " in " + roots);
	}
}
