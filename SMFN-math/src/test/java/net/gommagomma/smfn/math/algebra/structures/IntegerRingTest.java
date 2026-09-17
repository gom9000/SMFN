package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.EuclideanDomain;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.EuclideanDomainAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.utils.MathConstants;

@DisplayName("IntegerRing: assiomi di Dominio Euclideo (Z)")
public class IntegerRingTest extends EuclideanDomainAxiomContract<SignedInt, Natural>
{
	private final IntegerRing integerRing = IntegerRing.INSTANCE;

	@Override
	protected EuclideanDomain<SignedInt, Natural> structure() {
		return integerRing;
	}

	@Override
	protected SignedInt a() { return new SignedInt(10); }
	@Override
	protected SignedInt b() { return new SignedInt(-3); }
	@Override
	protected SignedInt c() { return new SignedInt(4); }

	@Test
	void isSingleton() {
		assertSame(IntegerRing.INSTANCE, integerRing);
	}

	@Test
	void name() {
		assertEquals("Integer Ring (Z)", integerRing.getName());
	}

	// Nota: IntegerRing e' final e implementa solo EuclideanDomain, mai Field --
	// garanzia del compilatore data dalla dichiarazione della classe, non
	// qualcosa da verificare a runtime con un instanceof (che non compilerebbe
	// nemmeno: Java lo rifiuta perche' provatamente impossibile).

	@Test
	void containsRejectsNull() {
		assertTrue(integerRing.contains(integerRing.one()));
		assertFalse(integerRing.contains(null));
	}

	@Test
	void ofFromLongAndInt() {
		assertEquals(1234567890123L, integerRing.of(1234567890123L).getValue());
		assertEquals(-42L, integerRing.of(-42).getValue());
	}

	@Test
	void ofFromDoubleValid() {
		assertEquals(5L, integerRing.of(5.0).getValue());
		assertEquals(10L, integerRing.of(10.0 + MathConstants.EPSILON / 2.0).getValue());
	}

	@Test
	void ofFromDoubleInvalid() {
		assertThrows(IllegalArgumentException.class, () -> integerRing.of(5.1));
		assertThrows(IllegalArgumentException.class, () -> integerRing.of(Double.NaN));
		assertThrows(IllegalArgumentException.class, () -> integerRing.of(Double.POSITIVE_INFINITY));
		assertThrows(IllegalArgumentException.class, () -> integerRing.of(1.0e20));
	}
}
