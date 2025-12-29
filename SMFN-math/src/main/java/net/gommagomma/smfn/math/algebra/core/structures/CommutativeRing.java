package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface CommutativeRing<E extends AlgebraicElement<E>>
extends Ring<E>, CommutativeMultiplicativeMonoid<E>
{}
