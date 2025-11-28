package net.gommagomma.smfn.math.linearalgebra.signedint;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.linearalgebra.core.structures.Module;


public class IntegerModule
implements Module<SignedInt, SignedIntVector>
{
    private static final IntegerModule INSTANCE = new IntegerModule();

    private IntegerModule() {}

    public static IntegerModule getInstance() {
        return INSTANCE;
    }

    @Override
    public IntegerRing getScalarRing() {
        return IntegerRing.getInstance();
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
