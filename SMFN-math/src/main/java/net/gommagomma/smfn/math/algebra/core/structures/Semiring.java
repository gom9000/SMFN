package net.gommagomma.smfn.math.algebra.core.structures;


import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;


public interface Semiring<E extends SemiringElement<E>>
extends AdditiveMonoid<E>, MultiplicativeMonoid<E>
{}
