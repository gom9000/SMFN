package net.gommagomma.smfn.math.algebra.core.structures;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;

public interface Field<E extends AlgebraicElement<E>>
extends CommutativeRing<E>, MultiplicativeGroup<E>
{}
