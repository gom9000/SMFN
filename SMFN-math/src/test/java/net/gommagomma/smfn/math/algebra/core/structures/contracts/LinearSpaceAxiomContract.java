package net.gommagomma.smfn.math.algebra.core.structures.contracts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import net.gommagomma.smfn.math.algebra.core.elements.LinearElement;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.LinearSpace;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public abstract class LinearSpaceAxiomContract<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
extends ModuleAxiomContract<V, K, S>
{
	@Override
	protected abstract LinearSpace<V, K, S> structure();

	protected abstract K nonZeroScalar();

	@Test
	@DisplayName("Scalare per l'inverso dello scalare torna al vettore di partenza")
	void scalingByInverseUndoesScaling() {
		K k = nonZeroScalar();
		K kInverse = structure().getScalarStructure().inverse(k);
		V scaled = structure().scale(k, a());
		V unscaled = structure().scale(kInverse, scaled);
		assertTrue(structure().areEqual(unscaled, a()));
	}
}
