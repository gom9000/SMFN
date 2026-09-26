package net.gommagomma.smfn.math.analysis.core.solvers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;

@DisplayName("SolverResult: BasicSolverResult e RootFindingSolverResult")
class SolverResultTest
{
	@Test
	@DisplayName("BasicSolverResult espone esattamente i campi universali passati al costruttore")
	void basicSolverResultExposesCoreFields() {
		SolverResult<String> result = new BasicSolverResult<>("valore", ConvergenceStatus.CONVERGED, 7, new Real(0.001));

		assertEquals("valore", result.getValue());
		assertEquals(ConvergenceStatus.CONVERGED, result.getStatus());
		assertEquals(7, result.getIterationsExecuted());
		assertEquals(0.001, result.getFinalStepDistance().getValue(), 1e-12);
	}

	@Test
	@DisplayName("BasicSolverResult non e' ResidualAware: il residuo non e' definito per un problema di punto fisso")
	void basicSolverResultIsNotResidualAware() {
		SolverResult<String> result = new BasicSolverResult<>("valore", ConvergenceStatus.MAX_ITERATIONS_REACHED, 3, new Real(0.0));

		assertFalse(result instanceof ResidualAware);
	}

	@Test
	@DisplayName("BasicSolverResult rifiuta un numero di iterazioni negativo")
	void basicSolverResultRejectsNegativeIterations() {
		assertThrows(IllegalArgumentException.class,
			() -> new BasicSolverResult<>("valore", ConvergenceStatus.CONVERGED, -1, new Real(0.0)));
	}

	@Test
	@DisplayName("BasicSolverResult rifiuta value, status o finalStepDistance nulli")
	void basicSolverResultRejectsNullFields() {
		assertThrows(NullPointerException.class,
			() -> new BasicSolverResult<>(null, ConvergenceStatus.CONVERGED, 1, new Real(0.0)));
		assertThrows(NullPointerException.class,
			() -> new BasicSolverResult<>("valore", null, 1, new Real(0.0)));
		assertThrows(NullPointerException.class,
			() -> new BasicSolverResult<>("valore", ConvergenceStatus.CONVERGED, 1, null));
	}

	@Test
	@DisplayName("RootFindingSolverResult espone anche il residuo finale tramite ResidualAware")
	void rootFindingSolverResultExposesResidualViaCapability() {
		SolverResult<Real> result = new RootFindingSolverResult<>(new Real(1.0), ConvergenceStatus.CONVERGED, 5, new Real(1e-8), new Real(1e-9));

		assertTrue(result instanceof ResidualAware);
		Real residual = ((ResidualAware) result).getFinalResidual();
		assertEquals(1e-9, residual.getValue(), 1e-15);
	}

	@Test
	@DisplayName("RootFindingSolverResult rifiuta un residuo finale nullo")
	void rootFindingSolverResultRejectsNullResidual() {
		assertThrows(NullPointerException.class,
			() -> new RootFindingSolverResult<>(new Real(1.0), ConvergenceStatus.CONVERGED, 1, new Real(0.0), null));
	}
}
