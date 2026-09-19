package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.functions.MultivariateFunction;
import net.gommagomma.smfn.math.analysis.core.problems.MultivariateFunctionSystemProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.geometry.Circle;
import net.gommagomma.smfn.math.geometry.Line;
import net.gommagomma.smfn.math.geometry.Point;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixAlgebra;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

@DisplayName("VectorNewtonRaphsonSolver: Newton multidimensionale via Jacobiana")
class VectorNewtonRaphsonSolverTest
{
	private static final RealField R = RealField.INSTANCE;
	private final VectorSpace<Real, RealField> V2 = new VectorSpace<>(R, 2);
	private final SquareMatrixAlgebra<Real, RealField> matrixAlgebra = new SquareMatrixAlgebra<>(R, 2);
	private final VectorNewtonRaphsonSolver<Real, RealField> solver = new VectorNewtonRaphsonSolver<>(matrixAlgebra, V2);

	private final MetricSpace<Vector<Real>> space = (a, b) -> {
		Real dx = R.subtract(a.get(0), b.get(0));
		Real dy = R.subtract(a.get(1), b.get(1));
		return R.add(R.multiply(dx, dx), R.multiply(dy, dy)).sqrt();
	};
	private final ConvergenceParameters params = new ConvergenceParameters(new Real(1e-10), 100);

	private Vector<Real> guess(double x, double y) {
		return V2.of(new Real[] { new Real(x), new Real(y) });
	}

	@Test
	@DisplayName("Intersezione cerchio-retta: converge alla soluzione analitica esatta")
	void findsCircleLineIntersection() {
		Circle circle = new Circle(new Point(0.0, 0.0), new Real(3.0));
		Line line = Line.through(new Point(0.0, 4.0), new Point(4.0, 0.0)); // x + y = 4

		MultivariateFunctionSystemProblem<Real, RealField> problem =
			new MultivariateFunctionSystemProblem<>(List.of((MultivariateFunction<Real>) circle, line), R, new Real(1e-6));

		// Soluzione analitica nota: x = 2 +- sqrt(2)/2
		double expectedX1 = 2 + Math.sqrt(2) / 2;
		double expectedX2 = 2 - Math.sqrt(2) / 2;

		Vector<Real> intersection1 = solver.solve(problem, guess(3.0, 1.0),
			(d, p, it) -> d.getValue() < p.getTolerance().getValue(), params, space);
		Vector<Real> intersection2 = solver.solve(problem, guess(1.0, 3.0),
			(d, p, it) -> d.getValue() < p.getTolerance().getValue(), params, space);

		assertEquals(expectedX1, intersection1.get(0).getValue(), 1e-8);
		assertEquals(expectedX2, intersection2.get(0).getValue(), 1e-8);

		// Entrambe le soluzioni devono stare davvero su entrambe le figure
		assertEquals(true, circle.isOnEntity(intersection1));
		assertEquals(true, line.isOnEntity(intersection1));
	}

	@Test
	@DisplayName("Jacobiana singolare: SquareMatrixAlgebra.inverse() lancia, il solver non restituisce un risultato scorretto")
	void throwsWhenJacobianIsSingular() {
		// Due equazioni identiche: f1(x,y) = f2(x,y) = x - 1. Jacobiana costante
		// [[1,0],[1,0]], singolare ovunque -- non esiste un'unica soluzione isolata.
		MultivariateFunction<Real> f = v -> R.subtract(v.get(0), R.one());

		MultivariateFunctionSystemProblem<Real, RealField> problem =
			new MultivariateFunctionSystemProblem<>(List.of(f, f), R, new Real(1e-6));

		assertThrows(ArithmeticException.class, () ->
			solver.solve(problem, guess(0.0, 0.0),
				(d, p, it) -> d.getValue() < p.getTolerance().getValue(), params, space));
	}
}
