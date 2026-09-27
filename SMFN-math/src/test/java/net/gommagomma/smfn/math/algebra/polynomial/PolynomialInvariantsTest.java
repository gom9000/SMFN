package net.gommagomma.smfn.math.algebra.polynomial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;

/**
 * Invarianti algebrici di Polynomial&lt;Rational&gt;/EuclideanPolynomialRing sul catalogo di
 * PolynomialTestValues, sullo stesso spirito di RationalInvariantsTest e delle altre
 * *InvariantsTest del pacchetto numerics: le stesse leggi generali, verificate sull'intero
 * catalogo invece che su una singola terna fissa (quella resta compito di
 * EuclideanPolynomialRingTest, che estende EuclideanDomainAxiomContract e non viene toccata).
 */
@DisplayName("Polynomial<Rational>: invarianti algebrici sul catalogo di valori (standard + estremi)")
class PolynomialInvariantsTest
{
	private final EuclideanPolynomialRing<Rational, RationalField> R = PolynomialTestValues.RING;
	private final RationalField Q = RationalField.INSTANCE;

	@Test
	@DisplayName("Addizione commutativa: a+b == b+a, sul catalogo sicuro per combinazione (standard + estremi strutturali)")
	void additionIsCommutative() {
		// Non su allValues(): gli estremi scalari (coefficienti Rational a magnitudine estrema)
		// possono far incappare add() nel proprio contratto di overflow (cross-moltiplicazione
		// di denominatori), gia' verificato a parte su RationalInvariantsTest -- non e' un difetto
		// di Polynomial. Vedi PolynomialTestValues.combinationSafeValues().
		List<Polynomial<Rational>> values = PolynomialTestValues.combinationSafeValues();
		for (Polynomial<Rational> a : values) {
			for (Polynomial<Rational> b : values) {
				assertTrue(R.areEqual(R.add(a, b), R.add(b, a)), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Moltiplicazione commutativa: a*b == b*a, sul catalogo sicuro per combinazione (standard + estremi strutturali)")
	void multiplicationIsCommutative() {
		List<Polynomial<Rational>> values = PolynomialTestValues.combinationSafeValues();
		for (Polynomial<Rational> a : values) {
			for (Polynomial<Rational> b : values) {
				assertTrue(R.areEqual(R.multiply(a, b), R.multiply(b, a)), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Addizione associativa: (a+b)+c == a+(b+c), su terne di valori standard")
	void additionIsAssociative() {
		List<Polynomial<Rational>> values = PolynomialTestValues.standardValues();
		for (Polynomial<Rational> a : values) {
			for (Polynomial<Rational> b : values) {
				for (Polynomial<Rational> c : values) {
					Polynomial<Rational> left = R.add(R.add(a, b), c);
					Polynomial<Rational> right = R.add(a, R.add(b, c));
					assertTrue(R.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Moltiplicazione associativa: (a*b)*c == a*(b*c), su terne di valori standard")
	void multiplicationIsAssociative() {
		List<Polynomial<Rational>> values = PolynomialTestValues.standardValues();
		for (Polynomial<Rational> a : values) {
			for (Polynomial<Rational> b : values) {
				for (Polynomial<Rational> c : values) {
					Polynomial<Rational> left = R.multiply(R.multiply(a, b), c);
					Polynomial<Rational> right = R.multiply(a, R.multiply(b, c));
					assertTrue(R.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Distributiva: a*(b+c) == a*b + a*c, su terne di valori standard")
	void multiplicationDistributesOverAddition() {
		List<Polynomial<Rational>> values = PolynomialTestValues.standardValues();
		for (Polynomial<Rational> a : values) {
			for (Polynomial<Rational> b : values) {
				for (Polynomial<Rational> c : values) {
					Polynomial<Rational> left = R.multiply(a, R.add(b, c));
					Polynomial<Rational> right = R.add(R.multiply(a, b), R.multiply(a, c));
					assertTrue(R.areEqual(left, right), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Zero e' identita' additiva su tutto il catalogo, estremi inclusi")
	void zeroIsAdditiveIdentity() {
		for (Polynomial<Rational> a : PolynomialTestValues.allValues()) {
			assertTrue(R.areEqual(a, R.add(a, R.zero())), "a=" + a);
		}
	}

	@Test
	@DisplayName("Uno e' identita' moltiplicativa su tutto il catalogo, estremi inclusi")
	void oneIsMultiplicativeIdentity() {
		for (Polynomial<Rational> a : PolynomialTestValues.allValues()) {
			assertTrue(R.areEqual(a, R.multiply(a, R.one())), "a=" + a);
		}
	}

	@Test
	@DisplayName("Inverso additivo: p + (-p) == 0, per ogni polinomio del catalogo senza coefficienti Long.MIN_VALUE (limite noto di Rational.negate())")
	void additiveInverseReturnsZero() {
		for (Polynomial<Rational> p : PolynomialTestValues.valuesSafeForNegation()) {
			assertTrue(R.areEqual(R.zero(), R.add(p, R.negate(p))), "p=" + p);
		}
	}

	@Test
	@DisplayName("Grado del prodotto: deg(a*b) == deg(a) + deg(b), per ogni coppia non nulla del catalogo sicuro per combinazione")
	void degreeOfProductIsSumOfDegreesForNonzeroFactors() {
		List<Polynomial<Rational>> values = PolynomialTestValues.nonZeroCombinationSafeValues();
		for (Polynomial<Rational> a : values) {
			for (Polynomial<Rational> b : values) {
				Polynomial<Rational> product = R.multiply(a, b);
				assertEquals(a.degree() + b.degree(), product.degree(), "a=" + a + " b=" + b + " a*b=" + product);
			}
		}
	}

	@Test
	@DisplayName("Grado della somma: deg(a+b) <= max(deg(a), deg(b)), su tutte le coppie di valori standard")
	void degreeOfSumNeverExceedsMaxDegree() {
		List<Polynomial<Rational>> values = PolynomialTestValues.standardValues();
		for (Polynomial<Rational> a : values) {
			for (Polynomial<Rational> b : values) {
				Polynomial<Rational> sum = R.add(a, b);
				assertTrue(sum.degree() <= Math.max(a.degree(), b.degree()), "a=" + a + " b=" + b + " a+b=" + sum);
			}
		}
	}

	@Test
	@DisplayName("Divisione euclidea: a == q*b + r con r == 0 oppure deg(r) < deg(b), su dividendo/divisore del catalogo sicuro per combinazione")
	void divisionSatisfiesEuclideanAlgorithm() {
		List<Polynomial<Rational>> dividends = PolynomialTestValues.combinationSafeValues();
		List<Polynomial<Rational>> divisors = PolynomialTestValues.nonZeroCombinationSafeValues();
		for (Polynomial<Rational> a : dividends) {
			for (Polynomial<Rational> b : divisors) {
				PolynomialDivisionResult<Rational> result = R.divide(a, b);
				Polynomial<Rational> reconstructed = R.add(R.multiply(result.quotient(), b), result.remainder());
				assertTrue(R.areEqual(a, reconstructed), "a=" + a + " b=" + b + " q=" + result.quotient() + " r=" + result.remainder());
				assertTrue(R.isZero(result.remainder()) || result.remainder().degree() < b.degree(),
					"a=" + a + " b=" + b + " r=" + result.remainder() + " deve avere grado < deg(b) oppure essere nullo");
			}
		}
	}

	@Test
	@DisplayName("normalize() rende monico ogni polinomio non nullo del catalogo sicuro per combinazione (coefficiente di testa == 1)")
	void normalizeMakesEveryNonZeroValueMonic() {
		// Gli estremi scalari sono esclusi: normalize() divide ogni coefficiente per quello di
		// testa, che per polyR(1/Long.MAX_VALUE) reintroduce lo stesso contratto di overflow di
		// Rational.multiply() gia' verificato a parte -- non un difetto di normalize() in se'.
		for (Polynomial<Rational> p : PolynomialTestValues.nonZeroCombinationSafeValues()) {
			Polynomial<Rational> normalized = R.normalize(p);
			assertTrue(Q.areEqual(Q.one(), normalized.getCoefficient(normalized.degree())), "p=" + p + " normalizzato=" + normalized);
		}
	}

	@Test
	@DisplayName("normalize() lascia il polinomio zero invariato")
	void normalizeLeavesZeroUnchanged() {
		assertTrue(R.areEqual(R.zero(), R.normalize(R.zero())));
	}

	@Test
	@DisplayName("getCoefficient() restituisce zero per gradi fuori range (negativi o oltre il grado), su tutto il catalogo")
	void getCoefficientOutOfRangeReturnsZero() {
		for (Polynomial<Rational> p : PolynomialTestValues.allValues()) {
			assertEquals(Q.zero(), p.getCoefficient(-1), "p=" + p);
			assertEquals(Q.zero(), p.getCoefficient(p.degree() + 5), "p=" + p);
		}
	}

	@Test
	@DisplayName("copy() e' un no-op sicuro (il polinomio e' immutabile): copy().equals(originale), su tutto il catalogo")
	void copyReturnsEquivalentPolynomial() {
		for (Polynomial<Rational> p : PolynomialTestValues.allValues()) {
			assertEquals(p, p.copy(), "p=" + p);
		}
	}
}
