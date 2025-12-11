package net.gommagomma.smfn.math.linearalgebra.real;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.HilbertSpace;


public final class RealVectorSpace
implements HilbertSpace<Real, RealVector>
{
    private static final RealVectorSpace INSTANCE = new RealVectorSpace();

    private RealVectorSpace() { /* singleton */ }

    public static RealVectorSpace getInstance()
    {
        return INSTANCE;
    }

    @Override
    public Field<Real, ?> getScalarStructure()
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