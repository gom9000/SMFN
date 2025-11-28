package net.gommagomma.smfn.math.core.algebra.structures;


import net.gommagomma.smfn.math.core.algebra.AlgebraicStructure;
import net.gommagomma.smfn.math.core.algebra.elements.additive.AdditiveMonoidElement;


public interface AdditiveMonoid<E extends AdditiveMonoidElement<E>>
extends AlgebraicStructure<E>
{
    E additiveIdentity();
}
