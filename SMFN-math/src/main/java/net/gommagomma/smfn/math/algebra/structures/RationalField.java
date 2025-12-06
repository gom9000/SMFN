package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.numeric.RationalFactory;


public final class RationalField
implements Field<Rational>
{
	public static final RationalField INSTANCE = new RationalField();


    private RationalField() {}
    public static RationalField getInstance() { return INSTANCE; }


	@Override // AlgebraicStructure impls
	public String getName()
	{
		return "Rational Field (Q)";
	}

	@Override // AlgebraicStructure impls
	public boolean contains(Rational e)
	{
		return (e != null);
	}


	@Override // AdditiveMonoid impls
	public Rational additiveIdentity()
	{
		return RationalFactory.getInstance().zero();
	}


	@Override // MultiplicativeMonoid impls
	public Rational multiplicativeIdentity()
	{
		return RationalFactory.getInstance().one();
	}
}
