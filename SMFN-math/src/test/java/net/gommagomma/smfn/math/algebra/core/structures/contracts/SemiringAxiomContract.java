package net.gommagomma.smfn.math.algebra.core.structures.contracts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

/**
 * Contratto d'assioma per un Semiring<E>: chi lo estende eredita automaticamente
 * la verifica di tutti gli assiomi di semianello, senza doverli riscrivere.
 *
 * Ogni sottoclasse deve fornire la struttura da verificare e tre elementi
 * campione (a, b, c) tra loro distinti, diversi da zero e da uno, a meno
 * che la struttura stessa non lo garantisca (es. Natural).
 *
 * Le operazioni e l'uguaglianza passano sempre attraverso structure(), mai
 * come metodo d'istanza sull'elemento: gli elementi di questa libreria sono
 * puramente structure-centric (Natural, Polynomial, ...) e non portano ne'
 * operazioni ne' un confronto matematico proprio.
 */
public abstract class SemiringAxiomContract<E extends ScalarElement<E>>
{
	protected abstract Semiring<E> structure();
	protected abstract E a();
	protected abstract E b();
	protected abstract E c();

	@Nested
	@DisplayName("Assiomi additivi (Monoide)")
	class AdditiveMonoidAxioms
	{
		@Test
		@DisplayName("Chiusura")
		void closure() {
			assertTrue(structure().contains(structure().add(a(), b())));
		}

		@Test
		@DisplayName("Associativit")
		void associativity() {
			E left = structure().add(structure().add(a(), b()), c());
			E right = structure().add(a(), structure().add(b(), c()));
			assertTrue(structure().areEqual(left, right));
		}

		@Test
		@DisplayName("Identit (zero)")
		void identity() {
			E zero = structure().zero();
			assertTrue(structure().areEqual(structure().add(a(), zero), a()));
			assertTrue(structure().areEqual(structure().add(zero, a()), a()));
		}

		@Test
		@DisplayName("Commutativit")
		void commutativity() {
			assertTrue(structure().areEqual(structure().add(a(), b()), structure().add(b(), a())));
		}
	}

	@Nested
	@DisplayName("Assiomi moltiplicativi (Monoide)")
	class MultiplicativeMonoidAxioms
	{
		@Test
		@DisplayName("Chiusura")
		void closure() {
			assertTrue(structure().contains(structure().multiply(a(), b())));
		}

		@Test
		@DisplayName("Associativit")
		void associativity() {
			E left = structure().multiply(structure().multiply(a(), b()), c());
			E right = structure().multiply(a(), structure().multiply(b(), c()));
			assertTrue(structure().areEqual(left, right));
		}

		@Test
		@DisplayName("Identit (uno)")
		void identity() {
			E one = structure().one();
			assertTrue(structure().areEqual(structure().multiply(a(), one), a()));
			assertTrue(structure().areEqual(structure().multiply(one, a()), a()));
		}
	}

	@Nested
	@DisplayName("Assiomi di collegamento")
	class LinkageAxioms
	{
		@Test
		@DisplayName("Distributivit della moltiplicazione sull'addizione")
		void distributivity() {
			E left = structure().multiply(a(), structure().add(b(), c()));
			E right = structure().add(structure().multiply(a(), b()), structure().multiply(a(), c()));
			assertTrue(structure().areEqual(left, right));
		}

		@Test
		@DisplayName("Assorbimento dello zero")
		void zeroAbsorption() {
			E zero = structure().zero();
			assertTrue(structure().areEqual(structure().multiply(a(), zero), zero));
			assertTrue(structure().areEqual(structure().multiply(zero, a()), zero));
		}
	}

	@Test
	@DisplayName("contains() rifiuta null")
	void containsRejectsNull() {
		assertTrue(!structure().contains(null));
	}

	@Test
	@DisplayName("isZero()/isOne() coerenti con zero()/one()")
	void isZeroIsOneConsistency() {
		assertTrue(structure().isZero(structure().zero()));
		assertTrue(structure().isOne(structure().one()));
	}

	@Test
	@DisplayName("Il nome della struttura non  vuoto")
	void nameIsNotBlank() {
		assertTrue(structure().getName() != null && !structure().getName().isBlank());
	}
}
