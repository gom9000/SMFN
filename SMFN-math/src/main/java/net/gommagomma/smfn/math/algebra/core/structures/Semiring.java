package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface Semiring<E extends AlgebraicElement<E>>
extends AdditiveMonoid<E>, MultiplicativeMonoid<E>
{}
