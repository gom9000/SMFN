package net.gommagomma.smfn.math.core.algebra.structures;


import net.gommagomma.smfn.math.core.algebra.AlgebraicStructure;
import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.MultiplicativeMonoidElement;


public interface MultiplicativeMonoid<E extends MultiplicativeMonoidElement<E>>
extends AlgebraicStructure<E>
{
    E multiplicativeIdentity();
}
