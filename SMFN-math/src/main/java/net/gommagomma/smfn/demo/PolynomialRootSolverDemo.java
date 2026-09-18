package net.gommagomma.smfn.demo;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.EuclideanPolynomialRing;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialDifferentiationProvider;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialElementFactory;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.functionals.HornerEvaluator;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableScalarProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation.CentralDifferenceDifferentiator;
import net.gommagomma.smfn.math.analysis.numerical.solvers.roots.NewtonRaphsonSolver;
import net.gommagomma.smfn.math.analysis.functions.PolynomialFunction;

/**
 * Trova tutte le radici (reali o complesse) di un polinomio, per
 * Newton-Raphson + deflazione: trova una radice, dividi il polinomio per
 * (x - radice) con la divisione euclidea esatta gia' costruita, ripeti sul
 * quoziente.
 */
public class PolynomialRootSolverDemo
{
	private static final ComplexField C = ComplexField.INSTANCE;
	private static final EuclideanPolynomialRing<Complex, ComplexField> RING = new EuclideanPolynomialRing<>(C);
	private static final PolynomialDifferentiationProvider<Complex, ComplexField> DIFF = new PolynomialDifferentiationProvider<>(C);
	private static final HornerEvaluator<Complex, ComplexField, PolynomialFunction<Complex, ComplexField>> HORNER = new HornerEvaluator<>(C);
	private static final MetricSpace<Complex> SPACE = (a, b) -> new Real(C.subtract(a, b).modulus());
	private static final NewtonRaphsonSolver<Complex> SOLVER =
		new NewtonRaphsonSolver<>(C, new CentralDifferenceDifferentiator<>(C, new Complex(1e-6, 0)));

	public static void main(String[] args) {
		System.out.println("--- Caso 1: (x-1)(x-2)(x-3) = x^3 - 6x^2 + 11x - 6, tre radici reali note ---");
		Polynomial<Complex> p1 = PolynomialElementFactory.of(C,
			new Complex(-6, 0), new Complex(11, 0), new Complex(-6, 0), new Complex(1, 0));
		printRoots(findAllRoots(p1));

		System.out.println("\n--- Caso 2: x^2 + 1, radici complesse note (i, -i) ---");
		Polynomial<Complex> p2 = PolynomialElementFactory.of(C, new Complex(1, 0), new Complex(0, 0), new Complex(1, 0));
		printRoots(findAllRoots(p2));
	}

	private static List<Complex> findAllRoots(Polynomial<Complex> polynomial) {
		List<Complex> roots = new ArrayList<>();
		Polynomial<Complex> current = polynomial;

		// Punto di partenza non reale, non nullo: aiuta Newton a trovare
		// radici complesse anche partendo da coefficienti reali.
		Complex guess = new Complex(0.4, 0.9);

		while (current.degree() > 0) {
			Complex root = SOLVER.solve(
				problemFor(current),
				guess,
				(distance, params, it) -> distance.getValue() < params.getTolerance().getValue(),
				new ConvergenceParameters(new Real(1e-10), 100),
				SPACE
			);
			roots.add(root);

			// Deflazione: divide per (x - root), usando la divisione euclidea esatta.
			Polynomial<Complex> divisor = PolynomialElementFactory.of(C, C.negate(root), C.one());
			current = RING.divide(current, divisor).quotient();
		}

		return roots;
	}

	private static DifferentiableScalarProblem<Complex> problemFor(Polynomial<Complex> polynomial) {
		PolynomialFunction<Complex, ComplexField> f = new PolynomialFunction<>(polynomial, C, HORNER);
		Polynomial<Complex> derivativePolynomial = DIFF.derivative(polynomial);
		PolynomialFunction<Complex, ComplexField> fPrime = new PolynomialFunction<>(derivativePolynomial, C, HORNER);

		return new DifferentiableScalarProblem<Complex>() {
			@Override public Complex apply(Complex x) { return f.apply(x); }
			@Override public Mapping<Complex, Complex> getDerivative() { return fPrime; }
		};
	}

	private static void printRoots(List<Complex> roots) {
		for (int i = 0; i < roots.size(); i++) {
			System.out.println("  radice " + (i + 1) + " = " + roots.get(i));
		}
	}
}
