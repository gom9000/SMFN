package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.numeric.NaturalFactory;


public final class NaturalSemiring
implements Semiring<Natural>
{
	public static final NaturalSemiring INSTANCE = new NaturalSemiring();


    private NaturalSemiring() {}
    public static NaturalSemiring getInstance() { return INSTANCE; }


    @Override // AlgebraicStructure impls
    public String getName()
    {
        return "Natural Semiring (N)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(Natural e)
    {
    	return (e != null);
    }


    @Override // AdditiveMonoid impls
    public Natural additiveIdentity()
    {
        return NaturalFactory.getInstance().zero();
    }


    @Override // MultiplicativeMonoid impls
    public Natural multiplicativeIdentity()
    {
        return NaturalFactory.getInstance().one();
    }
}
