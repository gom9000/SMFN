package net.gommagomma.smfn.demo;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialElementFactory;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.functionals.HornerEvaluator;
import net.gommagomma.smfn.math.analysis.core.problems.ScalarRootFindingProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.functions.LinearFunction;
import net.gommagomma.smfn.math.analysis.functions.PolynomialFunction;
import net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation.CentralDifferenceDifferentiator;
import net.gommagomma.smfn.math.analysis.numerical.solvers.roots.NewtonRaphsonSolver;

/**
 * Intersezione di due ScalarFunction<K>: f(x) = g(x), cioe' lo zero di
 * h(x) = f(x) - g(x). Un'incognita, un'equazione -- a differenza
 * dell'intersezione tra MultivariateFunction, non serve nessun sistema:
 * ScalarRootFindingProblem (gia' esistente) basta.
 */
public class ScalarFunctionIntersectionDemo
{
	private static final RealField R = RealField.INSTANCE;

	public static void main(String[] args) {
		// f(x) = 2x - 1  (retta)
		LinearFunction<Real> f = new LinearFunction<>(R, new Real(2.0), new Real(-1.0));

		// g(x) = x^2 - 3  (parabola)
		Polynomial<Real> gPoly = PolynomialElementFactory.of(R, new Real(-3.0), new Real(0.0), new Real(1.0));
		HornerEvaluator<Real, RealField, PolynomialFunction<Real, RealField>> horner = new HornerEvaluator<>(R);
		PolynomialFunction<Real, RealField> g = new PolynomialFunction<>(gPoly, R, horner);

		System.out.println("f(x) = 2x - 1");
		System.out.println("g(x) = x^2 - 3");
		System.out.println("Soluzioni analitiche attese: x = 1 +- sqrt(3) = "
			+ (1 + Math.sqrt(3)) + " oppure " + (1 - Math.sqrt(3)));

		// h(x) = f(x) - g(x): l'incognita e' una sola, non un sistema
		ScalarRootFindingProblem<Real> problem = x -> R.subtract(f.apply(x), g.apply(x));

		NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>(R, new CentralDifferenceDifferentiator<>(R, new Real(1e-6)));
		MetricSpace<Real> space = (a, b) -> R.subtract(a, b).abs();
		StoppingParameters params = new StoppingParameters(new Real(1e-10), 100);

		Real root1 = solver.solve(problem, new Real(3.0),
			(d, p, it) -> d.getValue() < p.getTolerance().getValue(), params, space).getValue();
		Real root2 = solver.solve(problem, new Real(-3.0),
			(d, p, it) -> d.getValue() < p.getTolerance().getValue(), params, space).getValue();

		System.out.println("\nIntersezione 1 (guess  3.0) = " + root1);
		System.out.println("Intersezione 2 (guess -3.0) = " + root2);

		System.out.println("\nVerifica: f(x) == g(x) in entrambi i punti:");
		System.out.println("  f(" + root1 + ") = " + f.apply(root1) + " , g(" + root1 + ") = " + g.apply(root1));
		System.out.println("  f(" + root2 + ") = " + f.apply(root2) + " , g(" + root2 + ") = " + g.apply(root2));
	}
}
