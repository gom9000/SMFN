package net.gommagomma.smfn.math.analysis.numerical.solvers.ode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.problems.InitialValueProblem;
import net.gommagomma.smfn.math.analysis.core.solvers.IntegrationParameters;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorModule;

@DisplayName("RungeKutta4Solver: integrazione a passo fisso")
class RungeKutta4SolverTest
{
	private static final RealField R = RealField.INSTANCE;
	private final VectorModule<Real, RealField> V1 = new VectorModule<>(R, 1);

	private InitialValueProblem<Real, Vector<Real>> exponentialGrowth(double y0, double t0) {
		return new InitialValueProblem<Real, Vector<Real>>() {
			@Override public Vector<Real> derivative(Vector<Real> state, Real time) { return state; }
			@Override public Vector<Real> getInitialState() { return V1.of(new Real[] { new Real(y0) }); }
			@Override public Real getStartTime() { return new Real(t0); }
		};
	}

	@Test
	@DisplayName("y' = y, y(0) = 1: y(1) coincide con e entro l'errore atteso di un metodo di ordine 4")
	void integratesExponentialGrowthForward() {
		RungeKutta4Solver<Real, Vector<Real>, RealField> solver = new RungeKutta4Solver<>();
		IntegrationParameters params = new IntegrationParameters(new Real(0.01));

		Vector<Real> result = solver.integrate(exponentialGrowth(1.0, 0.0), new Real(1.0), params, V1);

		assertEquals(Math.exp(1.0), result.get(0).getValue(), 1e-8);
	}

	@Test
	@DisplayName("Integrazione all'indietro (endTime < startTime) converge alla soluzione corretta")
	void integratesBackwardCorrectly() {
		RungeKutta4Solver<Real, Vector<Real>, RealField> solver = new RungeKutta4Solver<>();
		IntegrationParameters params = new IntegrationParameters(new Real(0.01));

		// y(1) = e; integrando all'indietro fino a t=0 dovremmo ritrovare y(0) = 1
		Vector<Real> result = solver.integrate(exponentialGrowth(Math.exp(1.0), 1.0), new Real(0.0), params, V1);

		assertEquals(1.0, result.get(0).getValue(), 1e-7);
	}

	@Test
	@DisplayName("Regressione: fixedStepSize negativo con integrazione in avanti non deve entrare in ciclo infinito")
	void doesNotLoopForeverWithNegativeStepSizeGoingForward() {
		RungeKutta4Solver<Real, Vector<Real>, RealField> solver = new RungeKutta4Solver<>();
		// segno "sbagliato" per errore del chiamante: il solver deve correggerlo da solo,
		// derivando la direzione da (startTime, endTime) e non dal segno di fixedStepSize
		IntegrationParameters params = new IntegrationParameters(new Real(-0.01));

		assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
			Vector<Real> result = solver.integrate(exponentialGrowth(1.0, 0.0), new Real(1.0), params, V1);
			assertEquals(Math.exp(1.0), result.get(0).getValue(), 1e-6);
		});
	}
}
