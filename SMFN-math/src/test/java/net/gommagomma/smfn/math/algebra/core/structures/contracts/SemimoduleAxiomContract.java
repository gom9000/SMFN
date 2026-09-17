package net.gommagomma.smfn.math.algebra.core.structures.contracts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Semimodule;

public abstract class SemimoduleAxiomContract<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
{
	protected abstract Semimodule<V, K, S> structure();
	protected abstract V a();
	protected abstract V b();
	protected abstract V c();
	protected abstract K k1();
	protected abstract K k2();

	@Nested
	@DisplayName("Assiomi additivi su V (Monoide)")
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
			V left = structure().add(structure().add(a(), b()), c());
			V right = structure().add(a(), structure().add(b(), c()));
			assertTrue(structure().areEqual(left, right));
		}

		@Test
		@DisplayName("Identit (zero)")
		void identity() {
			V zero = structure().zero();
			assertTrue(structure().areEqual(structure().add(a(), zero), a()));
		}

		@Test
		@DisplayName("Commutativit")
		void commutativity() {
			assertTrue(structure().areEqual(structure().add(a(), b()), structure().add(b(), a())));
		}
	}

	@Nested
	@DisplayName("Assiomi dell'azione dello scalare")
	class ScalarActionAxioms
	{
		@Test
		@DisplayName("Identit: 1*v = v")
		void scalarIdentity() {
			K one = structure().getScalarStructure().one();
			assertTrue(structure().areEqual(structure().scale(one, a()), a()));
		}

		@Test
		@DisplayName("Compatibilit: k1*(k2*v) = (k1*k2)*v")
		void scalarCompatibility() {
			V left = structure().scale(k1(), structure().scale(k2(), a()));
			V right = structure().scale(structure().getScalarStructure().multiply(k1(), k2()), a());
			assertTrue(structure().areEqual(left, right));
		}

		@Test
		@DisplayName("Distributivit sull'addizione vettoriale: k*(v+w) = k*v + k*w")
		void distributesOverVectorAddition() {
			V left = structure().scale(k1(), structure().add(a(), b()));
			V right = structure().add(structure().scale(k1(), a()), structure().scale(k1(), b()));
			assertTrue(structure().areEqual(left, right));
		}

		@Test
		@DisplayName("Distributivit sull'addizione scalare: (k1+k2)*v = k1*v + k2*v")
		void distributesOverScalarAddition() {
			K sum = structure().getScalarStructure().add(k1(), k2());
			V left = structure().scale(sum, a());
			V right = structure().add(structure().scale(k1(), a()), structure().scale(k2(), a()));
			assertTrue(structure().areEqual(left, right));
		}

		@Test
		@DisplayName("Scalare zero annulla il vettore: 0*v = 0")
		void zeroScalarAnnihilates() {
			K zeroScalar = structure().getScalarStructure().zero();
			assertTrue(structure().areEqual(structure().scale(zeroScalar, a()), structure().zero()));
		}
	}

	@Test
	@DisplayName("Il nome della struttura non  vuoto")
	void nameIsNotBlank() {
		assertTrue(structure().getName() != null && !structure().getName().isBlank());
	}
}
