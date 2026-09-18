package net.gommagomma.smfn.physics.mq;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;
import net.gommagomma.smfn.math.linearalgebra.vectors.InnerProductVectorSpace;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.utils.MathConstants;

@DisplayName("QuantumSystemSimulator: valore di aspettazione <psi|H|psi>")
class QuantumSystemSimulatorTest
{
	private static final double EPSILON = MathConstants.EPSILON;
	private final ComplexField C = ComplexField.INSTANCE;
	private final SquareMatrixRing<Complex, ComplexField> M2 = new SquareMatrixRing<>(C, 2);
	private final InnerProductVectorSpace<Complex, ComplexField> ip = new InnerProductVectorSpace<>(C, 2);
	private final QuantumSystemSimulator simulator = new QuantumSystemSimulator();

	private final SquareMatrix<Complex> pauliX = M2.of(new Complex[] {
		new Complex(0, 0), new Complex(1, 0),
		new Complex(1, 0), new Complex(0, 0)
	});

	@Test
	@DisplayName("<0|Pauli-X|0> = 0: |0> non e' autostato di Pauli-X")
	void expectationOfNonEigenstateIsZero() {
		Observable H = new Observable(pauliX);
		Vector<Complex> psi0 = ip.of(new Complex[] { new Complex(1, 0), new Complex(0, 0) });

		Real expectation = simulator.measure(ip, H, psi0);
		assertTrue(Math.abs(expectation.getValue()) < EPSILON);
	}

	@Test
	@DisplayName("<+|Pauli-X|+> = 1: |+> = (|0>+|1>)/sqrt(2) e' autostato con autovalore +1")
	void expectationOfEigenstateIsEigenvalue() {
		Observable H = new Observable(pauliX);
		double invSqrt2 = 1.0 / Math.sqrt(2.0);
		Vector<Complex> plus = ip.of(new Complex[] { new Complex(invSqrt2, 0), new Complex(invSqrt2, 0) });

		Real expectation = simulator.measure(ip, H, plus);
		assertTrue(Math.abs(expectation.getValue() - 1.0) < EPSILON);
	}

	@Test
	@DisplayName("<-|Pauli-X|-> = -1: |-> = (|0>-|1>)/sqrt(2) e' autostato con autovalore -1")
	void expectationOfOtherEigenstateIsNegativeEigenvalue() {
		Observable H = new Observable(pauliX);
		double invSqrt2 = 1.0 / Math.sqrt(2.0);
		Vector<Complex> minus = ip.of(new Complex[] { new Complex(invSqrt2, 0), new Complex(-invSqrt2, 0) });

		Real expectation = simulator.measure(ip, H, minus);
		assertTrue(Math.abs(expectation.getValue() - (-1.0)) < EPSILON);
	}
}
