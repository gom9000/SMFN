package net.gommagomma.smfn.math.core.algebra.structures;


import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.SemiringElement;


public interface Semiring<E extends SemiringElement<E>>
extends AdditiveMonoid<E>, MultiplicativeMonoid<E>
{}
