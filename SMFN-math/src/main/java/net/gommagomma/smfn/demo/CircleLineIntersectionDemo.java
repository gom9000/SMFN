package net.gommagomma.smfn.demo;

import java.util.List;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.functions.MultivariateFunction;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.numerical.solvers.roots.VectorNewtonRaphsonSolver;
import net.gommagomma.smfn.math.geometry.Circle;
import net.gommagomma.smfn.math.geometry.Line;
import net.gommagomma.smfn.math.geometry.Point;
import net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation.CentralDifferenceJacobianEstimator;
import net.gommagomma.smfn.math.analysis.numerical.problems.MultivariateFunctionSystemProblem;
import net.gommagomma.smfn.math.analysis.numerical.solvers.linear.GaussianEliminationSolver;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Intersezione cerchio-retta come ricerca di radici vettoriale:
 * F(x,y) = (dist(P,centro) - raggio, distanza con segno di P dalla retta) = (0,0).
 *
 * A differenza della prima versione di questa demo, qui nessuna Jacobiana
 * e' scritta a mano: MultivariateFunctionSystemProblem non sa nulla di "cerchio"
 * o "retta" -- usa il gradiente esatto di ciascuna figura (getGradient(),
 * gia' formalizzato in Circle/Line) tramite lo stesso schema instanceof
 * gia' visto altrove nella libreria.
 */
public class CircleLineIntersectionDemo
{
	private static final RealField R = RealField.INSTANCE;
	private static final VectorSpace<Real, RealField> V2 = new VectorSpace<>(R, 2);

	public static void main(String[] args) {
		Circle circle = new Circle(new Point(0.0, 0.0), new Real(3.0));
		Line line = Line.through(new Point(0.0, 4.0), new Point(4.0, 0.0)); // x + y = 4

		System.out.println("Cerchio: " + circle);
		System.out.println("Retta:   " + line);
		System.out.println("\nSoluzioni analitiche attese: x = " + (2 + Math.sqrt(2) / 2) + " oppure x = " + (2 - Math.sqrt(2) / 2) + "  (y = 4 - x)");

		MultivariateFunctionSystemProblem<Real, RealField> problem =
			new MultivariateFunctionSystemProblem<>(List.of((MultivariateFunction<Real>) circle, line), R, new Real(1e-6));

		GaussianEliminationSolver<Real, RealField> linearSolver = new GaussianEliminationSolver<>(R, 2);
		CentralDifferenceJacobianEstimator<Real, RealField> jacobianFallback = new CentralDifferenceJacobianEstimator<>(R, new Real(1e-6));
		VectorNewtonRaphsonSolver<Real, RealField> solver = new VectorNewtonRaphsonSolver<>(linearSolver, V2, jacobianFallback);

		MetricSpace<Vector<Real>> space = (a, b) -> {
			Real dx = R.subtract(a.get(0), b.get(0));
			Real dy = R.subtract(a.get(1), b.get(1));
			return R.add(R.multiply(dx, dx), R.multiply(dy, dy)).sqrt();
		};
		StoppingParameters params = new StoppingParameters(new Real(1e-10), 100);

		// Due punti di partenza distinti: a differenza della deflazione polinomiale,
		// Newton multidimensionale non ha un modo sistematico per trovare "tutte"
		// le soluzioni -- ognuna richiede un proprio punto di partenza.
		Vector<Real> guess1 = V2.of(new Real[] { new Real(3.0), new Real(1.0) });
		Vector<Real> intersection1 = solver.solve(problem, guess1,
			(distance, p, it) -> distance.getValue() < p.getTolerance().getValue(), params, space).getValue();
		System.out.println("\nPartendo da (3,1): intersezione = " + intersection1);

		Vector<Real> guess2 = V2.of(new Real[] { new Real(1.0), new Real(3.0) });
		Vector<Real> intersection2 = solver.solve(problem, guess2,
			(distance, p, it) -> distance.getValue() < p.getTolerance().getValue(), params, space).getValue();
		System.out.println("Partendo da (1,3): intersezione = " + intersection2);

		System.out.println("\nVerifica isOnEntity su entrambe le figure:");
		System.out.println("  Intersezione 1 -> cerchio: " + circle.isOnEntity(intersection1) + ", retta: " + line.isOnEntity(intersection1));
		System.out.println("  Intersezione 2 -> cerchio: " + circle.isOnEntity(intersection2) + ", retta: " + line.isOnEntity(intersection2));
	}
}
