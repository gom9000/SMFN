package net.gommagomma.smfn.math.algebra.core.elements.multiplicative;

public interface FieldElement<E extends FieldElement<E>>
extends CommutativeRingElement<E>
{
	E inverse();
	default E divide(E other) {
		if (other.isZero()) throw new ArithmeticException("Division by zero");
		return multiply(other.inverse());
	}
}
