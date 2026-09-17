package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;

@DisplayName("Natural: elemento (costruttore, Orderable, Exponentiable)")
class NaturalTest
{
	private final NaturalSemiring N = NaturalSemiring.INSTANCE;
	private final Natural n5 = new Natural(5);
	private final Natural n10 = new Natural(10);

	@Test
	@DisplayName("Costruttore rifiuta valori negativi")
	void constructorRejectsNegative() {
		assertThrows(IllegalArgumentException.class, () -> new Natural(-1));
	}

	@Test
	@DisplayName("Getter")
	void getter() {
		assertEquals(10, n10.getValue());
	}

	@Test
	@DisplayName("copy() restituisce una nuova istanza con lo stesso valore")
	void copy() {
		Natural copy = n5.copy();
		assertEquals(n5, copy);
		assertNotSame(n5, copy);
	}

	@Test
	@DisplayName("Orderable: compareTo/isLessThan")
	void orderable() {
		assertTrue(n10.compareTo(n5) > 0);
		assertTrue(n5.compareTo(n10) < 0);
		assertTrue(n5.compareTo(new Natural(5)) == 0);
		assertTrue(n5.isLessThan(n10));
	}

	@Test
	@DisplayName("Exponentiable: potenza")
	void power() {
		assertEquals(125, n5.power(3).getValue());
		assertEquals(1, n5.power(0).getValue());
		assertEquals(0, N.zero().power(5).getValue());
		assertEquals(1, N.one().power(100).getValue());
	}

	@Test
	@DisplayName("Potenza con esponente negativo lancia eccezione")
	void powerNegativeExponent() {
		assertThrows(ArithmeticException.class, () -> n5.power(-1));
	}

	@Test
	@DisplayName("Potenza in overflow lancia eccezione")
	void powerOverflow() {
		Natural largeBase = new Natural(1_000_000_000_000_000_000L);
		assertThrows(ArithmeticException.class, () -> largeBase.power(3));
	}

	@Test
	@DisplayName("Struttura: addizione/moltiplicazione in overflow lanciano eccezione")
	void structureOverflow() {
		Natural max = new Natural(Long.MAX_VALUE);
		assertThrows(ArithmeticException.class, () -> N.add(max, N.one()));
		assertThrows(ArithmeticException.class, () -> N.multiply(max, new Natural(2)));
	}

	@Test
	@DisplayName("equals/hashCode/toString")
	void standardMethods() {
		assertEquals(new Natural(5), n5);
		assertEquals(new Natural(5).hashCode(), n5.hashCode());
		assertEquals("5", n5.toString());
		assertEquals(String.valueOf(Long.MAX_VALUE), new Natural(Long.MAX_VALUE).toString());
	}
}
