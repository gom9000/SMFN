package net.gommagomma.smfn.math.algebra.core.structures.contracts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;

/**
 * Contratto d'assioma per un CommutativeRing<E>: eredita tutti gli assiomi di
 * anello e aggiunge la commutativit della moltiplicazione (che in un Ring
 * generico non  garantita: si pensi alle matrici).
 */
public abstract class CommutativeRingAxiomContract<E extends ScalarElement<E>>
extends RingAxiomContract<E>
{
	@Override
	protected abstract CommutativeRing<E> structure();

	@Test
	@DisplayName("Commutativit moltiplicativa: a * b = b * a")
	void multiplicativeCommutativity() {
		assertTrue(structure().areEqual(structure().multiply(a(), b()), structure().multiply(b(), a())));
	}
}
