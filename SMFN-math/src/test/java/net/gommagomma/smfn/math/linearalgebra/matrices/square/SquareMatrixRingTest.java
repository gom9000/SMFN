package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.RingAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * SquareMatrixRing verifica solo gli assiomi di Ring, non di CommutativeRing:
 * la moltiplicazione tra matrici non commuta in generale (n >= 2), a
 * differenza degli scalari sottostanti. E' un anello onesto sulla propria
 * capacita', esattamente come CommutativePolynomialRing lo e' sulla propria.
 */
@DisplayName("SquareMatrixRing<Real>: assiomi di Anello (non commutativo)")
class SquareMatrixRingTest extends RingAxiomContract<SquareMatrix<Real>>
{
	private final RealField R = RealField.INSTANCE;
	private final SquareMatrixRing<Real, RealField> M2 = new SquareMatrixRing<>(R, 2);

	private SquareMatrix<Real> m(double... values) {
		Real[] data = new Real[values.length];
		for (int i = 0; i < values.length; i++) data[i] = new Real(values[i]);
		return M2.of(data);
	}

	@Override
	protected Ring<SquareMatrix<Real>> structure() { return M2; }

	@Override
	protected SquareMatrix<Real> a() { return m(1, 2, 3, 4); }
	@Override
	protected SquareMatrix<Real> b() { return m(2, 0, 1, 1); }
	@Override
	protected SquareMatrix<Real> c() { return m(1, 1, 0, 2); }

	@Test
	@DisplayName("La moltiplicazione NON e' commutativa in generale: A*B != B*A")
	void multiplicationIsNotCommutativeInGeneral() {
		SquareMatrix<Real> A = m(1, 1, 0, 1);
		SquareMatrix<Real> B = m(1, 0, 1, 1);

		SquareMatrix<Real> ab = M2.multiply(A, B);
		SquareMatrix<Real> ba = M2.multiply(B, A);

		assertFalse(M2.areEqual(ab, ba), "A*B e B*A devono differire per queste due matrici");
	}

	@Test
	@DisplayName("Determinante: det(A*B) = det(A)*det(B), anche se A*B != B*A")
	void determinantIsMultiplicative() {
		SquareMatrix<Real> A = m(1, 1, 0, 1);
		SquareMatrix<Real> B = m(1, 0, 1, 1);

		Real detA = M2.determinant(A);
		Real detB = M2.determinant(B);
		Real detAB = M2.determinant(M2.multiply(A, B));

		assertTrue(R.areEqual(detAB, R.multiply(detA, detB)));
	}

	@Test
	@DisplayName("Determinante di una matrice singolare (righe proporzionali) e' zero")
	void determinantOfSingularMatrixIsZero() {
		SquareMatrix<Real> singular = m(1, 2, 2, 4); // riga 2 = 2 * riga 1
		assertTrue(R.isZero(M2.determinant(singular)));
	}
}
