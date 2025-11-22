package net.gommagomma.smfn.math.algebra.core.structures;


import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;


//Un campo è un anello commutativo con inversi moltiplicativi
public interface Field<E extends FieldElement<E>>
extends CommutativeRing<E>
{}
