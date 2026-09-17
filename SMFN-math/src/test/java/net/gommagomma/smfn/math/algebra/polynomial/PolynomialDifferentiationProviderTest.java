package net.gommagomma.smfn.math.algebra.polynomial;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;

@DisplayName("PolynomialDifferentiationProvider: derivata simbolica")
class PolynomialDifferentiationProviderTest
{
	private final RationalField Q = RationalField.INSTANCE;
	private final EuclideanPolynomialRing<Rational, RationalField> ring = new EuclideanPolynomialRing<>(Q);
	private final PolynomialDifferentiationProvider<Rational, RationalField> provider = new PolynomialDifferentiationProvider<>(Q);

	private Polynomial<Rational> poly(long... coeffs) {
		List<Rational> list = new java.util.ArrayList<>();
		for (long c : coeffs) list.add(new Rational(c, 1));
		return new Polynomial<>(ring, Q, list);
	}

	@Test
	@DisplayName("d/dx(x^3 - 2x + 5) = 3x^2 - 2")
	void derivativeOfCubic() {
		Polynomial<Rational> p = poly(5, -2, 0, 1); // 5 - 2x + 0x^2 + x^3
		Polynomial<Rational> expected = poly(-2, 0, 3); // -2 + 0x + 3x^2

		Polynomial<Rational> derivative = provider.derivative(p);
		assertTrue(derivative.equals(expected), "Atteso " + expected + ", ottenuto " + derivative);
	}

	@Test
	@DisplayName("La derivata di una costante e' zero")
	void derivativeOfConstantIsZero() {
		Polynomial<Rational> constant = poly(7);
		Polynomial<Rational> derivative = provider.derivative(constant);
		assertTrue(derivative.equals(poly()));
	}

	@Test
	@DisplayName("Linearita': d/dx(p + q) = d/dx(p) + d/dx(q)")
	void derivativeIsLinear() {
		Polynomial<Rational> p = poly(1, 2, 3);   // 1 + 2x + 3x^2
		Polynomial<Rational> q = poly(0, -1, 5);  // -x + 5x^2

		Polynomial<Rational> derivativeOfSum = provider.derivative(ring.add(p, q));
		Polynomial<Rational> sumOfDerivatives = ring.add(provider.derivative(p), provider.derivative(q));

		assertTrue(derivativeOfSum.equals(sumOfDerivatives));
	}
}
