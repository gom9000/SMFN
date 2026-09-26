package net.gommagomma.smfn.demo;

import java.util.List;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialElementFactory;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.roots.PolynomialRootSolver;

/**
 * Trova tutte le radici (reali o complesse) di un polinomio, usando
 * PolynomialRootSolver (Newton-Raphson + deflazione, gia' formalizzato
 * come classe riusabile, non piu' ripetuto qui).
 */
public class PolynomialRootSolverDemo
{
	private static final ComplexField C = ComplexField.INSTANCE;

	public static void main(String[] args) {
		MetricSpace<Complex> space = (a, b) -> new Real(C.subtract(a, b).modulus());

		// Punto di partenza non reale, non nullo: aiuta Newton a trovare
		// radici complesse anche partendo da coefficienti reali.
		Complex initialGuess = new Complex(0.4, 0.9);

		PolynomialRootSolver<Complex, ComplexField> rootSolver = new PolynomialRootSolver<>(
			C, new Complex(1e-6, 0), space, initialGuess, new StoppingParameters(new Real(1e-10), 100));

		System.out.println("--- Caso 1: (x-1)(x-2)(x-3) = x^3 - 6x^2 + 11x - 6, tre radici reali note ---");
		Polynomial<Complex> p1 = PolynomialElementFactory.of(C,
			new Complex(-6, 0), new Complex(11, 0), new Complex(-6, 0), new Complex(1, 0));
		printRoots(rootSolver.findAllRoots(p1));

		System.out.println("\n--- Caso 2: x^2 + 1, radici complesse note (i, -i) ---");
		Polynomial<Complex> p2 = PolynomialElementFactory.of(C, new Complex(1, 0), new Complex(0, 0), new Complex(1, 0));
		printRoots(rootSolver.findAllRoots(p2));
	}

	private static void printRoots(List<Complex> roots) {
		for (int i = 0; i < roots.size(); i++) {
			System.out.println("  radice " + (i + 1) + " = " + roots.get(i));
		}
	}
}
