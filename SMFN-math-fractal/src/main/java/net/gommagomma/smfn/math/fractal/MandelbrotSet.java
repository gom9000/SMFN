/*
 * MandelbrotSet.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.fractal;


import net.gommagomma.smfn.math.algebra.Complex;
import net.gommagomma.smfn.math.algebra.ComplexField;


/**
 * 
 * @author gommagomma.net
 */
public class MandelbrotSet
extends ComplexIteration
{
	private ComplexField C = new ComplexField();


	public MandelbrotSet(int maximumIterations, double iterationEpsilon)
	{
		super(maximumIterations, iterationEpsilon);
	}


	@Override
	public Complex iteration(Complex z0, Complex zn)
	{
		return C.sum(zn.power(2), z0);
	}


	@Override
	public void preIteration(Complex z)
	{}


	@Override
	public void postIteration(Complex zold, Complex zn)
	{
		//System.out.println(super.getCurrentIteration() + " : |zn|=" + zn.modulus() + ", delta=" + Math.abs(zn.modulus()-zold.modulus()));
	}


	@Override
	public boolean iterationCondition(Complex zold, Complex zn)
	{
		//return getCurrentIteration() <= 3;
		return (getCurrentIteration() <= maximumIterations) && (zn.modulus() <= 2+iterationEpsilon) && (Math.abs(zn.modulus()-zold.modulus()) > iterationEpsilon);
	}
}
