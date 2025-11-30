package net.gommagomma.smfn.math.linearalgebra.signedint;

import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.Module;


public class SignedIntModule
implements Module<SignedInt, SignedIntVector>
{
    private static final SignedIntModule INSTANCE = new SignedIntModule();

    private SignedIntModule() {}

    public static SignedIntModule getInstance() {
        return INSTANCE;
    }

    @Override
    public Ring<SignedInt> getScalarStructure() {
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
