package net.gommagomma.smfn.math.core.algebra.elements.additive;

//GroupElement estende MonoidElement e aggiunge l'inverso additivo (negazione)
public interface GroupElement<E extends GroupElement<E>>
extends AdditiveMonoidElement<E>
{
	E negate(); 
	default E subtract(E other) {
		return add(other.negate());
	}
}
