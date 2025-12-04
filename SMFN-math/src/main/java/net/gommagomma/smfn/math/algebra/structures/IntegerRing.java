package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.numeric.SignedInt;


public final class IntegerRing
implements CommutativeRing<SignedInt>
{
	public static final IntegerRing INSTANCE = new IntegerRing();

    private IntegerRing() {}

    public static IntegerRing getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName()
    {
        return "Integer Ring (Z)";
    }


    @Override
    public boolean contains(SignedInt e)
    {
    	return (e != null);
    }


    @Override
    public SignedInt additiveIdentity()
    {
        return SignedInt.ZERO;
    }


    @Override
    public SignedInt multiplicativeIdentity()
    {
        return SignedInt.ONE;
    }
}
