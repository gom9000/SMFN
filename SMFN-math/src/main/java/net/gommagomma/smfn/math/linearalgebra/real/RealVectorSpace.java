package net.gommagomma.smfn.math.linearalgebra.real;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.MetricSpace;
import net.gommagomma.smfn.math.linearalgebra.core.structures.VectorSpace;


public class RealVectorSpace
implements VectorSpace<Real, RealVector>, MetricSpace<RealVector>
{
    private static final RealVectorSpace INSTANCE = new RealVectorSpace();

    private RealVectorSpace() { /* singleton */ }

    public static RealVectorSpace getInstance()
    {
        return INSTANCE;
    }

    @Override
    public RealField getScalarRing()
    {
        return RealField.getInstance();
    }

    @Override
    public String getName() {
        return "Real Vector Space (R^n)";
    }

    @Override
    public boolean contains(RealVector v)
    {
        return true;
    }

    @Override
    public Real distance(RealVector v1, RealVector v2)
    {
        RealVector difference = v1.subtract(v2);

        return difference.norm(); 
    }
}