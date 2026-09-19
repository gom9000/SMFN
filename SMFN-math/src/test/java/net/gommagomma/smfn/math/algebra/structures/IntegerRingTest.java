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

	// --- Casi limite legati a Long.MIN_VALUE ---

	@Test
	@DisplayName("quotient(Long.MIN_VALUE, -1) lancia: il risultato vero (+2^63) non sta in un long")
	void quotientRejectsMinValueDividedByMinusOne() {
		assertThrows(ArithmeticException.class,
			() -> integerRing.quotient(new SignedInt(Long.MIN_VALUE), new SignedInt(-1)));
	}

	@Test
	@DisplayName("quotient/remainder con DIVISORE = Long.MIN_VALUE restano corretti (a = q*b + r, 0<=r<|b|)")
	void quotientAndRemainderCorrectWithMinValueDivisor() {
		SignedInt a = new SignedInt(-5);
		SignedInt b = new SignedInt(Long.MIN_VALUE);

		SignedInt q = integerRing.quotient(a, b);
		SignedInt r = integerRing.remainder(a, b);

		// a = q*b + r, verificato con aritmetica esatta (BigInteger) per evitare
		// di ripetere lo stesso overflow nel test che si vuole verificare.
		java.math.BigInteger bigA = java.math.BigInteger.valueOf(a.getValue());
		java.math.BigInteger bigB = java.math.BigInteger.valueOf(b.getValue());
		java.math.BigInteger bigQ = java.math.BigInteger.valueOf(q.getValue());
		java.math.BigInteger bigR = java.math.BigInteger.valueOf(r.getValue());

		assertEquals(bigA, bigQ.multiply(bigB).add(bigR));
		assertTrue(bigR.signum() >= 0, "Il resto deve essere non negativo");
		assertTrue(bigR.compareTo(bigB.abs()) < 0, "Il resto deve essere minore in valore assoluto del divisore");
	}

	@Test
	@DisplayName("degree(Long.MIN_VALUE) lancia con un messaggio chiaro, non l'errore confuso di Natural")
	void degreeRejectsMinValueWithClearMessage() {
		ArithmeticException ex = assertThrows(ArithmeticException.class,
			() -> integerRing.degree(new SignedInt(Long.MIN_VALUE)));
		assertTrue(ex.getMessage().contains("Long.MIN_VALUE"),
			"Il messaggio deve indicare la vera causa (Long.MIN_VALUE), non un sintomo a valle");
	}
}
