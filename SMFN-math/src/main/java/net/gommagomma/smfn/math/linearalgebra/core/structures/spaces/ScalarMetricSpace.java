package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces; 

import net.gommagomma.smfn.math.algebra.core.elements.additive.AbelianGroupElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.algebra.numeric.Real;

// Implementazione generica di MetricSpace per qualsiasi elemento che sia normabile e sottrattile
public class ScalarMetricSpace<T extends AbelianGroupElement<T> & NormableElement<Real, T>> 
implements MetricSpace<T>
{
    private final String name;

    public ScalarMetricSpace(String name) {
        this.name = name;
    }

    @Override
    public Real distance(T point1, T point2) {
        T difference = point1.subtract(point2);
        return difference.norm();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean contains(T e) {
        return true; 
    }
}
