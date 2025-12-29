package net.gommagomma.smfn.math.linearalgebra.core.structures.spaces; 

import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

public class RealMetricSpace
implements MetricSpace<Real, Real>
{    
    private final String name;

    public RealMetricSpace(String name) {
        this.name = name;
    }

    @Override
    public Real distance(Real p1, Real p2) {
        return p1.subtract(p2).norm();
    }

    @Override
    public Semiring<Real> getScalarStructure() {
        return RealField.getInstance(); 
    }

    @Override
    public String getName() { return name; }

    @Override
    public boolean contains(Real e) { return true; }
}
