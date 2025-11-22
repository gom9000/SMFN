package net.gommagomma.smfn.old.math.geometry;

import net.gommagomma.smfn.math.algebra.numeric.Complex;

public class DiscreteGaussPlane
extends DiscreteCartesianPlane
{
	public DiscreteGaussPlane(Complex leftUpPoint, Complex rightDownPoint, double d)
	{
		super(leftUpPoint.getRe(), leftUpPoint.getIm(), rightDownPoint.getRe(), rightDownPoint.getIm(), d);
	}


	public DiscreteGaussPlane(Complex leftUpPoint, Complex rightDownPoint, int sizeX, int sizeY)
	{
		super(leftUpPoint.getRe(), leftUpPoint.getIm(), rightDownPoint.getRe(), rightDownPoint.getIm(), sizeX, sizeY);
	}


	public Complex getPoint(int x, int y)
	{
		return new Complex(getPointX(x, y), getPointY(x, y));
	}
}
