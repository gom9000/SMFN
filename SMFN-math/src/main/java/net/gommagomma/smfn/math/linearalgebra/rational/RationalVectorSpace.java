package net.gommagomma.smfn.math.linearalgebra.rational;

import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.core.VectorSpace;

public final class RationalVectorSpace
implements VectorSpace<RationalVector, Rational>
{    
    private static final RationalVectorSpace INSTANCE = new RationalVectorSpace();

    private RationalVectorSpace() {
        // Costruttore privato per il singleton
    }

    public static RationalVectorSpace getInstance() {
        return INSTANCE;
    }

    @Override
    public RationalField getScalarRing() { // getScalarRing qui è in realtà un Field
        return RationalField.getInstance();
    }
    
    @Override
    public int dimension() {
        throw new UnsupportedOperationException("Specific vector instances have dimensions, the space Q^n itself is generic.");
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
