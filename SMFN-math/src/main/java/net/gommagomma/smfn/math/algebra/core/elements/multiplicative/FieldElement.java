package net.gommagomma.smfn.math.algebra.core.elements.multiplicative;

public interface FieldElement<E extends FieldElement<E>>
extends CommutativeRingElement<E>
{
	E inverse();
	default E divide(E other) {
		return multiply(other.inverse());
	}
}
