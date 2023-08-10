package net.gommagomma.smfn.math.geometry;


import net.gommagomma.smfn.math.algebra.Complex;


public class DiscreteGaussPlane
extends DiscreteCartesianPlane
{
	public DiscreteGaussPlane(Complex leftUpPoint, Complex rightDownPoint, double d)
	{
		super(leftUpPoint.getRe(), leftUpPoint.getIm(), rightDownPoint.getRe(), rightDownPoint.getIm(), d);
	}


	public Complex getPoint(int x, int y)
	{
		return new Complex(getPointX(x, y), getPointY(x, y));
	}
}
