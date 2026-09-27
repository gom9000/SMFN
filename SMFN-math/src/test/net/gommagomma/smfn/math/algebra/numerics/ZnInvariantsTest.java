package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.structures.ZnRing;

/**
 * Invarianti algebrici di ZnElement/ZnRing sul catalogo di ZnTestValues, sullo stesso
 * spirito di RationalInvariantsTest/SignedIntInvariantsTest.
 * <p>
 * A differenza degli altri tipi, ZnElement e' parametrizzato dal modulo: ogni test scorre
 * un gruppo alla volta ({@link ZnTestValues#standardGroupedByModulus()} o
 * {@link ZnTestValues#allGroupedByModulus()}), mai valori di moduli diversi tra loro
 * (add()/multiply() lo impedirebbero comunque, verificato esplicitamente qui sotto).
 * ZnElement non implementa {@code Orderable} (non ha senso ordinare classi resto), quindi
 * qui non ci sono test di compareTo/isLessThan.
 */
@DisplayName("ZnElement: invarianti algebrici sul catalogo di valori (moduli standard + estremi)")
class ZnInvariantsTest
{
	@Test
	@DisplayName("Addizione commutativa: a+b == b+a, per ogni coppia entro lo stesso modulo, su tutti i moduli")
	void additionIsCommutative() {
		for (List<ZnElement> group : ZnTestValues.allGroupedByModulus()) {
			ZnRing ring = ZnRing.forModulus(group.get(0).getModulus());
			for (ZnElement a : group) {
				for (ZnElement b : group) {
					assertEquals(ring.add(a, b), ring.add(b, a), "a=" + a + " b=" + b);
				}
			}
		}
	}

	@Test
	@DisplayName("Moltiplicazione commutativa: a*b == b*a, per ogni coppia entro lo stesso modulo, su tutti i moduli")
	void multiplicationIsCommutative() {
		for (List<ZnElement> group : ZnTestValues.allGroupedByModulus()) {
			ZnRing ring = ZnRing.forModulus(group.get(0).getModulus());
			for (ZnElement a : group) {
				for (ZnElement b : group) {
					assertEquals(ring.multiply(a, b), ring.multiply(b, a), "a=" + a + " b=" + b);
				}
			}
		}
	}

	@Test
	@DisplayName("Addizione associativa: (a+b)+c == a+(b+c), su terne entro lo stesso modulo, moduli standard")
	void additionIsAssociative() {
		for (List<ZnElement> group : ZnTestValues.standardGroupedByModulus()) {
			ZnRing ring = ZnRing.forModulus(group.get(0).getModulus());
			for (ZnElement a : group) {
				for (ZnElement b : group) {
					for (ZnElement c : group) {
						assertEquals(ring.add(ring.add(a, b), c), ring.add(a, ring.add(b, c)), "a=" + a + " b=" + b + " c=" + c);
					}
				}
			}
		}
	}

	@Test
	@DisplayName("Distributiva: a*(b+c) == a*b + a*c, su terne entro lo stesso modulo, moduli standard")
	void multiplicationDistributesOverAddition() {
		for (List<ZnElement> group : ZnTestValues.standardGroupedByModulus()) {
			ZnRing ring = ZnRing.forModulus(group.get(0).getModulus());
			for (ZnElement a : group) {
				for (ZnElement b : group) {
					for (ZnElement c : group) {
						assertEquals(ring.multiply(a, ring.add(b, c)), ring.add(ring.multiply(a, b), ring.multiply(a, c)), "a=" + a + " b=" + b + " c=" + c);
					}
				}
			}
		}
	}

	@Test
	@DisplayName("Zero e' identita' additiva su tutto il catalogo, moduli estremi inclusi")
	void zeroIsAdditiveIdentity() {
		for (List<ZnElement> group : ZnTestValues.allGroupedByModulus()) {
			ZnRing ring = ZnRing.forModulus(group.get(0).getModulus());
			for (ZnElement a : group) {
				assertEquals(a, ring.add(a, ring.zero()), "a=" + a);
			}
		}
	}

