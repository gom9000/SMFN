package net.gommagomma.smfn.math.core.algebra.elements.multiplicative;


import net.gommagomma.smfn.math.core.algebra.AlgebraicElement;


//Base per strutture con addizione e moltiplicazione (semianelli e anelli)
public interface MultiplicativeMonoidElement<E extends MultiplicativeMonoidElement<E>>
extends AlgebraicElement<E>
{
	E multiply(E other);
	E getOne();
	default boolean isOne() { return isEqual(getOne()); }
}
