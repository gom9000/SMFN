package net.gommagomma.smfn.math.linearalgebra.rational;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.InnerProductSpace;

public final class RationalVectorSpace
implements InnerProductSpace<Rational, RationalVector>
{    
    private static final RationalVectorSpace INSTANCE = new RationalVectorSpace();

    private RationalVectorSpace() {}

    public static RationalVectorSpace getInstance() {
        return INSTANCE;
    }

    @Override
    public Field<Rational> getScalarStructure() {
        return RationalField.getInstance();
    }

    @Override
    public String getName() {
        return "Rational Vector Space (Q^n)";
    }

    @Override
    public boolean contains(RationalVector v) {
        return (v != null);
    }
}
