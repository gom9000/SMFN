package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.composite.LinearSpace;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.LinearSpaceAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * SquareMatrixAlgebra non implementa Field (matrici singolari esistono,
 * la moltiplicazione non commuta) -- verifica solo LinearSpace (genuinamente
 * vera: le matrici quadrate su un campo formano uno spazio vettoriale) e
 * InvertibleElements (inverso solo dove esiste davvero, mai promesso a priori).
 */
@DisplayName("SquareMatrixAlgebra<Complex>: assiomi di Spazio Vettoriale + InvertibleElements")
class SquareMatrixAlgebraTest extends LinearSpaceAxiomContract<SquareMatrix<Complex>, Complex, ComplexField>
{
	private final ComplexField C = ComplexField.INSTANCE;
	private final SquareMatrixAlgebra<Complex, ComplexField> M2 = new SquareMatrixAlgebra<>(C, 2);

	private SquareMatrix<Complex> m(double... reOnly) {
		Complex[] data = new Complex[reOnly.length];
		for (int i = 0; i < reOnly.length; i++) data[i] = new Complex(reOnly[i], 0);
		return M2.of(data);
	}

	@Override
	protected LinearSpace<SquareMatrix<Complex>, Complex, ComplexField> structure() { return M2; }

	@Override
	protected SquareMatrix<Complex> a() { return m(1, 2, 3, 4); }
	@Override
	protected SquareMatrix<Complex> b() { return m(2, 0, 1, 1); }
	@Override
	protected SquareMatrix<Complex> c() { return m(1, 1, 0, 2); }
	@Override
	protected Complex k1() { return new Complex(-3, 1); }
	@Override
	protected Complex k2() { return new Complex(0.5, -2); }
	@Override
	protected Complex nonZeroScalar() { return new Complex(2, 1); }

	@Nested
	@DisplayName("InvertibleElements")
	class InvertibleElementsTests
	{
		@Test
		@DisplayName("Matrice non singolare: e' invertibile, e A*A^-1 = I")
		void invertibleMatrixHasWorkingInverse() {
			SquareMatrix<Complex> matrix = a();
			assertTrue(M2.isInvertible(matrix));

			SquareMatrix<Complex> inverse = M2.inverse(matrix);
			SquareMatrix<Complex> identityCheck = M2.multiply(matrix, inverse);
			assertTrue(M2.isOne(identityCheck));
		}

		@Test
		@DisplayName("Matrice singolare: non e' invertibile, inverse() lancia ArithmeticException")
		void singularMatrixHasNoInverse() {
			SquareMatrix<Complex> singular = m(1, 2, 2, 4); // riga 2 = 2 * riga 1
			assertFalse(M2.isInvertible(singular));
			assertThrows(ArithmeticException.class, () -> M2.inverse(singular));
		}
	}

	@Test
	@DisplayName("Determinante 4x4: Laplace (SquareMatrixRing) e Gauss (SquareMatrixAlgebra) devono coincidere")
	void laplaceAndGaussAgreeOnFourByFour() {
		// Matrice a blocchi con uno zero sulla diagonale: [[0,1],[1,0]] (det=-1) e diag(2,3) (det=6) -> det=-6.
		// Regressione del bug del pivot mai cercato: senza la ricerca del pivot, Gauss si fermava a zero.
		RealField R = RealField.INSTANCE;
		SquareMatrixRing<Real, RealField> M4Ring = new SquareMatrixRing<>(R, 4);
		SquareMatrixAlgebra<Real, RealField> M4Algebra = new SquareMatrixAlgebra<>(R, 4);

		Real[] data = new Real[16];
		double[] values = { 0,1,0,0, 1,0,0,0, 0,0,2,0, 0,0,0,3 };
		for (int i = 0; i < 16; i++) data[i] = new Real(values[i]);
		SquareMatrix<Real> m4 = M4Ring.of(data);

		Real detLaplace = M4Ring.determinant(m4);
		Real detGauss = M4Algebra.determinant(m4);

		assertTrue(R.areEqual(detLaplace, new Real(-6.0)));
		assertTrue(R.areEqual(detGauss, new Real(-6.0)));
	}
}
