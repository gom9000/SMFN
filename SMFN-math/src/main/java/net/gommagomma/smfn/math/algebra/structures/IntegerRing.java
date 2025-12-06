package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.numeric.SignedIntFactory;


public final class IntegerRing
implements CommutativeRing<SignedInt>
{
	public static final IntegerRing INSTANCE = new IntegerRing();


    private IntegerRing() {}
    public static IntegerRing getInstance() { return INSTANCE; }


    @Override // AlgebraicStructure impls
    public String getName()
    {
        return "Integer Ring (Z)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(SignedInt e)
    {
    	return (e != null);
    }


    @Override // AdditiveMonoid impls
    public SignedInt additiveIdentity()
    {
        return SignedIntFactory.getInstance().zero();
    }


    @Override // MultiplicativeMonoid impls
    public SignedInt multiplicativeIdentity()
    {
        return SignedIntFactory.getInstance().one();
    }
}
