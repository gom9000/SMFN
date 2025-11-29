package net.gommagomma.smfn.math.core.algebra.structures;


import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;


//Un campo è un anello commutativo con inversi moltiplicativi
public interface Field<E extends FieldElement<E>>
extends CommutativeRing<E>
{
	E valueOf(double value);
}
