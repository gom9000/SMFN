package net.gommagomma.smfn.math.core.algebra.structures;


import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.RingElement;


public interface Ring<E extends RingElement<E>>
extends Semiring<E>, AbelianGroup<E>
{}
