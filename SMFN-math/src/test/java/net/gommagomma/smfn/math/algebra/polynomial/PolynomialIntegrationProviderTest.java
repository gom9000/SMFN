package net.gommagomma.smfn.math.algebra.polynomial;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;

@DisplayName("PolynomialIntegrationProvider: integrale simbolico")
class PolynomialIntegrationProviderTest
{
	private final RationalField Q = RationalField.INSTANCE;
	private final EuclideanPolynomialRing<Rational, RationalField> ring = new EuclideanPolynomialRing<>(Q);
	private final PolynomialIntegrationProvider<Rational, RationalField> integrationProvider = new PolynomialIntegrationProvider<>(Q);
	private final PolynomialDifferentiationProvider<Rational, RationalField> differentiationProvider = new PolynomialDifferentiationProvider<>(Q);

	private Polynomial<Rational> poly(long... coeffs) {
		List<Rational> list = new java.util.ArrayList<>();
		for (long c : coeffs) list.add(new Rational(c, 1));
		return new Polynomial<>(ring, Q, list);
	}

	@Test
	@DisplayName("Integrale(2x, costante=0) = x^2")
	void integralOfLinear() {
		Polynomial<Rational> p = poly(0, 2); // 2x
		Polynomial<Rational> expected = poly(0, 0, 1); // x^2

		Polynomial<Rational> integral = integrationProvider.integrate(p, Q.zero());
		assertTrue(integral.equals(expected), "Atteso " + expected + ", ottenuto " + integral);
	}

	@Test
	@DisplayName("La costante di integrazione diventa il termine noto")
	void integrationConstantBecomesConstantTerm() {
		Polynomial<Rational> p = poly(0, 2); // 2x
		Rational constant = new Rational(5, 1);

		Polynomial<Rational> integral = integrationProvider.integrate(p, constant);
		assertTrue(Q.areEqual(integral.getCoefficient(0), constant));
	}

	@Test
	@DisplayName("Teorema fondamentale: d/dx(integrale(p)) = p, per ogni costante di integrazione")
	void fundamentalTheoremRoundTrip() {
		Polynomial<Rational> p = poly(3, -1, 4); // 3 - x + 4x^2

		for (long constantValue : new long[] {0, 1, -7}) {
			Rational constant = new Rational(constantValue, 1);
			Polynomial<Rational> integral = integrationProvider.integrate(p, constant);
			Polynomial<Rational> backToDerivative = differentiationProvider.derivative(integral);

			assertTrue(backToDerivative.equals(p),
				"d/dx(integrale(p, " + constant + ")) deve tornare a p; ottenuto " + backToDerivative);
		}
	}
}
