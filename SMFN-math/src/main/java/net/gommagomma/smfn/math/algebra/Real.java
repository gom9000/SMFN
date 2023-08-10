/*
 * Real.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra;


import net.gommagomma.smfn.math.SMFNConstant;


/**
 * Representation of a real number.
 * @author gommagomma.net
 */
public class Real
implements ScalarFieldElement<Real>
{
	private double value;


	public Real(double magnitude)
	{
		this.value = magnitude;
	}


	public Real()
	{
		this(0);
	}


	public double getValue()
	{
		return this.value;
	}


	@Override
	public double modulus()
	{
		return Math.abs(this.value);
	}


	@Override
	public Real power(int power)
	{
		return new Real(Math.pow(this.value, power));
	}


	@Override
	public Real square()
	{
		return new Real(Math.sqrt(this.value));
	}


    @Override
    public final boolean equals(Object e) 
    {
        return (e instanceof Real) && (this.value - ((Real)e).getValue()) < SMFNConstant.EPSILON;
    }


    @Override
    public String toString()
    {
    	return String.valueOf(this.value);
    }
}
