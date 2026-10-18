package net.gommagomma.smfn.math.linearalgebra.operators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

@DisplayName("Polinomio caratteristico: i coefficienti di una matrice di scala piccola non vengono azzerati")
class CharacteristicPolynomialScaleTest
{
	private static final RealField R = RealField.INSTANCE;

	@Test
	@DisplayName("Per s*I2 il polinomio e' x^2 - 2 s x + s^2 per ogni scala s")
	void scaledIdentity() {
		for (double s : new double[] { 1e-7, 1.0, 1e7 }) {
			SquareMatrix<Real> m = SquareMatrixElementFactory.of(R, s, 0.0, 0.0, s);
			Polynomial<Real> p = new CharacteristicPolynomialMapping<Real, RealField>().apply(m);
			assertEquals(2, p.degree());
			assertEquals(s * s, p.getCoefficient(0).getValue(), 1e-12 * s * s);
			assertEquals(-2 * s, p.getCoefficient(1).getValue(), 1e-12 * s);
			assertEquals(1.0, p.getCoefficient(2).getValue(), 0.0);
		}
	}
}
