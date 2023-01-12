package net.gommagomma.smfn.math.geometry;


import net.gommagomma.smfn.math.algebra.Complex;


public class DiscreteGaussPlane
extends DiscreteCartesianPlane
{
	public DiscreteGaussPlane(Complex lu, Complex rd, double d)
	{
		super(lu.getRe(), lu.getIm(), rd.getRe(), rd.getIm(), d);
	}


	public Complex getPoint(int x, int y)
	{
		return new Complex(getPointX(x, y), getPointY(x, y));
	}
}
