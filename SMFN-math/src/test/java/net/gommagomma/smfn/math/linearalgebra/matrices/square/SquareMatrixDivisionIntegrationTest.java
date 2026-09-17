package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialDivisionProvider;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialDivisionResult;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialRing;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;

/**
 * Verifica PolynomialDivisionProvider (algebra.polynomial) applicato a
 * SquareMatrix<Complex> -- l'unico punto della libreria dove un anello
 * genuinamente non commutativo viene usato come coefficiente polinomiale.
 * Non e' un test di algebra.polynomial (che non deve dipendere da
 * linearalgebra): e' un test di integrazione, e vive qui di conseguenza.
 */
@DisplayName("Divisione polinomiale a coefficienti SquareMatrix<Complex> (non commutativi)")
class SquareMatrixDivisionIntegrationTest
{
	private final ComplexField C = ComplexField.INSTANCE;
	private final SquareMatrixRing<Complex, ComplexField> M2 = new SquareMatrixRing<>(C, 2);
	private final SquareMatrixAlgebra<Complex, ComplexField> M2Algebra = new SquareMatrixAlgebra<>(C, 2);
	private final PolynomialRing<SquareMatrix<Complex>, SquareMatrixAlgebra<Complex, ComplexField>> polyRing =
		new PolynomialRing<>(M2Algebra);
	private final PolynomialDivisionProvider<SquareMatrix<Complex>, SquareMatrixAlgebra<Complex, ComplexField>> provider =
		new PolynomialDivisionProvider<>(M2Algebra);

	private SquareMatrix<Complex> m(double... reOnly) {
		Complex[] data = new Complex[reOnly.length];
		for (int i = 0; i < reOnly.length; i++) data[i] = new Complex(reOnly[i], 0);
		return M2.of(data);
	}

	@Test
	@DisplayName("dividendo = quoziente*divisore + resto, verificato nel verso corretto (K non commuta)")
	void divisionIdentityHoldsInCorrectOrder() {
		SquareMatrix<Complex> identity = m(1, 0, 0, 1);
		SquareMatrix<Complex> rotation90 = m(0, -1, 1, 0);

		// pmz(x) = I + R90*x, qmz(x) = R90 + I*x -- coefficiente di testa del divisore = R90, invertibile
		Polynomial<SquareMatrix<Complex>> pmz = polyRing.of(List.of(identity, rotation90));
		Polynomial<SquareMatrix<Complex>> qmz = polyRing.of(List.of(rotation90, identity));

		PolynomialDivisionResult<SquareMatrix<Complex>> result = provider.divide(pmz, qmz);

		// Attenzione all'ordine: quoziente*divisore, MAI divisore*quoziente (differiscono, K non commuta)
		Polynomial<SquareMatrix<Complex>> reconstructed = polyRing.add(
			polyRing.multiply(result.quotient(), qmz), result.remainder());

		assertTrue(pmz.equals(reconstructed));
	}

	@Test
	@DisplayName("Divisore con coefficiente di testa singolare: lancia ArithmeticException")
	void singularLeadingCoefficientThrows() {
		SquareMatrix<Complex> identity = m(1, 0, 0, 1);
		SquareMatrix<Complex> singular = m(1, 2, 2, 4); // righe proporzionali, non invertibile

		Polynomial<SquareMatrix<Complex>> dividend = polyRing.of(List.of(identity, identity));
		Polynomial<SquareMatrix<Complex>> divisor = polyRing.of(List.of(identity, singular));

		assertThrows(ArithmeticException.class, () -> provider.divide(dividend, divisor));
	}
}
