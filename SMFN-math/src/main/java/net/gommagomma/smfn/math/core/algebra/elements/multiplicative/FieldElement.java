package net.gommagomma.smfn.math.core.algebra.elements.multiplicative;

//Campo: Anello commutativo + Inverso Moltiplicativo
public interface FieldElement<E extends FieldElement<E>>
extends CommutativeRingElement<E>
{
	E inverse();
	default E divide(E other) {
		return multiply(other.inverse());
	}
}
