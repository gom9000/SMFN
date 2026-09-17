package net.gommagomma.smfn.math.algebra.core.structures.contracts;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;

/**
 * Contratto d'assioma per un Field<E>: eredita tutti gli assiomi di anello
 * commutativo e aggiunge l'inverso moltiplicativo per ogni elemento non nullo.
 *
 * a() e b() forniti dalla sottoclasse devono essere entrambi non nulli.
 */
public abstract class FieldAxiomContract<E extends ScalarElement<E>>
extends CommutativeRingAxiomContract<E>
{
	@Override
	protected abstract Field<E> structure();

	@Nested
	@DisplayName("Assioma moltiplicativo aggiuntivo (Gruppo)")
	class MultiplicativeGroupAxiom
	{
		@Test
		@DisplayName("Inverso moltiplicativo: a * a^-1 = 1")
		void multiplicativeInverse() {
			E one = structure().one();
			assertTrue(structure().areEqual(structure().multiply(a(), structure().inverse(a())), one));
			assertTrue(structure().areEqual(structure().multiply(structure().inverse(a()), a()), one));
		}

		@Test
		@DisplayName("Divisione coerente con inverse: a / b = a * b^-1")
		void divisionConsistentWithInverse() {
			E viaDivide = structure().divide(a(), b());
			E viaInverse = structure().multiply(a(), structure().inverse(b()));
			assertTrue(structure().areEqual(viaDivide, viaInverse));
		}

		@Test
		@DisplayName("Zero non ha inverso")
		void zeroHasNoInverse() {
			assertThrows(ArithmeticException.class, () -> structure().inverse(structure().zero()));
		}

		@Test
		@DisplayName("Divisione per zero non  definita")
		void divisionByZeroIsUndefined() {
			assertThrows(ArithmeticException.class, () -> structure().divide(a(), structure().zero()));
		}
	}
}
