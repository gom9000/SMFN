package net.gommagomma.smfn.math.fractal;


import net.gommagomma.smfn.old.math.algebra.Complex;
import net.gommagomma.smfn.old.math.algebra.Real;
import net.gommagomma.smfn.old.math.geometry.DiscreteGaussPlane;
import net.gommagomma.smfn.old.math.geometry.PlaneToScalarOperator;


public class MandelbrotSetTest
{
	static public void main(String[] args)
	throws Exception
	{
		Complex leftUp = new Complex(-2, 1.35);
		Complex rightDown = new Complex(1, -1.35);
		double d = 0.1;

		ComplexIteration fractal = new MandelbrotSet(1000000, Double.MIN_NORMAL);
		//DiscreteGaussPlane plane = new DiscreteGaussPlane(leftUp, rightDown, d);
		DiscreteGaussPlane plane = new DiscreteGaussPlane(leftUp, rightDown, 31, 27);
		PlaneToScalarOperator<Real> op = fractal.iteratePlane(plane);

    	for (int yy = 0; yy < plane.getSizeY(); yy++)
    	{
    		for (int xx = 0; xx < plane.getSizeX(); xx++)
    		{
    			if (op.getValue(xx, yy).getValue() <= 0) System.out.print("[*]");
    			else System.out.print("[ ]");
    		}
    		System.out.println();
    	}
    	System.out.println();
	}
}
