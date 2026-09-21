package net.gommagomma.smfn.physics.mq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;
import net.gommagomma.smfn.math.utils.MathConstants;

@DisplayName("QuantumSystemSimulator: valore di aspettazione <psi|H|psi>")
class QuantumSystemSimulatorTest
{
	private static final double EPSILON = MathConstants.EPSILON;
	private final ComplexField C = ComplexField.INSTANCE;
	private final SquareMatrixRing<Complex, ComplexField> M2 = new SquareMatrixRing<>(C, 2);
	private final QuantumSystemSimulator simulator = new QuantumSystemSimulator();

	private final SquareMatrix<Complex> pauliX = M2.of(new Complex[] {
		new Complex(0, 0), new Complex(1, 0),
		new Complex(1, 0), new Complex(0, 0)
	});

	@Test
	@DisplayName("<0|Pauli-X|0> = 0: |0> non e' autostato di Pauli-X")
	void expectationOfNonEigenstateIsZero() {
		Observable H = new Observable(pauliX);
		QuantumState psi0 = QuantumState.of(new Complex(1, 0), new Complex(0, 0));

		Real expectation = simulator.measure(H, psi0);
		assertTrue(Math.abs(expectation.getValue()) < EPSILON);
	}

	@Test
	@DisplayName("<+|Pauli-X|+> = 1: |+> = (|0>+|1>)/sqrt(2) e' autostato con autovalore +1")
	void expectationOfEigenstateIsEigenvalue() {
		Observable H = new Observable(pauliX);
		double invSqrt2 = 1.0 / Math.sqrt(2.0);
		QuantumState plus = QuantumState.of(new Complex(invSqrt2, 0), new Complex(invSqrt2, 0));

		Real expectation = simulator.measure(H, plus);
		assertTrue(Math.abs(expectation.getValue() - 1.0) < EPSILON);
	}

	@Test
	@DisplayName("<-|Pauli-X|-> = -1: |-> = (|0>-|1>)/sqrt(2) e' autostato con autovalore -1")
	void expectationOfOtherEigenstateIsNegativeEigenvalue() {
		Observable H = new Observable(pauliX);
		double invSqrt2 = 1.0 / Math.sqrt(2.0);
		QuantumState minus = QuantumState.of(new Complex(invSqrt2, 0), new Complex(-invSqrt2, 0));

		Real expectation = simulator.measure(H, minus);
		assertTrue(Math.abs(expectation.getValue() - (-1.0)) < EPSILON);
	}

	@Test
	@DisplayName("performMeasurement() su un autostato: esito sempre lo stesso autovalore, stato invariato")
	void measurementOnEigenstateIsDeterministic() {
		Observable H = new Observable(pauliX);
		double invSqrt2 = 1.0 / Math.sqrt(2.0);
		QuantumState plus = QuantumState.of(new Complex(invSqrt2, 0), new Complex(invSqrt2, 0));
		ConvergenceParameters params = new ConvergenceParameters(new Real(1e-12), 100);
		Random random = new Random(1);

		for (int i = 0; i < 10; i++) {
			MeasurementOutcome outcome = simulator.performMeasurement(H, plus, params, random);
			assertTrue(Math.abs(outcome.getValue().getValue() - 1.0) < 1e-9);
		}
	}

	@Test
	@DisplayName("performMeasurement() su una sovrapposizione: distribuzione statistica coerente con la regola di Born")
	void measurementOnSuperpositionFollowsBornRule() {
		double invSqrt2 = 1.0 / Math.sqrt(2.0);
		QuantumState plus = QuantumState.of(new Complex(invSqrt2, 0), new Complex(invSqrt2, 0));
		ConvergenceParameters params = new ConvergenceParameters(new Real(1e-12), 100);
		Random random = new Random(99);

		// |+> e' un autostato di Pauli-X, quindi il risultato dovrebbe essere
		// SEMPRE +1 -- verifica indipendente rispetto al test deterministico sopra,
		// usato qui come base: costruiamo invece una base diversa (|0>,|1>) per
		// avere probabilita' genuinamente 50/50 rispetto a un H differente.
		SquareMatrix<Complex> pauliZ = M2.of(new Complex[] {
			new Complex(1, 0), new Complex(0, 0),
			new Complex(0, 0), new Complex(-1, 0)
		});
		Observable Z = new Observable(pauliZ);

		int plusCount = 0, trials = 5000;
		for (int i = 0; i < trials; i++) {
			MeasurementOutcome outcome = simulator.performMeasurement(Z, plus, params, random);
			if (outcome.getValue().getValue() > 0) plusCount++;
		}
		double fraction = (double) plusCount / trials;
		assertTrue(Math.abs(fraction - 0.5) < 0.05); // tolleranza larga, e' statistico
	}

	@Test
	@DisplayName("measurementProbabilities(): su |+x> misurato con Pauli-Z, 50/50 ESATTO -- nessun campionamento, nessuna tolleranza statistica")
	void measurementProbabilitiesAreExact() {
		SquareMatrix<Complex> pauliZ = M2.of(new Complex[] {
			new Complex(1, 0), new Complex(0, 0),
			new Complex(0, 0), new Complex(-1, 0)
		});
		Observable Z = new Observable(pauliZ);
		double invSqrt2 = 1.0 / Math.sqrt(2.0);
		QuantumState plus = QuantumState.of(new Complex(invSqrt2, 0), new Complex(invSqrt2, 0));
		ConvergenceParameters params = new ConvergenceParameters(new Real(1e-12), 100);

		List<MeasurementProbability> probabilities = simulator.measurementProbabilities(Z, plus, params);

		assertEquals(2, probabilities.size());
		double total = 0.0;
		for (MeasurementProbability p : probabilities) {
			assertTrue(Math.abs(p.getProbability().getValue() - 0.5) < 1e-9);
			total += p.getProbability().getValue();
		}
		assertEquals(1.0, total, 1e-9); // le probabilita' sommano sempre a 1
	}

	@Test
	@DisplayName("measurementProbabilities(): su un autostato, probabilita' 1 per il proprio autovalore, 0 per l'altro")
	void measurementProbabilitiesOnEigenstateAreDeterministic() {
		Observable H = new Observable(pauliX);
		double invSqrt2 = 1.0 / Math.sqrt(2.0);
		QuantumState plus = QuantumState.of(new Complex(invSqrt2, 0), new Complex(invSqrt2, 0));
		ConvergenceParameters params = new ConvergenceParameters(new Real(1e-12), 100);

		List<MeasurementProbability> probabilities = simulator.measurementProbabilities(H, plus, params);

		boolean foundCertainOutcome = false;
		for (MeasurementProbability p : probabilities) {
			if (p.getValue().getValue() > 0) {
				assertEquals(1.0, p.getProbability().getValue(), 1e-9);
				foundCertainOutcome = true;
			} else {
				assertEquals(0.0, p.getProbability().getValue(), 1e-9);
			}
		}
		assertTrue(foundCertainOutcome);
	}
}
