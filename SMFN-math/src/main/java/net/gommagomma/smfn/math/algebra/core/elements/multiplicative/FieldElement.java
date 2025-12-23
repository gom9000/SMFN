package net.gommagomma.smfn.math.algebra.core.elements.multiplicative;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ComparableElement;

public interface FieldElement<E extends FieldElement<E, N>, N extends ComparableElement<N>>
extends EuclideanDomainElement<E, N>
{
	E inverse();
	default E divide(E other) {
		if (other.isZero()) throw new ArithmeticException("Division by zero");
		return multiply(other.inverse());
	}
}
