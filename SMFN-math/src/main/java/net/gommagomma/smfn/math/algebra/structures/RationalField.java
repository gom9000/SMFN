package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Rational;


public final class RationalField
implements Field<Rational>
{
	public static final RationalField INSTANCE = new RationalField();

    private RationalField() {}

    public static RationalField getInstance() {
        return INSTANCE;
    }
    
	@Override
	public String getName()
	{
		return "Rational Field (Q)";
	}


	@Override
	public boolean contains(Rational e)
	{
		return (e != null);
	}


	@Override
	public Rational additiveIdentity()
	{
		return Rational.ZERO;
	}


	@Override
	public Rational multiplicativeIdentity()
	{
		return Rational.ONE;
	}
}
