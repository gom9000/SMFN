package net.gommagomma.smfn.physics.mq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.solvers.StoppingParameters;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Invarianti fisico-algebrici del pacchetto physics.mq sul catalogo di PhysicsTestValues.
 * <p>
 * A differenza dei *Test esistenti (casi noti verificati a mano, come "<+|X|+> = 1"), qui si
 * verificano proprieta' che devono valere per costruzione su QUALUNQUE osservabile/stato del
 * catalogo, senza conoscerne il risultato numerico a priori: la realta' del valore di
 * aspettazione per un operatore hermitiano, la coerenza fra la regola di Born e la formula del
 * valore di aspettazione (sum_i p_i*lambda_i == <psi|H|psi>, due formule matematicamente
 * equivalenti ma calcolate da codice diverso), l'equazione agli autovalori per gli stati
 * stazionari, e la conservazione della norma sotto evoluzione unitaria (Schrodinger).
 */
@DisplayName("physics.mq: invarianti fisico-algebrici (realta', regola di Born, autostati, evoluzione unitaria) sul catalogo")
class PhysicsInvariantsTest
{
	private static final ComplexField C = ComplexField.INSTANCE;
	private static final double EPSILON = 1e-9;
	private final QuantumSystemSimulator simulator = new QuantumSystemSimulator();

	@Test
	@DisplayName("norm() non e' mai negativa, e normalize() produce sempre uno stato di norma 1, su tutto il catalogo")
	void normIsNonNegativeAndNormalizeProducesUnitNorm() {
		for (QuantumState psi : concatAllStates()) {
			assertTrue(psi.norm().getValue() >= 0.0, "stato: " + psi);
			assertEquals(1.0, psi.normalize().norm().getValue(), EPSILON, "stato: " + psi);
		}
	}

	@Test
	@DisplayName("Simmetria hermitiana del prodotto interno: <phi|psi> == coniugato di <psi|phi>, su tutte le coppie del catalogo")
	void innerProductIsHermitianSymmetric() {
		List<QuantumState> states = PhysicsTestValues.twoDimStates();
		for (QuantumState phi : states) {
			for (QuantumState psi : states) {
				Complex left = phi.innerProduct(psi);
				Complex right = psi.innerProduct(phi).conjugate();
				assertEquals(right.getRe(), left.getRe(), EPSILON, "phi=" + phi + " psi=" + psi);
				assertEquals(right.getIm(), left.getIm(), EPSILON, "phi=" + phi + " psi=" + psi);
			}
		}
	}

	@Test
	@DisplayName("Disuguaglianza di Cauchy-Schwarz: |<phi|psi>|^2 <= ||phi||^2 * ||psi||^2, su tutte le coppie del catalogo")
	void cauchySchwarzInequalityHolds() {
		List<QuantumState> states = PhysicsTestValues.twoDimStates();
		for (QuantumState phi : states) {
			for (QuantumState psi : states) {
				double innerProductSquared = phi.innerProduct(psi).modulusSquared();
				double bound = phi.norm().getValue() * phi.norm().getValue() * psi.norm().getValue() * psi.norm().getValue();
				assertTrue(innerProductSquared <= bound + EPSILON, "phi=" + phi + " psi=" + psi);
			}
		}
	}

	@Test
	@DisplayName("Il valore di aspettazione di un operatore hermitiano e' sempre reale (parte immaginaria nulla), su tutto il catalogo")
	void expectationValueIsAlwaysReal() {
		for (Observable<Complex> observable : PhysicsTestValues.twoDimObservables()) {
			for (QuantumState psi : PhysicsTestValues.twoDimStates()) {
				Vector<Complex> hPsi = observable.asOperator().apply(psi.asVector());
				Complex rawExpectation = psi.innerProduct(QuantumState.from(hPsi));
				assertEquals(0.0, rawExpectation.getIm(), EPSILON, "osservabile+stato: " + psi);
			}
		}
	}

	@Test
	@DisplayName("expectationValue() e' invariante per riscalamento non nullo dello stato, su tutto il catalogo")
	void expectationValueIsInvariantUnderNonZeroRescaling() {
		Complex scaleFactor = new Complex(3.0, -2.0);
		for (Observable<Complex> observable : PhysicsTestValues.twoDimObservables()) {
			for (QuantumState psi : PhysicsTestValues.twoDimStates()) {
				Real direct = simulator.expectationValue(observable, psi);
				Real scaled = simulator.expectationValue(observable, psi.scale(scaleFactor));
				assertEquals(direct.getValue(), scaled.getValue(), EPSILON, "osservabile+stato: " + psi);
			}
		}
	}

