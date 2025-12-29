package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface Ring<E extends AlgebraicElement<E>>
extends Semiring<E>, AbelianGroup<E>
{}
