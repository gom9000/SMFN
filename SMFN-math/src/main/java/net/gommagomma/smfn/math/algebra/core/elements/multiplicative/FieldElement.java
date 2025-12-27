package net.gommagomma.smfn.math.algebra.core.elements.multiplicative;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Scalable;

public interface FieldElement<E extends FieldElement<E>>
extends CommutativeRingElement<E>, Scalable<E, E>
{
	E inverse();
	default E divide(E other) {
		if (other.isZero()) throw new ArithmeticException("Division by zero");
		return multiply(other.inverse());
	}

	@Override
    default E scale(E scalar) {
        return this.multiply(scalar);
    }
}
