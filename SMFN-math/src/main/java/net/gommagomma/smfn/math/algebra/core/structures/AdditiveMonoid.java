package net.gommagomma.smfn.math.algebra.core.structures;


import net.gommagomma.smfn.math.algebra.core.AlgebraicStructure;
import net.gommagomma.smfn.math.algebra.core.elements.additive.AdditiveMonoidElement;


public interface AdditiveMonoid<E extends AdditiveMonoidElement<E>>
extends AlgebraicStructure<E>
{
    E additiveIdentity();
}
