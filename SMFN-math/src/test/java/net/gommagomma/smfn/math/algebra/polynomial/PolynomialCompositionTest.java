package net.gommagomma.smfn.math.algebra.polynomial;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;

/**
 * Test "di integrazione": non verificano un singolo assioma isolato, ma
 * scenari in cui piu' capacita' di algebra.polynomial lavorano insieme --
 * esattamente come faceva PolynomialDemo a occhio, ma qui come asserzioni
 * vere. Resta tutto dentro algebra.polynomial: nessun tipo di altri package.
 */
@DisplayName("algebra.polynomial: scenari che mettono insieme piu' capacita'")
class PolynomialCompositionTest
{
	private final RationalField Q = RationalField.INSTANCE;
	private final EuclideanPolynomialRing<Rational, RationalField> innerRing = new EuclideanPolynomialRing<>(Q);

	private Polynomial<Rational> poly(long... coeffs) {
		List<Rational> list = new ArrayList<>();
		for (long c : coeffs) list.add(new Rational(c, 1));
		return new Polynomial<>(innerRing, Q, list);
	}

	@Test
	@DisplayName("Chiusura sotto composizione: Polynomial<Polynomial<Rational>> e' costruibile e si somma correttamente")
	void polynomialOfPolynomialAddition() {
		// Polynomial<Rational> implementa ScalarElement<Polynomial<Rational>>, quindi
		// puo' fare da coefficiente K per un secondo livello di Polynomial -- senza
		// nessun caso speciale, lo stesso identico meccanismo di ScalarElement.
		PolynomialRing<Polynomial<Rational>, EuclideanPolynomialRing<Rational, RationalField>> outerRing =
			new PolynomialRing<>(innerRing);

		Polynomial<Rational> innerA = poly(1, 1);   // 1 + x
		Polynomial<Rational> innerB = poly(0, 1);   // x

		// q(y) = (1+x) + x*y  -- un "polinomio di polinomi"
		Polynomial<Polynomial<Rational>> outerQ = new Polynomial<>(outerRing, innerRing, List.of(innerA, innerB));
		// r(y) = x + 1*y
		Polynomial<Polynomial<Rational>> outerR = new Polynomial<>(outerRing, innerRing, List.of(poly(0, 1), poly(1)));

		Polynomial<Polynomial<Rational>> sum = outerRing.add(outerQ, outerR);

		// coefficiente di y^0: (1+x) + x = 1 + 2x
		assertTrue(sum.getCoefficient(0).equals(poly(1, 2)));
		// coefficiente di y^1: x + 1
		assertTrue(sum.getCoefficient(1).equals(poly(1, 1)));
	}

	@Test
	@DisplayName("Chiusura sotto composizione: la moltiplicazione di Polynomial<Polynomial<Rational>> convolve correttamente")
	void polynomialOfPolynomialMultiplication() {
		PolynomialRing<Polynomial<Rational>, EuclideanPolynomialRing<Rational, RationalField>> outerRing =
			new PolynomialRing<>(innerRing);

		// q(y) = x * y   (un solo termine)
		Polynomial<Polynomial<Rational>> outerQ = new Polynomial<>(outerRing, innerRing, List.of(innerRing.zero(), poly(0, 1)));
		// r(y) = 2 + y
		Polynomial<Polynomial<Rational>> outerR = new Polynomial<>(outerRing, innerRing, List.of(poly(2), poly(1)));

		Polynomial<Polynomial<Rational>> product = outerRing.multiply(outerQ, outerR);

		// (x*y)*(2+y) = 2x*y + x*y^2
		assertTrue(product.getCoefficient(0).equals(poly())); // termine costante: 0
		assertTrue(product.getCoefficient(1).equals(poly(0, 2))); // coeff di y: 2x
		assertTrue(product.getCoefficient(2).equals(poly(0, 1))); // coeff di y^2: x
	}

	@Test
	@DisplayName("Derivazione + GCD combinati: individuare una radice ripetuta")
	void derivativeAndGcdTogetherFindRepeatedRoot() {
		// p(x) = (x-1)^2 * (x+2) = x^3 - 3x + 2 -- radice doppia in x=1
		Polynomial<Rational> p = poly(2, -3, 0, 1);

		PolynomialDifferentiationProvider<Rational, RationalField> differentiation = new PolynomialDifferentiationProvider<>(Q);
		Polynomial<Rational> derivative = differentiation.derivative(p); // 3x^2 - 3 = 3(x-1)(x+1)

		// Tecnica classica: gcd(p, p') isola i fattori ripetuti di p.
		Polynomial<Rational> gcd = innerRing.normalize(innerRing.gcd(p, derivative));

		Polynomial<Rational> expectedRepeatedFactor = poly(-1, 1); // (x - 1)
		assertTrue(gcd.equals(expectedRepeatedFactor),
			"Atteso il fattore ripetuto (x-1), ottenuto " + gcd);
	}
}
