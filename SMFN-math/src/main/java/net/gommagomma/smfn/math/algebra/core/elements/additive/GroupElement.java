package net.gommagomma.smfn.math.algebra.core.elements.additive;

public interface GroupElement<E extends GroupElement<E>>
extends AdditiveMonoidElement<E>
{
	E negate(); 
	default E subtract(E other) {
		return add(other.negate());
	}
}
