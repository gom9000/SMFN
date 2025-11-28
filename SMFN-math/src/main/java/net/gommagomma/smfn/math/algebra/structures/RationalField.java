package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.core.algebra.numeric.Rational;
import net.gommagomma.smfn.math.core.algebra.structures.Field;


public class RationalField
implements Field<Rational>
{
	public static final RationalField INSTANCE = new RationalField();

    private RationalField() {
    	// Costruttore privato per il singleton
    }

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
