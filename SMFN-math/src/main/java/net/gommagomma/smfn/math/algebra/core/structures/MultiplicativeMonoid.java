package net.gommagomma.smfn.math.algebra.core.structures;


import net.gommagomma.smfn.math.algebra.core.AlgebraicStructure;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.MultiplicativeMonoidElement;


public interface MultiplicativeMonoid<E extends MultiplicativeMonoidElement<E>>
extends AlgebraicStructure<E>
{
    E multiplicativeIdentity();
}
