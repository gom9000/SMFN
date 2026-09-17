package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.CommutativeRingAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.algebra.numerics.ZnElement;

@DisplayName("ZnRing: assiomi di Anello Commutativo (Z/nZ)")
class ZnRingTest extends CommutativeRingAxiomContract<ZnElement>
{
	// Modulo 7 (primo): usato per gli assiomi generici di CommutativeRing,
	// che devono valere indipendentemente dalla primalita' del modulo.
	private final ZnRing z7 = ZnRing.of(new SignedInt(7));

	private ZnElement zn(long value) {
		return z7.getElement(new SignedInt(value));
	}

	@Override
	protected CommutativeRing<ZnElement> structure() {
		return z7;
	}

	@Override
	protected ZnElement a() { return zn(2); }
	@Override
	protected ZnElement b() { return zn(3); }
	@Override
	protected ZnElement c() { return zn(5); }

	@Test
	@DisplayName("Costruttore: modulo non valido")
	void constructorInvalidModulus() {
		assertThrows(IllegalArgumentException.class, () -> ZnRing.of(new SignedInt(0)));
		assertThrows(IllegalArgumentException.class, () -> ZnRing.of(new SignedInt(-5)));
	}

	@Test
	@DisplayName("Nome e modulo")
	void nameAndModulus() {
		assertEquals("Z/7Z Ring", z7.getName());
		assertEquals(new SignedInt(7), z7.getModulus());
	}

	@Test
	@DisplayName("Factory: getElement normalizza il valore")
	void getElementFactory() {
		ZnElement result = z7.getElement(new SignedInt(10)); // 10 mod 7 = 3
		assertEquals(3L, result.getValue().getValue());
		assertEquals(new SignedInt(7), result.getModulus());
	}

	@Test
	@DisplayName("Factory: of(double/long/int)")
	void ofMethods() {
		assertEquals(zn(3), z7.of(3.0));
		assertEquals(zn(15), z7.of(15L));
		assertEquals(zn(4), z7.of(4));

		assertThrows(IllegalArgumentException.class, () -> z7.of(Double.MAX_VALUE));
		assertThrows(IllegalArgumentException.class, () -> z7.of(3.5));
	}

	// Nota: ZnRing e' final e implementa solo CommutativeRing, mai Field --
	// come per NaturalSemiring, e' una garanzia data dal compilatore in base
	// alla dichiarazione della classe, non qualcosa da verificare a runtime.
	// Quello che vale davvero la pena verificare e' la CONSEGUENZA pratica
	// di questa scelta: con un modulo non primo esistono divisori dello zero.
	@Nested
	@DisplayName("Onesta' algebrica: conseguenze del non dichiararsi Field")
	class FieldHonestyChecks
	{
		private final ZnRing z12 = ZnRing.of(new SignedInt(12)); // 12 non e' primo

		@Test
		@DisplayName("Con modulo non primo esistono divisori dello zero: nessun inverso funzionante")
		void nonPrimeModulusHasZeroDivisors() {
			// In Z/12Z, 4 * 3 = 12 = 0, pur essendo entrambi non nulli:
			// un vero campo non potrebbe avere questa proprieta'.
			ZnElement four = z12.getElement(new SignedInt(4));
			ZnElement three = z12.getElement(new SignedInt(3));
			ZnElement product = z12.multiply(four, three);

			assertTrue(z12.isZero(product), "4 * 3 deve annullarsi modulo 12, pur essendo entrambi non nulli");
			assertFalse(z12.isZero(four));
			assertFalse(z12.isZero(three));
		}
	}
}
