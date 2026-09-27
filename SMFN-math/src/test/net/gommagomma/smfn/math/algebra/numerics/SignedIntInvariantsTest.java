package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.structures.IntegerRing;

/**
 * Invarianti algebrici di SignedInt/IntegerRing sul catalogo di SignedIntTestValues,
 * sullo stesso spirito di RationalInvariantsTest. Il catalogo estremo qui include
 * proprio i valori all'origine di BUG-03 (quotient/remainder con Long.MIN_VALUE).
 */
@DisplayName("SignedInt: invarianti algebrici sul catalogo di valori (standard + estremi)")
class SignedIntInvariantsTest
{
	private final IntegerRing Z = IntegerRing.INSTANCE;

	@Test
	@DisplayName("Addizione commutativa: a+b == b+a, su tutte le coppie di valori standard")
	void additionIsCommutative() {
		List<SignedInt> values = SignedIntTestValues.standardValues();
		for (SignedInt a : values) {
			for (SignedInt b : values) {
				assertEquals(Z.add(a, b), Z.add(b, a), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Moltiplicazione commutativa: a*b == b*a, su tutte le coppie di valori standard")
	void multiplicationIsCommutative() {
		List<SignedInt> values = SignedIntTestValues.standardValues();
		for (SignedInt a : values) {
			for (SignedInt b : values) {
				assertEquals(Z.multiply(a, b), Z.multiply(b, a), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Addizione associativa: (a+b)+c == a+(b+c), su terne di valori standard")
	void additionIsAssociative() {
		List<SignedInt> values = SignedIntTestValues.standardValues();
		for (SignedInt a : values) {
			for (SignedInt b : values) {
				for (SignedInt c : values) {
					assertEquals(Z.add(Z.add(a, b), c), Z.add(a, Z.add(b, c)), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Distributiva: a*(b+c) == a*b + a*c, su terne di valori standard")
	void multiplicationDistributesOverAddition() {
		List<SignedInt> values = SignedIntTestValues.standardValues();
		for (SignedInt a : values) {
			for (SignedInt b : values) {
				for (SignedInt c : values) {
					assertEquals(Z.multiply(a, Z.add(b, c)), Z.add(Z.multiply(a, b), Z.multiply(a, c)), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Zero e' identita' additiva su tutto il catalogo, estremi inclusi")
	void zeroIsAdditiveIdentity() {
		for (SignedInt a : SignedIntTestValues.allValues()) {
			assertEquals(a, Z.add(a, Z.zero()), "a=" + a);
		}
	}

	@Test
	@DisplayName("Uno e' identita' moltiplicativa su tutto il catalogo, estremi inclusi")
	void oneIsMultiplicativeIdentity() {
		for (SignedInt a : SignedIntTestValues.allValues()) {
			assertEquals(a, Z.multiply(a, Z.one()), "a=" + a);
		}
	}

	@Test
	@DisplayName("Inverso additivo: a + (-a) == 0, per ogni valore del catalogo dove negate() e' rappresentabile")
	void additiveInverseReturnsZero() {
		for (SignedInt a : SignedIntTestValues.valuesSafeForNegationAndAbs()) {
			assertEquals(Z.zero(), Z.add(a, Z.negate(a)), "a=" + a);
		}
	}

	@Test
	@DisplayName("negate()/abs()/degree() lanciano per Long.MIN_VALUE (overflow genuino), coerentemente")
	void negateAbsDegreeRejectLongMinValue() {
		SignedInt min = new SignedInt(Long.MIN_VALUE);
		assertThrows(ArithmeticException.class, () -> Z.negate(min));
		assertThrows(ArithmeticException.class, min::abs);
		assertThrows(ArithmeticException.class, () -> Z.degree(min));
	}

	@Test
	@DisplayName("abs() non e' mai negativo, tranne il limite noto (Long.MIN_VALUE)")
	void absIsNeverNegativeExceptKnownLimit() {
		for (SignedInt a : SignedIntTestValues.valuesSafeForNegationAndAbs()) {
			assertTrue(a.abs().signum() >= 0, "a=" + a);
		}
	}

	@Test
	@DisplayName("compareTo e' antisimmetrico su tutto il catalogo, estremi inclusi")
	void compareToIsAntisymmetric() {
		List<SignedInt> values = SignedIntTestValues.allValues();
		for (SignedInt a : values) {
			for (SignedInt b : values) {
				assertEquals(Integer.signum(a.compareTo(b)), -Integer.signum(b.compareTo(a)), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("isLessThan e' coerente con compareTo su tutto il catalogo, estremi inclusi")
	void isLessThanIsConsistentWithCompareTo() {
		List<SignedInt> values = SignedIntTestValues.allValues();
		for (SignedInt a : values) {
			for (SignedInt b : values) {
				assertEquals(a.compareTo(b) < 0, a.isLessThan(b), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("quotient/remainder: a == q*b + r con 0<=r<|b|, verificato con BigInteger, su tutto il catalogo (origine di BUG-03)")
	void quotientAndRemainderSatisfyDivisionAlgorithm() {
		List<SignedInt> values = SignedIntTestValues.allValues();
		for (SignedInt a : values) {
			for (SignedInt b : values) {
				if (Z.isZero(b)) {
					continue;
				}
				if (a.getValue() == Long.MIN_VALUE && b.getValue() == -1) {
					// Long.MIN_VALUE / -1 non e' rappresentabile: limite noto, gia' verificato altrove.
					assertThrows(ArithmeticException.class, () -> Z.quotient(a, b));
					continue;
				}
				SignedInt q = Z.quotient(a, b);
				SignedInt r = Z.remainder(a, b);

				BigInteger bigA = BigInteger.valueOf(a.getValue());
				BigInteger bigB = BigInteger.valueOf(b.getValue());
				BigInteger bigQ = BigInteger.valueOf(q.getValue());
				BigInteger bigR = BigInteger.valueOf(r.getValue());

				assertEquals(bigA, bigQ.multiply(bigB).add(bigR), "a=" + a + " b=" + b + " q=" + q + " r=" + r);
				assertTrue(bigR.signum() >= 0, "a=" + a + " b=" + b + " r=" + r + " deve essere non negativo");
				assertTrue(bigR.compareTo(bigB.abs()) < 0, "a=" + a + " b=" + b + " r=" + r + " deve essere < |b|");
			}
		}
	}

	@Test
	@DisplayName("power(): a^0 == 1 e a^1 == a, su tutto il catalogo, estremi inclusi (regressione BUG-08: quadrato inutile della base all'ultima iterazione)")
	void powerBaseCasesHoldForWholeCatalog() {
		// Stesso difetto di Natural.power(): l'algoritmo square-and-multiply elevava al
		// quadrato la base anche all'ultima iterazione, quando non serve piu' -- per basi
		// come Long.MAX_VALUE, quel quadrato andava in overflow anche se a^1 == a e' banale.
		for (SignedInt a : SignedIntTestValues.allValues()) {
			assertEquals(Z.one(), a.power(0), "a=" + a + " a^0");
			assertEquals(a, a.power(1), "a=" + a + " a^1");
		}
	}

	@Test
	@DisplayName("power(): a^n coincide con la moltiplicazione ripetuta, per esponenti piccoli su valori standard")
	void powerMatchesRepeatedMultiplicationForSmallExponents() {
		for (SignedInt a : SignedIntTestValues.standardValues()) {
			SignedInt expected = Z.one();
			for (int exp = 0; exp <= 4; exp++) {
				assertEquals(expected, a.power(exp), "a=" + a + " exp=" + exp);
				expected = Z.multiply(expected, a);
			}
		}
	}
}