	@Test
	@DisplayName("Uno e' identita' moltiplicativa su tutto il catalogo, moduli estremi inclusi (compreso il caso banale modulo 1, dove uno == zero)")
	void oneIsMultiplicativeIdentity() {
		for (List<ZnElement> group : ZnTestValues.allGroupedByModulus()) {
			ZnRing ring = ZnRing.forModulus(group.get(0).getModulus());
			for (ZnElement a : group) {
				assertEquals(a, ring.multiply(a, ring.one()), "a=" + a);
			}
		}
	}

	@Test
	@DisplayName("Inverso additivo: a + (-a) == 0, su tutto il catalogo, moduli estremi inclusi")
	void additiveInverseReturnsZero() {
		for (List<ZnElement> group : ZnTestValues.allGroupedByModulus()) {
			ZnRing ring = ZnRing.forModulus(group.get(0).getModulus());
			for (ZnElement a : group) {
				assertEquals(ring.zero(), ring.add(a, ring.negate(a)), "a=" + a);
			}
		}
	}

	@Test
	@DisplayName("Ogni elemento e' normalizzato: 0 <= valore < modulo, su tutto il catalogo, moduli estremi inclusi")
	void everyElementIsNormalizedWithinModulus() {
		for (List<ZnElement> group : ZnTestValues.allGroupedByModulus()) {
			for (ZnElement a : group) {
				long v = a.getValue().getValue();
				long m = a.getModulus().getValue();
				assertTrue(v >= 0L, "a=" + a + " valore normalizzato deve essere >= 0");
				assertTrue(v < m, "a=" + a + " valore normalizzato deve essere < modulo");
			}
		}
	}

	@Test
	@DisplayName("LIMITE NOTO/per progetto: mescolare elementi di moduli diversi in add()/multiply() lancia IllegalArgumentException")
	void operationsRejectMismatchedModuli() {
		ZnElement a = new ZnElement(new SignedInt(3), new SignedInt(7));
		ZnElement b = new ZnElement(new SignedInt(3), new SignedInt(12));
		ZnRing ring7 = ZnRing.forModulus(new SignedInt(7));
		assertThrows(IllegalArgumentException.class, () -> ring7.add(a, b));
		assertThrows(IllegalArgumentException.class, () -> ring7.multiply(a, b));
	}

	@Test
	@DisplayName("Nel modulo banale (1) ogni elemento e' zero, e zero coincide con uno")
	void trivialModulusCollapsesEverythingToZero() {
		SignedInt modulus = new SignedInt(1);
		ZnRing ring = ZnRing.forModulus(modulus);
		for (ZnElement a : ZnTestValues.valuesForModulus(modulus)) {
			assertEquals(ring.zero(), a, "a=" + a);
		}
		assertEquals(ring.zero(), ring.one(), "in Z/1Z, zero e uno coincidono");
	}

	@Test
	@DisplayName("power(): a^0 == 1 e a^1 == a, su tutto il catalogo, moduli estremi inclusi")
	void powerBaseCasesHoldForWholeCatalog() {
		for (List<ZnElement> group : ZnTestValues.allGroupedByModulus()) {
			ZnRing ring = ZnRing.forModulus(group.get(0).getModulus());
			for (ZnElement a : group) {
				assertEquals(ring.one(), a.power(0), "a=" + a + " a^0");
				assertEquals(a, a.power(1), "a=" + a + " a^1");
			}
		}
	}

	@Test
	@DisplayName("power(): a^n coincide con la moltiplicazione ripetuta, per esponenti piccoli, moduli standard")
	void powerMatchesRepeatedMultiplicationForSmallExponents() {
		for (List<ZnElement> group : ZnTestValues.standardGroupedByModulus()) {
			ZnRing ring = ZnRing.forModulus(group.get(0).getModulus());
			for (ZnElement a : group) {
				ZnElement expected = ring.one();
				for (int exp = 0; exp <= 4; exp++) {
					assertEquals(expected, a.power(exp), "a=" + a + " exp=" + exp);
					expected = ring.multiply(expected, a);
				}
			}
		}
	}

	@Test
	@DisplayName("power() con esponente negativo lancia (nessun calcolo dell'inverso modulare implementato)")
	void powerRejectsNegativeExponent() {
		ZnElement a = new ZnElement(new SignedInt(3), new SignedInt(7));
		assertThrows(ArithmeticException.class, () -> a.power(-1));
	}
}