	@Test
	@DisplayName("Coerenza fra regola di Born e valore di aspettazione: sum_i p_i*lambda_i == <psi|H|psi>, su tutto il catalogo")
	void bornRuleIsConsistentWithExpectationValue() {
		StoppingParameters params = new StoppingParameters(new Real(1e-10), 100);

		for (Observable<Complex> observable : PhysicsTestValues.twoDimObservables()) {
			for (QuantumState psi : PhysicsTestValues.twoDimStates()) {
				List<MeasurementProbability> distribution = simulator.measurementProbabilities(observable, psi, params);

				double totalProbability = 0.0;
				double weightedSum = 0.0;
				for (MeasurementProbability outcome : distribution) {
					totalProbability += outcome.getProbability().getValue();
					weightedSum += outcome.getProbability().getValue() * outcome.getValue().getValue();
				}

				assertEquals(1.0, totalProbability, 1e-8, "osservabile+stato: " + psi);

				Real expectation = simulator.expectationValue(observable, psi);
				assertEquals(expectation.getValue(), weightedSum, 1e-6, "osservabile+stato: " + psi);
			}
		}
	}

	@Test
	@DisplayName("Le probabilita' di Born sono sempre in [0,1], su tutto il catalogo")
	void bornProbabilitiesAreAlwaysInUnitInterval() {
		StoppingParameters params = new StoppingParameters(new Real(1e-10), 100);
		for (Observable<Complex> observable : PhysicsTestValues.twoDimObservables()) {
			for (QuantumState psi : PhysicsTestValues.twoDimStates()) {
				for (MeasurementProbability outcome : simulator.measurementProbabilities(observable, psi, params)) {
					double p = outcome.getProbability().getValue();
					assertTrue(p >= -EPSILON && p <= 1.0 + EPSILON, "probabilita' fuori range: " + p);
				}
			}
		}
	}

	@Test
	@DisplayName("Gli stati stazionari soddisfano H|psi_n> = E_n|psi_n>, sia per Hamiltoniane 2x2 sia 3x3")
	void stationaryStatesSatisfyEigenvalueEquation() {
		StoppingParameters params = new StoppingParameters(new Real(1e-10), 200);

		for (Observable<Complex> observable : PhysicsTestValues.twoDimObservables()) {
			assertEigenvalueEquationHolds(new Hamiltonian<>(observable.asOperator()), params);
		}
		assertEigenvalueEquationHolds(new Hamiltonian<>(PhysicsTestValues.threeDimObservable().asOperator()), params);
	}

	private void assertEigenvalueEquationHolds(Hamiltonian<Complex> hamiltonian, StoppingParameters params) {
		StationaryStates stationary = hamiltonian.findStationaryStates(params);
		List<Real> energies = stationary.getEnergyLevels();
		List<QuantumState> states = stationary.getStates();

		for (int i = 0; i < energies.size(); i++) {
			Vector<Complex> hPsi = hamiltonian.asOperator().apply(states.get(i).asVector());
			Vector<Complex> ePsi = states.get(i).scale(new Complex(energies.get(i).getValue(), 0.0)).asVector();

			for (int k = 0; k < hPsi.size(); k++) {
				assertEquals(ePsi.get(k).getRe(), hPsi.get(k).getRe(), 1e-6, "componente " + k);
				assertEquals(ePsi.get(k).getIm(), hPsi.get(k).getIm(), 1e-6, "componente " + k);
			}
		}
	}

	@Test
	@DisplayName("L'evoluzione di Schrodinger e' unitaria: la norma dello stato e' conservata nel tempo, su tutto il catalogo")
	void schrodingerEvolutionPreservesNorm() {
		for (Observable<Complex> observable : PhysicsTestValues.twoDimObservables()) {
			SchrodingerEquationSystem system = new SchrodingerEquationSystem(observable);
			for (QuantumState initial : PhysicsTestValues.twoDimStates()) {
				double initialNorm = initial.norm().getValue();
				for (double t : new double[] { 0.3, 1.0, 2.5 }) {
					QuantumState evolved = system.evolve(initial, new Real(t), new Real(0.001));
					assertEquals(initialNorm, evolved.norm().getValue(), 1e-6, "t=" + t + " stato iniziale=" + initial);
				}
			}
		}
	}

	private static List<QuantumState> concatAllStates() {
		java.util.List<QuantumState> all = new java.util.ArrayList<>();
		all.addAll(PhysicsTestValues.twoDimStates());
		all.addAll(PhysicsTestValues.threeDimStates());
		return all;
	}
}
