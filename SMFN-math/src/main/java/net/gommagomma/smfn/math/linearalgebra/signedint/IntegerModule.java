package net.gommagomma.smfn.math.linearalgebra.signedint;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.linearalgebra.core.Module;


public class IntegerModule
implements Module<SignedIntVector, SignedInt>
{
    private static final IntegerModule INSTANCE = new IntegerModule();

    private IntegerModule() {
        // Costruttore privato per il singleton
    }

    public static IntegerModule getInstance() {
        return INSTANCE;
    }

    @Override
    public IntegerRing getScalarRing() {
        return IntegerRing.getInstance();
    }

    @Override
    public int dimension() {
        throw new UnsupportedOperationException("Specific vector instances have dimensions, the module Z^n itself is generic.");
    }

    @Override
    public String getName() {
        return "Integer Module (Z^n)";
    }

    @Override
    public boolean contains(SignedIntVector v) {
        return true;
    }
}
