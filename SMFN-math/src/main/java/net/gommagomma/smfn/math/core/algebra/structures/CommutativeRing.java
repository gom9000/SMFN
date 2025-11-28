package net.gommagomma.smfn.math.core.algebra.structures;


import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.CommutativeRingElement;


public interface CommutativeRing<E extends CommutativeRingElement<E>>
extends Ring<E>, CommutativeMultiplicativeMonoid<E>
{}
