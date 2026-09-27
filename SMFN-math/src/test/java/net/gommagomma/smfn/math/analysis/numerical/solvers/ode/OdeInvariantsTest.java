package net.gommagomma.smfn.math.analysis.numerical.solvers.ode;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.analysis.core.solvers.IntegrationParameters;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Invarianti di RungeKutta4Solver (passo fisso) ed EmbeddedRK23Solver (passo adattivo) sul
 * catalogo di OdeTestValues. Come per RootFindingInvariantsTest, l'invariante centrale e' la
 * convergenza numerica al valore analitico noto, non una legge algebrica fissa; in aggiunta si
 * verifica empiricamente che RK4 abbia davvero ordine di convergenza 4 (dimezzare il passo
 * riduce l'errore di un fattore ~16), a garanzia che non sia stato implementato un metodo di
 * ordine inferiore che "sembra" funzionare sui casi semplici.
 */
@DisplayName("RungeKutta4Solver / EmbeddedRK23Solver: invarianti di convergenza sul catalogo di IVP con soluzione analitica nota")
class OdeInvariantsTest
{
	private static final RealField R = RealField.INSTANCE;

	@Test
	@DisplayName("RK4 a passo fisso converge alla soluzione analitica, su tutto il catalogo")
	void rk4ConvergesToAnalyticSolution() {
		RungeKutta4Solver<Real, Vector<Real>, RealField> solver = new RungeKutta4Solver<>();
		IntegrationParameters params = new IntegrationParameters(new Real(0.001));

		for (OdeTestValues.IvpCase testCase : OdeTestValues.cases()) {
			Vector<Real> result = solver.integrate(testCase.problem, new Real(testCase.endTime), params, testCase.space);

			for (int i = 0; i < testCase.analyticSolution.length; i++) {
				double expected = testCase.analyticSolution[i].applyAsDouble(testCase.endTime);
				assertTrue(Math.abs(expected - result.get(i).getValue()) < 1e-6,
					"caso: " + testCase.name + " componente " + i + ": atteso=" + expected + " ottenuto=" + result.get(i).getValue());
			}
		}
	}

	@Test
	@DisplayName("EmbeddedRK23 a passo adattivo converge alla soluzione analitica entro la tolleranza richiesta, su tutto il catalogo")
	void embeddedRk23ConvergesToAnalyticSolution() {
		EmbeddedRK23Solver<Real, Vector<Real>, RealField> solver = new EmbeddedRK23Solver<>();
		IntegrationParameters params = new IntegrationParameters(null, new Real(1e-9), new Real(0.1), new Real(1e-9));

		for (OdeTestValues.IvpCase testCase : OdeTestValues.cases()) {
			Vector<Real> result = solver.integrate(testCase.problem, new Real(testCase.endTime), params, testCase.space);

			for (int i = 0; i < testCase.analyticSolution.length; i++) {
				double expected = testCase.analyticSolution[i].applyAsDouble(testCase.endTime);
				assertTrue(Math.abs(expected - result.get(i).getValue()) < 1e-6,
					"caso: " + testCase.name + " componente " + i + ": atteso=" + expected + " ottenuto=" + result.get(i).getValue());
			}
		}
	}

	@Test
	@DisplayName("RK4 ha ordine di convergenza 4: dimezzare il passo riduce l'errore di un fattore ~16, non meno")
	void rk4HasFourthOrderConvergence() {
		OdeTestValues.IvpCase reference = OdeTestValues.cases().get(0); // y'=y, y(0)=1 -> e^t
		RungeKutta4Solver<Real, Vector<Real>, RealField> solver = new RungeKutta4Solver<>();

		double expected = reference.analyticSolution[0].applyAsDouble(reference.endTime);

		Vector<Real> coarse = solver.integrate(reference.problem, new Real(reference.endTime), new IntegrationParameters(new Real(0.02)), reference.space);
		Vector<Real> fine = solver.integrate(reference.problem, new Real(reference.endTime), new IntegrationParameters(new Real(0.01)), reference.space);

		double errorCoarse = Math.abs(expected - coarse.get(0).getValue());
		double errorFine = Math.abs(expected - fine.get(0).getValue());

		assertTrue(errorFine > 0.0, "l'errore a passo fine non dovrebbe essere esattamente zero in aritmetica in virgola mobile");
		double ratio = errorCoarse / errorFine;
		assertTrue(ratio > 10.0, "rapporto di riduzione dell'errore troppo basso per un metodo di ordine 4: " + ratio
			+ " (atteso ~16, errore grezzo=" + errorCoarse + " errore fine=" + errorFine + ")");
	}
}
