package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.structures.RationalField;

/**
 * Invarianti algebrici di Rational verificati sul catalogo di RationalTestValues,
 * non su una singola tripla come RationalFieldTest (che resta com'e': verifica gli
 * assiomi di campo su a()/b()/c() fissi). Qui l'obiettivo e' l'opposto: le stesse
 * leggi, ma fatte scorrere su molti valori scelti apposta per includere i bordi,
 * cosi' che aggiungere un valore al catalogo basti a estendere automaticamente
 * ogni test qui sotto, senza doverli riscrivere uno per uno.
 * <p>
 * Dove un'operazione ha un limite rappresentazionale noto e genuino (es. negate()
 * su numeratore Long.MIN_VALUE), il test lo isola esplicitamente invece di escluderlo
 * in silenzio dal catalogo: cosi' un'eventuale regressione (l'operazione smette di
 * lanciare, o lancia dove non dovrebbe) resta visibile.
 */
@DisplayName("Rational: invarianti algebrici sul catalogo di valori (standard + estremi)")
class RationalInvariantsTest
{
	private final RationalField Q = RationalField.INSTANCE;

	@Test
	@DisplayName("Addizione commutativa: a+b == b+a, su tutte le coppie di valori standard")
	void additionIsCommutative() {
		List<Rational> values = RationalTestValues.standardValues();
		for (Rational a : values) {
			for (Rational b : values) {
				assertEquals(Q.add(a, b), Q.add(b, a), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Moltiplicazione commutativa: a*b == b*a, su tutte le coppie di valori standard")
	void multiplicationIsCommutative() {
		List<Rational> values = RationalTestValues.standardValues();
		for (Rational a : values) {
			for (Rational b : values) {
				assertEquals(Q.multiply(a, b), Q.multiply(b, a), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Addizione associativa: (a+b)+c == a+(b+c), su tutte le terne di valori standard")
	void additionIsAssociative() {
		List<Rational> values = RationalTestValues.standardValues();
		for (Rational a : values) {
			for (Rational b : values) {
				for (Rational c : values) {
					Rational left = Q.add(Q.add(a, b), c);
					Rational right = Q.add(a, Q.add(b, c));
					assertEquals(left, right, "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Moltiplicazione associativa: (a*b)*c == a*(b*c), su tutte le terne di valori standard")
	void multiplicationIsAssociative() {
		List<Rational> values = RationalTestValues.standardValues();
		for (Rational a : values) {
			for (Rational b : values) {
				for (Rational c : values) {
					Rational left = Q.multiply(Q.multiply(a, b), c);
					Rational right = Q.multiply(a, Q.multiply(b, c));
					assertEquals(left, right, "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Distributiva: a*(b+c) == a*b + a*c, su tutte le terne di valori standard")
	void multiplicationDistributesOverAddition() {
		List<Rational> values = RationalTestValues.standardValues();
		for (Rational a : values) {
			for (Rational b : values) {
				for (Rational c : values) {
					Rational left = Q.multiply(a, Q.add(b, c));
					Rational right = Q.add(Q.multiply(a, b), Q.multiply(a, c));
					assertEquals(left, right, "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Zero e' identita' additiva su tutto il catalogo, estremi inclusi")
	void zeroIsAdditiveIdentity() {
		for (Rational a : RationalTestValues.allValues()) {
			assertEquals(a, Q.add(a, Q.zero()), "a=" + a);
			assertEquals(a, Q.add(Q.zero(), a), "a=" + a);
		}
	}

	@Test
	@DisplayName("Uno e' identita' moltiplicativa su tutto il catalogo, estremi inclusi")
	void oneIsMultiplicativeIdentity() {
		for (Rational a : RationalTestValues.allValues()) {
			assertEquals(a, Q.multiply(a, Q.one()), "a=" + a);
			assertEquals(a, Q.multiply(Q.one(), a), "a=" + a);
		}
	}

	@Test
	@DisplayName("Inverso additivo: a + (-a) == 0, su valori a magnitudine standard (add() ha un proprio contratto di overflow a parte, verificato altrove)")
	void additiveInverseReturnsZero() {
		for (Rational a : RationalTestValues.standardValues()) {
			Rational sum = Q.add(a, Q.negate(a));
			assertEquals(Q.zero(), sum, "a=" + a + " negate(a)=" + Q.negate(a));
		}
	}

	@Test
	@DisplayName("negate() nega esattamente il numeratore lasciando il denominatore invariato, per ogni valore rappresentabile del catalogo")
	void negateFlipsNumeratorSignExactly() {
		// Verifica diretta sulla definizione di negate(), senza passare da add(): a+(-a)==0
		// dipende anche dal contratto di overflow di add() (denominatori che si moltiplicano
		// tra loro), che e' un limite separato gia' verificato altrove -- qui si isola la sola
		// correttezza di negate() in se'.
		for (Rational a : RationalTestValues.valuesSafeForNegationAndAbs()) {
			Rational negated = Q.negate(a);
			assertEquals(-a.getNumerator(), negated.getNumerator(), "a=" + a);
			assertEquals(a.getDenominator(), negated.getDenominator(), "a=" + a);
		}
	}

	@Test
	@DisplayName("negate() e' involutiva: -(-a) == a, per ogni valore del catalogo dove e' rappresentabile")
	void doubleNegationReturnsOriginal() {
		for (Rational a : RationalTestValues.valuesSafeForNegationAndAbs()) {
			assertEquals(a, Q.negate(Q.negate(a)), "a=" + a);
		}
	}

	@Test
	@DisplayName("negate() deve lanciare per numeratore Long.MIN_VALUE (overflow genuino), non restituire un valore silenziosamente errato")
	void negateRejectsLongMinValueNumerator() {
		Rational a = new Rational(Long.MIN_VALUE, 1);
		assertThrows(ArithmeticException.class, () -> Q.negate(a));
	}

	@Test
	@DisplayName("Inverso moltiplicativo: a * a^-1 == 1 per ogni valore non nullo a magnitudine standard (multiply() ha un proprio contratto di overflow a parte, verificato altrove)")
	void multiplicativeInverseReturnsOne() {
		for (Rational a : RationalTestValues.standardValues()) {
			if (Q.isZero(a)) {
				continue;
			}
			Rational product = Q.multiply(a, Q.inverse(a));
			assertEquals(Q.one(), product, "a=" + a);
		}
	}

	@Test
	@DisplayName("inverse(a) e' l'esatto reciproco di a, per ogni valore del catalogo, verificato con BigInteger indipendentemente da multiply()")
	void inverseIsExactReciprocal() {
		// a*inverse(a) andrebbe verificato senza passare dalla multiply() di produzione:
		// per valori estremi (es. due coprimi enormi) il prodotto dei numeratori puo'
		// eccedere un long anche quando la frazione risultante vale esattamente 1 --
		// e' il contratto di overflow di multiply() (gia' verificato altrove), non
		// un difetto di inverse(). Qui si confrontano i prodotti incrociati con
		// BigInteger, che non ha questo limite.
		for (Rational a : RationalTestValues.allValues()) {
			if (Q.isZero(a)) {
				continue;
			}
			if (a.getNumerator() == Long.MIN_VALUE) {
				// inverse(a) = new Rational(a.getDenominator(), a.getNumerator()): il nuovo
				// denominatore sarebbe Long.MIN_VALUE, e normalizzarne il segno richiederebbe
				// negarlo -- overflow genuino, limite gia' verificato altrove sul costruttore.
				assertThrows(ArithmeticException.class, () -> Q.inverse(a));
				continue;
			}
			Rational inv = Q.inverse(a);
			java.math.BigInteger numProduct = java.math.BigInteger.valueOf(a.getNumerator()).multiply(java.math.BigInteger.valueOf(inv.getNumerator()));
			java.math.BigInteger denProduct = java.math.BigInteger.valueOf(a.getDenominator()).multiply(java.math.BigInteger.valueOf(inv.getDenominator()));
			assertEquals(denProduct, numProduct, "a=" + a + " inverse(a)=" + inv);
		}
	}

	@Test
	@DisplayName("abs() non e' mai negativo, tranne il limite noto (numeratore Long.MIN_VALUE)")
	void absIsNeverNegativeExceptKnownLimit() {
		for (Rational a : RationalTestValues.allValues()) {
			if (a.getNumerator() == Long.MIN_VALUE) {
				assertThrows(ArithmeticException.class, a::abs);
				continue;
			}
			assertTrue(a.abs().signum() >= 0, "a=" + a);
		}
	}

	@Test
	@DisplayName("compareTo e' antisimmetrico su tutto il catalogo, estremi inclusi")
	void compareToIsAntisymmetric() {
		List<Rational> values = RationalTestValues.allValues();
		for (Rational a : values) {
			for (Rational b : values) {
				assertEquals(Integer.signum(a.compareTo(b)), -Integer.signum(b.compareTo(a)), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("compareTo e' riflessivo: a.compareTo(a) == 0, su tutto il catalogo")
	void compareToIsReflexive() {
		for (Rational a : RationalTestValues.allValues()) {
			assertEquals(0, a.compareTo(a), "a=" + a);
		}
	}

	@Test
	@DisplayName("isLessThan e' coerente con compareTo su tutto il catalogo, estremi inclusi")
	void isLessThanIsConsistentWithCompareTo() {
		List<Rational> values = RationalTestValues.allValues();
		for (Rational a : values) {
			for (Rational b : values) {
				assertEquals(a.compareTo(b) < 0, a.isLessThan(b), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("power(): a^0 == 1 e a^1 == a, su tutto il catalogo, estremi inclusi (regressione BUG-08: quadrato inutile della base all'ultima iterazione)")
	void powerBaseCasesHoldForWholeCatalog() {
		// BUG-08: l'algoritmo square-and-multiply elevava al quadrato la base anche
		// all'ultima iterazione, quando il quadrato non serve piu': per basi come
		// Long.MAX_VALUE/1, quel quadrato in piu' andava in overflow (ArithmeticException)
		// anche se il vero risultato di a^1 e' semplicemente a, perfettamente rappresentabile.
		for (Rational a : RationalTestValues.allValues()) {
			assertEquals(Q.one(), a.power(0), "a=" + a + " a^0");
			assertEquals(a, a.power(1), "a=" + a + " a^1");
		}
	}

	@Test
	@DisplayName("power(): a^n coincide con la moltiplicazione ripetuta, per esponenti piccoli su valori standard")
	void powerMatchesRepeatedMultiplicationForSmallExponents() {
		for (Rational a : RationalTestValues.standardValues()) {
			Rational expected = Q.one();
			for (int exp = 0; exp <= 4; exp++) {
				assertEquals(expected, a.power(exp), "a=" + a + " exp=" + exp);
				expected = Q.multiply(expected, a);
			}
		}
	}
}
