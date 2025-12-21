package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;

public interface HasScalarStructure<K extends SemiringElement<K>>
{
    Semiring<K> getScalarStructure();
}