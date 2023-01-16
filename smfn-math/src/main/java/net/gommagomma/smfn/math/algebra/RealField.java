/*
 * RealField.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra;


/**
 * Representation of the real field.
 * @author gommagomma.net
 */
public class RealField
implements Field<Real>
{
	@Override
	public Real sum(Real a, Real b)
	{
	   return new Real(a.getValue() + b.getValue());
	}

	@Override
	public Real mul(Real a, Real b)
	{
		return new Real(a.getValue() * b.getValue());
	}

	@Override
	public Real sub(Real a, Real b)
	{
		return new Real(a.getValue() - b.getValue());
	}

	@Override
	public Real div(Real a, Real b)
	{
		return new Real(a.getValue() / b.getValue());
	}
}
