package net.gommagomma.smfn.math.algebra.polynomial;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.EuclideanDomain;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.EuclideanDomainAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;

@DisplayName("EuclideanPolynomialRing<Rational>: assiomi di Dominio Euclideo")
class EuclideanPolynomialRingTest extends EuclideanDomainAxiomContract<Polynomial<Rational>, Natural>
{
	private final RationalField Q = RationalField.INSTANCE;
	private final EuclideanPolynomialRing<Rational, RationalField> ring = new EuclideanPolynomialRing<>(Q);

	private Polynomial<Rational> poly(long... coeffs) {
		List<Rational> list = new java.util.ArrayList<>();
		for (long c : coeffs) list.add(new Rational(c, 1));
		return new Polynomial<>(ring, Q, list);
	}

	@Override
	protected EuclideanDomain<Polynomial<Rational>, Natural> structure() {
		return ring;
	}

	@Override
	protected Polynomial<Rational> a() { return poly(-1, 0, 1); } // x^2 - 1
	@Override
	protected Polynomial<Rational> b() { return poly(1, -2, 1); } // x^2 - 2x + 1 = (x-1)^2
	@Override
	protected Polynomial<Rational> c() { return poly(0, 1); }     // x

	@Test
	@DisplayName("GCD((x^2-1), (x-1)^2) = (x-1), normalizzato monico")
	void gcdOfSharedFactor() {
		// A(x) = x^2 - 1 = (x-1)(x+1)
		Polynomial<Rational> A = poly(-1, 0, 1);
		// B(x) = x^2 - 2x + 1 = (x-1)^2
		Polynomial<Rational> B = poly(1, -2, 1);

		Polynomial<Rational> gcd = ring.gcd(A, B);
		Polynomial<Rational> normalized = ring.normalize(gcd);

		// Atteso: (x - 1), gia' monico
		Polynomial<Rational> expected = poly(-1, 1);
		assertTrue(normalized.equals(expected), "GCD atteso (x-1), ottenuto " + normalized);
	}

	@Test
	@DisplayName("normalize() rende monico il polinomio (divide per il coefficiente di testa)")
	void normalizeMakesMonic() {
		Polynomial<Rational> nonMonic = poly(4, 2); // 4 + 2x, coefficiente di testa 2
		Polynomial<Rational> normalized = ring.normalize(nonMonic);
		assertTrue(Q.areEqual(normalized.getCoefficient(normalized.degree()), Q.one()));
		// 4 + 2x normalizzato -> 2 + x (diviso per 2)
		assertTrue(normalized.equals(poly(2, 1)));
	}
}
