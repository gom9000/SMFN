package net.gommagomma.smfn.math.algebra.core.structures.contracts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public abstract class ModuleAxiomContract<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>>
extends SemimoduleAxiomContract<V, K, S>
{
	@Override
	protected abstract Module<V, K, S> structure();

	@Nested
	@DisplayName("Assioma additivo aggiuntivo su V (Gruppo Abeliano)")
	class AdditiveGroupAxiom
	{
		@Test
		@DisplayName("Inverso additivo: v + (-v) = 0")
		void additiveInverse() {
			V zero = structure().zero();
			assertTrue(structure().areEqual(structure().add(a(), structure().negate(a())), zero));
		}

		@Test
		@DisplayName("Sottrazione coerente con negate: v - w = v + (-w)")
		void subtractionConsistentWithNegate() {
			V viaSubtract = structure().subtract(a(), b());
			V viaNegate = structure().add(a(), structure().negate(b()));
			assertTrue(structure().areEqual(viaSubtract, viaNegate));
		}

		@Test
		@DisplayName("Scalare -1 coincide con negate: (-1)*v = -v")
		void negativeOneScalingMatchesNegate() {
			K minusOne = structure().getScalarStructure().negate(structure().getScalarStructure().one());
			V viaScale = structure().scale(minusOne, a());
			V viaNegate = structure().negate(a());
			assertTrue(structure().areEqual(viaScale, viaNegate));
		}
	}
}
