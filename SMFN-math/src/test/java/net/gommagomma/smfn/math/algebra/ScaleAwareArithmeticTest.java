package net.gommagomma.smfn.math.algebra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.polynomial.EuclideanPolynomialRing;
import net.gommagomma.smfn.math.algebra.polynomial.Polynomial;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialDivisionResult;
import net.gommagomma.smfn.math.algebra.polynomial.PolynomialElementFactory;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.algebra.structures.RealField;

@DisplayName("Aritmetica approssimata: zero esatto, trascurabilita' relativa a una scala, nessuna soglia assoluta")
class ScaleAwareArithmeticTest
{
	private static final RealField R = RealField.INSTANCE;
	private static final ComplexField C = ComplexField.INSTANCE;

	@Test
	@DisplayName("isZero() e' lo zero esatto: un valore piccolo ma non nullo non e' zero")
	void isZeroIsExact() {
		assertTrue(R.isZero(new Real(0.0)));
		assertTrue(R.isZero(new Real(-0.0)));
		assertFalse(R.isZero(new Real(1e-300)));
		assertTrue(C.isZero(new Complex(0.0, 0.0)));
		assertFalse(C.isZero(new Complex(0.0, 1e-300)));
	}

	@Test
	@DisplayName("isNegligible(): la soglia e' proporzionale alla scala, non assoluta")
	void isNegligibleIsRelativeToScale() {
		assertTrue(R.isNegligible(new Real(1e-16), new Real(1.0)));
		assertFalse(R.isNegligible(new Real(1e-13), new Real(1e-3)));
		assertTrue(R.isNegligible(new Real(1e-13), new Real(1e3)));
		assertFalse(R.isNegligible(new Real(1e-30), new Real(1e-20)));
		assertTrue(C.isNegligible(new Complex(1e-30, 1e-30), new Real(1e-10)));
		assertFalse(C.isNegligible(new Complex(1e-13, 0.0), new Real(1e-3)));
		assertTrue(RationalField.INSTANCE.isNegligible(RationalField.INSTANCE.zero(), new Real(1.0)));
		assertFalse(RationalField.INSTANCE.isNegligible(RationalField.INSTANCE.of(1L, 1000000000000L), new Real(1.0)));
	}

	@Test
	@DisplayName("inverse() e divide(): un divisore piccolo ma non nullo e' valido")
	void smallDivisorsAreValid() {
		assertEquals(1e13, R.inverse(new Real(1e-13)).getValue(), 1e-3);
		assertEquals(1e-7, R.divide(new Real(1e-20), new Real(1e-13)).getValue(), 1e-20);
		assertEquals(1e13, C.inverse(new Complex(1e-13, 0.0)).getRe(), 1e-3);
		assertTrue(R.isInvertible(new Real(1e-13)));
	}

	@Test
	@DisplayName("inverse(): zero esatto e reciproco non rappresentabile sono rifiutati")
	void zeroAndUnrepresentableReciprocalsAreRejected() {
		assertThrows(ArithmeticException.class, () -> R.inverse(new Real(0.0)));
		assertThrows(ArithmeticException.class, () -> R.inverse(new Real(4.9e-324)));
		assertThrows(ArithmeticException.class, () -> C.inverse(new Complex(0.0, 0.0)));
		assertThrows(ArithmeticException.class, () -> C.inverse(new Complex(4.9e-324, 4.9e-324)));
		assertFalse(R.isInvertible(new Real(4.9e-324)));
		assertFalse(C.isInvertible(new Complex(0.0, 0.0)));
	}

	@Test
	@DisplayName("Polinomi: un coefficiente piccolo ma non nullo non viene scartato (il grado resta quello vero)")
	void smallCoefficientsAreKept() {
		Polynomial<Real> p = PolynomialElementFactory.of(R, new Real(1e-14), new Real(0.0), new Real(1e-13));

		assertEquals(2, p.degree());
		assertEquals(1e-14, p.getCoefficient(0).getValue(), 0.0);
	}

	@Test
	@DisplayName("Polinomi: l'uguaglianza e' alla scala del polinomio intero")
	void polynomialEqualityUsesWholePolynomialScale() {
		EuclideanPolynomialRing<Real, RealField> ring = new EuclideanPolynomialRing<>(R);
		Polynomial<Real> p = PolynomialElementFactory.of(R, new Real(1.0), new Real(2.0));
		Polynomial<Real> noisy = PolynomialElementFactory.of(R, new Real(1.0), new Real(2.0), new Real(1e-20));
		Polynomial<Real> tiny1 = PolynomialElementFactory.of(R, new Real(1e-20), new Real(2e-20));
		Polynomial<Real> tiny2 = PolynomialElementFactory.of(R, new Real(2e-20), new Real(4e-20));

		assertTrue(ring.areEqual(p, noisy)); // 1e-20 e' rumore rispetto a coefficienti dell'ordine di 1
		assertFalse(ring.areEqual(tiny1, tiny2)); // a scala 1e-20 queste due sono ben distinte
	}

	@Test
	@DisplayName("Divisione di polinomi in virgola mobile: il resto ha grado minore del divisore e q*d + r ricostruisce il dividendo")
	void divisionDropsTheDegreeEvenWithRoundingNoise() {
		EuclideanPolynomialRing<Real, RealField> ring = new EuclideanPolynomialRing<>(R);
		Random random = new Random(42);
		for (int trial = 0; trial < 200; trial++) {
			Polynomial<Real> dividend = randomPolynomial(random, 3 + random.nextInt(6));
			Polynomial<Real> divisor = randomPolynomial(random, 1 + random.nextInt(3));

			PolynomialDivisionResult<Real> result = ring.divide(dividend, divisor);

			assertTrue(result.remainder().degree() < divisor.degree(), "resto " + result.remainder() + " per divisore " + divisor);
			Polynomial<Real> rebuilt = ring.add(ring.multiply(result.quotient(), divisor), result.remainder());
			assertTrue(ring.areEqual(rebuilt, dividend), "ricostruito " + rebuilt + " atteso " + dividend);
		}
	}

	private static Polynomial<Real> randomPolynomial(Random random, int degree) {
		Real[] coefficients = new Real[degree + 1];
		for (int i = 0; i < degree; i++) coefficients[i] = new Real(random.nextDouble() * 4.0 - 2.0);
		coefficients[degree] = new Real(0.5 + random.nextDouble());
		return PolynomialElementFactory.of(R, coefficients);
	}
}
