package net.gommagomma.smfn.math.algebra.core.structures;


import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.RingElement;


public interface Ring<E extends RingElement<E>>
extends Semiring<E>, AbelianGroup<E>
{}
