package net.gommagomma.smfn.math.algebra.core.structures.contracts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;

/**
 * Contratto d'assioma per un Ring<E>: eredita tutti gli assiomi di semianello
 * e aggiunge quello che rende additivamente un gruppo abeliano (l'inverso).
 */
public abstract class RingAxiomContract<E extends ScalarElement<E>>
extends SemiringAxiomContract<E>
{
	@Override
	protected abstract Ring<E> structure();

	@Nested
	@DisplayName("Assioma additivo aggiuntivo (Gruppo Abeliano)")
	class AdditiveGroupAxiom
	{
		@Test
		@DisplayName("Inverso additivo: a + (-a) = 0")
		void additiveInverse() {
			E zero = structure().zero();
			assertTrue(structure().areEqual(structure().add(a(), structure().negate(a())), zero));
			assertTrue(structure().areEqual(structure().add(structure().negate(a()), a()), zero));
		}

		@Test
		@DisplayName("Sottrazione coerente con negate: a - b = a + (-b)")
		void subtractionConsistentWithNegate() {
			E viaSubtract = structure().subtract(a(), b());
			E viaNegate = structure().add(a(), structure().negate(b()));
			assertTrue(structure().areEqual(viaSubtract, viaNegate));
		}

		@Test
		@DisplayName("Doppia negazione: -(-a) = a")
		void doubleNegation() {
			E doubleNegated = structure().negate(structure().negate(a()));
			assertTrue(structure().areEqual(doubleNegated, a()));
		}
	}
}
