package net.gommagomma.smfn.math.algebra.core.structures.metric;

import net.gommagomma.smfn.math.algebra.numerics.Real;

public interface MetricSpace<E>
{
    Real distance(E a, E b);
}
