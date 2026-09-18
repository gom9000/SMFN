package net.gommagomma.smfn.physics.mq;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;

@DisplayName("Observable: il vincolo di hermitianita' e' verificato al costruttore, non promesso a priori")
class ObservableTest
{
	private final ComplexField C = ComplexField.INSTANCE;
	private final SquareMatrixRing<Complex, ComplexField> M2 = new SquareMatrixRing<>(C, 2);

	@Test
	@DisplayName("Accetta un operatore hermitiano (Pauli-X)")
	void acceptsHermitianOperator() {
		SquareMatrix<Complex> pauliX = M2.of(new Complex[] {
			new Complex(0, 0), new Complex(1, 0),
			new Complex(1, 0), new Complex(0, 0)
		});

		Observable observable = new Observable(pauliX);
		assertTrue(observable.asOperator().equals(pauliX));
	}

	@Test
	@DisplayName("Rifiuta un operatore non hermitiano")
	void rejectsNonHermitianOperator() {
		SquareMatrix<Complex> notHermitian = M2.of(new Complex[] {
			new Complex(1, 0), new Complex(1, 0),
			new Complex(0, 0), new Complex(1, 0)
		});

		assertThrows(IllegalArgumentException.class, () -> new Observable(notHermitian));
	}
}
