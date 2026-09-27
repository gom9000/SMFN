package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;

/**
 * Invarianti algebrici di Natural/NaturalSemiring sul catalogo di NaturalTestValues,
 * sullo stesso spirito di RationalInvariantsTest/SignedIntInvariantsTest.
 * <p>
 * A differenza di SignedInt, qui non c'e' un equivalente di Long.MIN_VALUE da isolare
 * (Natural non ammette valori negativi, e non ha negate()/abs()): l'unico limite genuino
 * e' l'overflow additivo/moltiplicativo reale, gia' gestito da add()/multiply() tramite
 * Math.addExact/multiplyExact, per cui i test di chiusura restano sul catalogo standard
 * (magnitudine moderata) mentre le leggi di identita' e ordinamento vanno sul catalogo
 * completo, estremi inclusi.
 */
@DisplayName("Natural: invarianti algebrici sul catalogo di valori (standard + estremi)")
class NaturalInvariantsTest
{
	private final NaturalSemiring N = NaturalSemiring.INSTANCE;

	@Test
	@DisplayName("Addizione commutativa: a+b == b+a, su tutte le coppie di valori standard")
	void additionIsCommutative() {
		List<Natural> values = NaturalTestValues.standardValues();
		for (Natural a : values) {
			for (Natural b : values) {
				assertEquals(N.add(a, b), N.add(b, a), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Moltiplicazione commutativa: a*b == b*a, su tutte le coppie di valori standard")
	void multiplicationIsCommutative() {
		List<Natural> values = NaturalTestValues.standardValues();
		for (Natural a : values) {
			for (Natural b : values) {
				assertEquals(N.multiply(a, b), N.multiply(b, a), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("Addizione associativa: (a+b)+c == a+(b+c), su terne di valori standard")
	void additionIsAssociative() {
		List<Natural> values = NaturalTestValues.standardValues();
		for (Natural a : values) {
			for (Natural b : values) {
				for (Natural c : values) {
					assertEquals(N.add(N.add(a, b), c), N.add(a, N.add(b, c)), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Distributiva: a*(b+c) == a*b + a*c, su terne di valori standard")
	void multiplicationDistributesOverAddition() {
		List<Natural> values = NaturalTestValues.standardValues();
		for (Natural a : values) {
			for (Natural b : values) {
				for (Natural c : values) {
					assertEquals(N.multiply(a, N.add(b, c)), N.add(N.multiply(a, b), N.multiply(a, c)), "a=" + a + " b=" + b + " c=" + c);
				}
			}
		}
	}

	@Test
	@DisplayName("Zero e' identita' additiva su tutto il catalogo, estremi inclusi")
	void zeroIsAdditiveIdentity() {
		for (Natural a : NaturalTestValues.allValues()) {
			assertEquals(a, N.add(a, N.zero()), "a=" + a);
		}
	}

	@Test
	@DisplayName("Uno e' identita' moltiplicativa su tutto il catalogo, estremi inclusi")
	void oneIsMultiplicativeIdentity() {
		for (Natural a : NaturalTestValues.allValues()) {
			assertEquals(a, N.multiply(a, N.one()), "a=" + a);
		}
	}

	@Test
	@DisplayName("Zero e' l'unico elemento con isZero() vero, su tutto il catalogo, estremi inclusi")
	void zeroIsTheOnlyZeroElement() {
		for (Natural a : NaturalTestValues.allValues()) {
			assertEquals(a.getValue() == 0L, N.isZero(a), "a=" + a);
		}
	}

	@Test
	@DisplayName("compareTo e' antisimmetrico su tutto il catalogo, estremi inclusi")
	void compareToIsAntisymmetric() {
		List<Natural> values = NaturalTestValues.allValues();
		for (Natural a : values) {
			for (Natural b : values) {
				assertEquals(Integer.signum(a.compareTo(b)), -Integer.signum(b.compareTo(a)), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("compareTo e' riflessivo: a.compareTo(a) == 0, su tutto il catalogo")
	void compareToIsReflexive() {
		for (Natural a : NaturalTestValues.allValues()) {
			assertEquals(0, a.compareTo(a), "a=" + a);
		}
	}

	@Test
	@DisplayName("isLessThan e' coerente con compareTo su tutto il catalogo, estremi inclusi")
	void isLessThanIsConsistentWithCompareTo() {
		List<Natural> values = NaturalTestValues.allValues();
		for (Natural a : values) {
			for (Natural b : values) {
				assertEquals(a.compareTo(b) < 0, a.isLessThan(b), "a=" + a + " b=" + b);
			}
		}
	}

	@Test
	@DisplayName("power(): a^0 == 1 e a^1 == a, su tutto il catalogo, estremi inclusi")
	void powerBaseCasesHoldForWholeCatalog() {
		for (Natural a : NaturalTestValues.allValues()) {
			assertEquals(N.one(), a.power(0), "a=" + a + " a^0");
			assertEquals(a, a.power(1), "a=" + a + " a^1");
		}
	}

	@Test
	@DisplayName("power(): a^n coincide con la moltiplicazione ripetuta, per esponenti piccoli su valori standard")
	void powerMatchesRepeatedMultiplicationForSmallExponents() {
		for (Natural a : NaturalTestValues.standardValues()) {
			Natural expected = N.one();
			for (int exp = 0; exp <= 4; exp++) {
				assertEquals(expected, a.power(exp), "a=" + a + " exp=" + exp);
				expected = N.multiply(expected, a);
			}
		}
	}

	@Test
	@DisplayName("power() con esponente negativo lancia, tranne per base 1 (dove ogni potenza vale 1)")
	void powerRejectsNegativeExponentExceptForOne() {
		for (Natural a : NaturalTestValues.standardValues()) {
			if (a.getValue() == 1L) {
				assertEquals(N.one(), a.power(-3), "a=" + a + " (1^-3 == 1)");
			} else {
				assertThrows(ArithmeticException.class, () -> a.power(-1));
			}
		}
	}

	@Test
	@DisplayName("Nessun valore del catalogo standard/estremo e' negativo (Natural non ammette valori < 0)")
	void allCatalogValuesAreNonNegative() {
		for (Natural a : NaturalTestValues.allValues()) {
			assertTrue(a.getValue() >= 0L, "a=" + a);
		}
	}
}
