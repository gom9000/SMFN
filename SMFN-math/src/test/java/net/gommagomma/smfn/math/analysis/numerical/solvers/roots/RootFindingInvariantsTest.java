package net.gommagomma.smfn.math.analysis.numerical.solvers.roots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.solvers.ResidualAware;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.TerminationStatus;
import net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation.CentralDifferenceJacobianEstimator;
import net.gommagomma.smfn.math.analysis.numerical.solvers.linear.GaussianEliminationSolver;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Invarianti dei solutori di ricerca degli zeri (NewtonRaphsonSolver, VectorNewtonRaphsonSolver)
 * sul catalogo di RootFindingTestValues, sullo stesso spirito delle *InvariantsTest di
 * algebra/linearalgebra. A differenza di quelle, qui l'invariante non e' una legge algebrica
 * fissa ma la convergenza stessa: per ogni caso, ci si aspetta che il solutore raggiunga
 * TerminationStatus.CONVERGED, che il valore trovato sia vicino alla radice attesa e che il
 * residuo finale sia prossimo a zero, entro il budget di iterazioni assegnato al caso.
 */
@DisplayName("NewtonRaphsonSolver / VectorNewtonRaphsonSolver: invarianti di convergenza sul catalogo di casi con soluzione nota")
class RootFindingInvariantsTest
{
	private static final RealField R = RealField.INSTANCE;
	private static final MetricSpace<Real> SCALAR_SPACE = (a, b) -> R.subtract(a, b).abs();
	private static final StoppingCriteria CRITERIA = (distance, p, it) -> distance.getValue() < p.getTolerance().getValue();

	@Test
	@DisplayName("NewtonRaphsonSolver converge alla radice attesa, con residuo quasi nullo, su tutto il catalogo scalare")
	void scalarCasesConvergeToExpectedRoot() {
		NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>(R, null);

		for (RootFindingTestValues.ScalarCase testCase : RootFindingTestValues.scalarCases()) {
			StoppingParameters params = new StoppingParameters(new Real(1e-10), testCase.maxIterations);

			SolverResult<Real> result = solver.solve(testCase.problem, testCase.initialGuess, CRITERIA, params, SCALAR_SPACE);

			assertEquals(TerminationStatus.CONVERGED, result.getStatus(), "caso: " + testCase.name);
			assertEquals(testCase.expectedRoot.getValue(), result.getValue().getValue(), 1e-6, "caso: " + testCase.name);
			assertTrue(result.getIterationsExecuted() > 0 && result.getIterationsExecuted() <= testCase.maxIterations, "caso: " + testCase.name);

			assertTrue(result instanceof ResidualAware, "caso: " + testCase.name);
			double residual = ((ResidualAware) result).getFinalResidual().getValue();
			assertTrue(residual < 1e-6, "caso: " + testCase.name + " residuo=" + residual);
		}
	}

	@Test
	@DisplayName("VectorNewtonRaphsonSolver converge alla soluzione attesa, con residuo quasi nullo, su tutto il catalogo vettoriale")
	void vectorCasesConvergeToExpectedSolution() {
		VectorSpace<Real, RealField> v2 = new VectorSpace<>(R, 2);
		GaussianEliminationSolver<Real, RealField> linearSolver = new GaussianEliminationSolver<>(R, 2);
		CentralDifferenceJacobianEstimator<Real, RealField> jacobianFallback = new CentralDifferenceJacobianEstimator<>(R, new Real(1e-6));
		VectorNewtonRaphsonSolver<Real, RealField> solver = new VectorNewtonRaphsonSolver<>(linearSolver, v2, jacobianFallback);

		MetricSpace<Vector<Real>> space = (a, b) -> {
			Real dx = R.subtract(a.get(0), b.get(0));
			Real dy = R.subtract(a.get(1), b.get(1));
			return R.add(R.multiply(dx, dx), R.multiply(dy, dy)).sqrt();
		};
		StoppingParameters params = new StoppingParameters(new Real(1e-10), 100);

		for (RootFindingTestValues.VectorCase testCase : RootFindingTestValues.vectorCases()) {
			SolverResult<Vector<Real>> result = solver.solve(testCase.problem, testCase.initialGuess, CRITERIA, params, space);

			assertEquals(TerminationStatus.CONVERGED, result.getStatus(), "caso: " + testCase.name);
			assertEquals(testCase.expectedSolution.get(0).getValue(), result.getValue().get(0).getValue(), 1e-6, "caso: " + testCase.name);
			assertEquals(testCase.expectedSolution.get(1).getValue(), result.getValue().get(1).getValue(), 1e-6, "caso: " + testCase.name);
			assertTrue(result.getIterationsExecuted() > 0 && result.getIterationsExecuted() <= params.maxIterations, "caso: " + testCase.name);

			assertTrue(result instanceof ResidualAware, "caso: " + testCase.name);
			double residual = ((ResidualAware) result).getFinalResidual().getValue();
			assertTrue(residual < 1e-6, "caso: " + testCase.name + " residuo=" + residual);
		}
	}

	@Test
	@DisplayName("Struttura del SolverResult coerente: MAX_ITERATIONS_REACHED se il budget e' insufficiente, invece di CONVERGED spurio")
	void insufficientBudgetIsReportedHonestly() {
		NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>(R, null);
		// Stesso problema del primo caso del catalogo (x^2 - 2), ma con budget di una sola iterazione:
		// non basta a raggiungere la tolleranza richiesta partendo da x0=1.5.
		RootFindingTestValues.ScalarCase reference = RootFindingTestValues.scalarCases().get(0);
		StoppingParameters starvedParams = new StoppingParameters(new Real(1e-10), 1);

		SolverResult<Real> result = solver.solve(reference.problem, reference.initialGuess, CRITERIA, starvedParams, SCALAR_SPACE);

		assertEquals(TerminationStatus.MAX_ITERATIONS_REACHED, result.getStatus());
		assertEquals(1, result.getIterationsExecuted());
	}
}
