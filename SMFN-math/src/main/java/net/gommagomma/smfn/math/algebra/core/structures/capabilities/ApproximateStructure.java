package net.gommagomma.smfn.math.algebra.core.structures.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.ApproximateElement;
import net.gommagomma.smfn.math.algebra.core.structures.ScalarStructure;

public interface ApproximateStructure<K extends ApproximateElement<K>>
extends ScalarStructure<K>
{
    @Override
    default boolean isExact() { return false; }

    double epsilon();
}
