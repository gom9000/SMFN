package net.gommagomma.smfn.math.analysis.core.solvers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;

@DisplayName("SolverResult: BasicSolverResult, IterativeSolverResult e RootFindingSolverResult")
class SolverResultTest
{
	@Test
	@DisplayName("BasicSolverResult espone esattamente i tre campi universali passati al costruttore")
	void basicSolverResultExposesCoreFields() {
		SolverResult<String> result = new BasicSolverResult<>("valore", TerminationStatus.CONVERGED, 7);

		assertEquals("valore", result.getValue());
		assertEquals(TerminationStatus.CONVERGED, result.getStatus());
		assertEquals(7, result.getIterationsExecuted());
	}

	@Test
	@DisplayName("BasicSolverResult non e' StepDistanceAware ne' ResidualAware: nessuna delle due capacita' opzionali e' definita senza una metrica esterna o un'equazione")
	void basicSolverResultHasNoOptionalCapabilities() {
		SolverResult<String> result = new BasicSolverResult<>("valore", TerminationStatus.MAX_ITERATIONS_REACHED, 3);

		assertFalse(result instanceof StepDistanceAware);
		assertFalse(result instanceof ResidualAware);
	}

	@Test
	@DisplayName("BasicSolverResult rifiuta un numero di iterazioni negativo")
	void basicSolverResultRejectsNegativeIterations() {
		assertThrows(IllegalArgumentException.class,
			() -> new BasicSolverResult<>("valore", TerminationStatus.CONVERGED, -1));
	}

	@Test
	@DisplayName("BasicSolverResult rifiuta value o status nulli")
	void basicSolverResultRejectsNullFields() {
		assertThrows(NullPointerException.class,
			() -> new BasicSolverResult<>(null, TerminationStatus.CONVERGED, 1));
		assertThrows(NullPointerException.class,
			() -> new BasicSolverResult<>("valore", null, 1));
	}

	@Test
	@DisplayName("IterativeSolverResult espone anche la distanza dell'ultimo passo tramite StepDistanceAware, ma non e' ResidualAware")
	void iterativeSolverResultExposesStepDistanceViaCapability() {
		SolverResult<String> result = new IterativeSolverResult<>("valore", TerminationStatus.CONVERGED, 7, new Real(0.001));

		assertTrue(result instanceof StepDistanceAware);
		assertFalse(result instanceof ResidualAware);
		Real stepDistance = ((StepDistanceAware) result).getFinalStepDistance();
		assertEquals(0.001, stepDistance.getValue(), 1e-12);
	}

	@Test
	@DisplayName("IterativeSolverResult rifiuta una distanza finale nulla")
	void iterativeSolverResultRejectsNullStepDistance() {
		assertThrows(NullPointerException.class,
			() -> new IterativeSolverResult<>("valore", TerminationStatus.CONVERGED, 1, null));
	}

	@Test
	@DisplayName("RootFindingSolverResult espone sia la distanza dell'ultimo passo sia il residuo finale")
	void rootFindingSolverResultExposesResidualViaCapability() {
		SolverResult<Real> result = new RootFindingSolverResult<>(new Real(1.0), TerminationStatus.CONVERGED, 5, new Real(1e-8), new Real(1e-9));

		assertTrue(result instanceof StepDistanceAware);
		assertTrue(result instanceof ResidualAware);
		Real stepDistance = ((StepDistanceAware) result).getFinalStepDistance();
		Real residual = ((ResidualAware) result).getFinalResidual();
		assertEquals(1e-8, stepDistance.getValue(), 1e-15);
		assertEquals(1e-9, residual.getValue(), 1e-15);
	}

	@Test
	@DisplayName("RootFindingSolverResult rifiuta un residuo finale nullo")
	void rootFindingSolverResultRejectsNullResidual() {
		assertThrows(NullPointerException.class,
			() -> new RootFindingSolverResult<>(new Real(1.0), TerminationStatus.CONVERGED, 1, new Real(0.0), null));
	}
}
