package net.gommagomma.smfn.math.analysis.numerical.solvers.ode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.IntegrationParameters;
import net.gommagomma.smfn.math.linearalgebra.vectors.InnerProductVectorSpace;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

@DisplayName("EmbeddedRK23Solver: integrazione a passo adattivo")
class EmbeddedRK23SolverTest
{
	private static final RealField R = RealField.INSTANCE;
	private final InnerProductVectorSpace<Real, RealField> V1 = new InnerProductVectorSpace<>(R, 1);

	private InitialValueProblem<Real, Vector<Real>> exponentialGrowth() {
		return new InitialValueProblem<Real, Vector<Real>>() {
			@Override public Vector<Real> derivative(Vector<Real> state, Real time) { return state; }
			@Override public Vector<Real> getInitialState() { return V1.of(new Real[] { new Real(1.0) }); }
			@Override public Real getStartTime() { return new Real(0.0); }
		};
	}

	@Test
	@DisplayName("y' = y, y(0) = 1, tolleranza ragionevole: converge a e con l'accuratezza richiesta")
	void integratesWithAdaptiveStep() {
		EmbeddedRK23Solver<Real, Vector<Real>, RealField> solver = new EmbeddedRK23Solver<>();
		IntegrationParameters params = new IntegrationParameters(null, new Real(1e-8), new Real(0.1), new Real(1e-6));

		Vector<Real> result = solver.integrate(exponentialGrowth(), new Real(1.0), params, V1);

		assertEquals(Math.exp(1.0), result.get(0).getValue(), 1e-6);
	}

	@Test
	@DisplayName("Regressione: tolleranza irraggiungibile deve lanciare, non bloccarsi in un ciclo infinito")
	void impossibleToleranceThrowsInsteadOfHanging() {
		EmbeddedRK23Solver<Real, Vector<Real>, RealField> solver = new EmbeddedRK23Solver<>();
		// tolleranza 1e-300: irraggiungibile anche al passo minimo consentito.
		// Il rifiuto del passo deve poter scendere sotto minStepSize e lanciare,
		// non restare bloccato a ripetere lo stesso passo clampato all'infinito.
		IntegrationParameters params = new IntegrationParameters(null, new Real(1e-300), new Real(1.0), new Real(0.001));

		assertTimeoutPreemptively(Duration.ofSeconds(5), () ->
			assertThrows(RuntimeException.class, () -> solver.integrate(exponentialGrowth(), new Real(1.0), params, V1))
		);
	}
}
