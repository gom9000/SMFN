package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.FieldAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Rational;

@DisplayName("RationalField: assiomi di Campo (Q)")
public class RationalFieldTest extends FieldAxiomContract<Rational>
{
	private final RationalField rationalField = RationalField.INSTANCE;

	@Override
	protected Field<Rational> structure() {
		return rationalField;
	}

	@Override
	protected Rational a() { return new Rational(3, 4); }
	@Override
	protected Rational b() { return new Rational(-1, 2); }
	@Override
	protected Rational c() { return new Rational(2, 3); }

	@Test
	void isSingleton() {
		assertSame(RationalField.INSTANCE, rationalField);
	}

	@Test
	void name() {
		assertEquals("Rational Field (Q)", rationalField.getName());
	}

	@Test
	void containsRejectsNull() {
		assertTrue(rationalField.contains(new Rational(1, 2)));
		assertFalse(rationalField.contains(null));
	}

	@Test
	void ofFromLongAndInt() {
		assertEquals(new Rational(123, 1), rationalField.of(123L));
		assertEquals(new Rational(-42, 1), rationalField.of(-42));
	}

	@Test
	void ofFromDoubleIntegerValues() {
		assertEquals(new Rational(10, 1), rationalField.of(10.0));
		assertEquals(new Rational(1234567890123L, 1), rationalField.of(1234567890123.0));
		assertEquals(rationalField.zero(), rationalField.of(0.0));
	}

	@Test
	void ofFromDoubleFractionalValues() {
		assertEquals(new Rational(1, 2), rationalField.of(0.5));
		assertEquals(new Rational(3, 4), rationalField.of(0.75));
		assertEquals(new Rational(-1, 8), rationalField.of(-0.125));

		// 0.1 (decimale) non e' rappresentabile esattamente in binario: verifichiamo
		// che venga preservata la rappresentazione IEEE 754 esatta, non un arrotondamento decimale.
		Rational r_0_1 = rationalField.of(0.1);
		assertEquals(3602879701896397L, r_0_1.getNumerator());
		assertEquals(36028797018963968L, r_0_1.getDenominator());
	}

	@Test
	void ofFromDoubleInvalid() {
		assertThrows(IllegalArgumentException.class, () -> rationalField.of(Double.NaN));
		assertThrows(IllegalArgumentException.class, () -> rationalField.of(Double.POSITIVE_INFINITY));
		assertThrows(ArithmeticException.class, () -> rationalField.of(Double.MIN_VALUE));
	}
}
