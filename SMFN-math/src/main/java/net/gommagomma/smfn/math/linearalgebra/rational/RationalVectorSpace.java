package net.gommagomma.smfn.math.linearalgebra.rational;

import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.core.algebra.numeric.Rational;
import net.gommagomma.smfn.math.core.linearalgebra.structures.VectorSpace;

public final class RationalVectorSpace
implements VectorSpace<Rational, RationalVector>
{    
    private static final RationalVectorSpace INSTANCE = new RationalVectorSpace();

    private RationalVectorSpace() {
        // Costruttore privato per il singleton
    }

    public static RationalVectorSpace getInstance() {
        return INSTANCE;
    }

    @Override
    public RationalField getScalarRing() {
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
