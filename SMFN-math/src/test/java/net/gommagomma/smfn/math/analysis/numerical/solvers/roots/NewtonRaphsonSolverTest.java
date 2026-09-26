package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableScalarProblem;
import net.gommagomma.smfn.math.analysis.core.problems.ScalarRootFindingProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceStatus;
import net.gommagomma.smfn.math.analysis.core.solvers.ResidualAware;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;

@DisplayName("NewtonRaphsonSolver: esiti riportati come SolverResult, non piu' come eccezioni")
class NewtonRaphsonSolverTest
{
	private static final RealField R = RealField.INSTANCE;
	private static final MetricSpace<Real> SPACE = (a, b) -> R.subtract(a, b).abs();

	/** f(x) = x^3 - 2, derivata analitica f'(x) = 3x^2. Radice reale attesa: cbrt(2) ~ 1.2599. */
	private static final DifferentiableScalarProblem<Real> CUBIC_MINUS_TWO = new DifferentiableScalarProblem<Real>() {
		@Override
		public Real apply(Real x) {
			return R.subtract(R.multiply(R.multiply(x, x), x), R.of(2.0));
		}

		@Override
		public Mapping<Real, Real> getDerivative() {
			return x -> R.multiply(R.of(3.0), R.multiply(x, x));
		}
	};

	/** f(x) = x^2, derivata f'(x) = 2x: nulla esattamente in x = 0. */
	private static final DifferentiableScalarProblem<Real> SQUARE = new DifferentiableScalarProblem<Real>() {
		@Override
		public Real apply(Real x) {
			return R.multiply(x, x);
		}

		@Override
		public Mapping<Real, Real> getDerivative() {
			return x -> R.multiply(R.of(2.0), x);
		}
	};

	@Test
	@DisplayName("Convergenza: status CONVERGED, valore vicino alla radice attesa, residuo quasi nullo")
	void convergesToKnownRoot() {
		NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>(R, null);
		ConvergenceParameters params = new ConvergenceParameters(new Real(1e-10), 50);

		SolverResult<Real> result = solver.solve(CUBIC_MINUS_TWO, new Real(1.0),
			(distance, p, it) -> distance.getValue() < p.getTolerance().getValue(), params, SPACE);

		assertEquals(ConvergenceStatus.CONVERGED, result.getStatus());
		assertEquals(Math.cbrt(2.0), result.getValue().getValue(), 1e-8);
		assertTrue(result.getIterationsExecuted() > 0);

		assertTrue(result instanceof ResidualAware, "un solutore di ricerca degli zeri deve esporre il residuo");
		double residual = ((ResidualAware) result).getFinalResidual().getValue();
		assertTrue(residual < 1e-6, "residuo finale " + residual + " dovrebbe essere prossimo a zero");
	}

	@Test
	@DisplayName("Derivata nulla: status NUMERICAL_ERROR invece di ArithmeticException, nessuna iterazione completata")
	void zeroDerivativeYieldsNumericalErrorInsteadOfThrowing() {
		NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>(R, null);
		ConvergenceParameters params = new ConvergenceParameters(new Real(1e-10), 50);

		// x_0 = 0: f'(0) = 0, la derivata e' nulla gia' al primo tentativo.
		SolverResult<Real> result = solver.solve(SQUARE, new Real(0.0),
			(distance, p, it) -> distance.getValue() < p.getTolerance().getValue(), params, SPACE);

		assertEquals(ConvergenceStatus.NUMERICAL_ERROR, result.getStatus());
		assertEquals(0, result.getIterationsExecuted());
		assertEquals(0.0, result.getValue().getValue(), 1e-15);
	}

	@Test
	@DisplayName("Budget esaurito: status MAX_ITERATIONS_REACHED con l'ultima approssimazione disponibile, non un'eccezione")
	void insufficientBudgetYieldsMaxIterationsReached() {
		NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>(R, null);
		// Tolleranza irraggiungibile in una sola iterazione partendo lontano dalla radice.
		ConvergenceParameters params = new ConvergenceParameters(new Real(1e-12), 1);

		SolverResult<Real> result = solver.solve(CUBIC_MINUS_TWO, new Real(100.0),
			(distance, p, it) -> distance.getValue() < p.getTolerance().getValue(), params, SPACE);

		assertEquals(ConvergenceStatus.MAX_ITERATIONS_REACHED, result.getStatus());
		assertEquals(1, result.getIterationsExecuted());
	}
}
