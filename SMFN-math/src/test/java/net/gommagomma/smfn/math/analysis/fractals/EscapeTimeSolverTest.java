package net.gommagomma.smfn.math.analysis.fractals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.metric.MetricSpace;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.FixedPointProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceCriteria;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceStatus;
import net.gommagomma.smfn.math.analysis.core.solvers.ResidualAware;
import net.gommagomma.smfn.math.analysis.core.solvers.SolverResult;

@DisplayName("EscapeTimeSolver: R torna a essere lo stato finale, il conteggio si legge da SolverResult")
class EscapeTimeSolverTest
{
	private static final ComplexField C = ComplexField.INSTANCE;
	private static final RealField R = RealField.INSTANCE;
	private static final double DIVERGENCE_RADIUS_SQ = 4.0;
	private static final MetricSpace<Complex> SPACE = (z1, z2) -> R.of(Math.sqrt(C.subtract(z1, z2).modulusSquared()));
	private static final ConvergenceCriteria DIVERGENCE_TEST = (measure, params, iteration) -> measure.getValue() > DIVERGENCE_RADIUS_SQ;

	private final EscapeTimeSolver solver = new EscapeTimeSolver();

	@Test
	@DisplayName("c = 2: l'orbita 0 -> 2 -> 6 supera la soglia alla terza valutazione (iterazione 2)")
	void detectsDivergenceAndReportsExactIterationCount() {
		FixedPointProblem<Complex> mandelbrotAtC2 = z -> C.add(C.multiply(z, z), new Complex(2, 0));
		ConvergenceParameters params = new ConvergenceParameters(R.one(), 1000);

		SolverResult<Complex> result = solver.solve(mandelbrotAtC2, C.zero(), DIVERGENCE_TEST, params, SPACE);

		assertEquals(ConvergenceStatus.DIVERGED, result.getStatus());
		assertEquals(2, result.getIterationsExecuted());
		assertEquals(new Complex(6, 0), result.getValue());
		assertEquals(4.0, result.getFinalStepDistance().getValue(), 1e-9); // d(6, 2) = 4
	}

	@Test
	@DisplayName("c = 0: il punto fisso z = 0 non diverge mai, il budget si esaurisce con MAX_ITERATIONS_REACHED")
	void neverDivergingOrbitExhaustsBudget() {
		FixedPointProblem<Complex> mandelbrotAtOrigin = z -> C.add(C.multiply(z, z), C.zero());
		ConvergenceParameters params = new ConvergenceParameters(R.one(), 50);

		SolverResult<Complex> result = solver.solve(mandelbrotAtOrigin, C.zero(), DIVERGENCE_TEST, params, SPACE);

		assertEquals(ConvergenceStatus.MAX_ITERATIONS_REACHED, result.getStatus());
		assertEquals(50, result.getIterationsExecuted());
		assertEquals(C.zero(), result.getValue());
		assertEquals(0.0, result.getFinalStepDistance().getValue(), 1e-15); // l'orbita non si muove mai
	}

	@Test
	@DisplayName("Un problema di punto fisso non ha un'equazione associata: EscapeTimeSolver non e' ResidualAware")
	void isNotResidualAware() {
		FixedPointProblem<Complex> anyProblem = z -> z;
		ConvergenceParameters params = new ConvergenceParameters(R.one(), 10);

		SolverResult<Complex> result = solver.solve(anyProblem, C.zero(), DIVERGENCE_TEST, params, SPACE);

		assertFalse(result instanceof ResidualAware);
	}
}
