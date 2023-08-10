package net.gommagomma.smfn.math.fractal;


import net.gommagomma.smfn.math.algebra.Complex;
import net.gommagomma.smfn.math.geometry.DiscreteGaussPlane;
import net.gommagomma.smfn.math.geometry.PlaneToArrayOfScalarOperator;


public class MandelbrotSetTest
{
	static public void main(String[] args)
	throws Exception
	{
		Complex leftUp = new Complex(-2.1, 1.3);
		Complex rightDown = new Complex(.6, -1.3);
		double d = 0.1; //.0014;

		int sizeX = Math.abs((int)((2.7) / d))+1;
		int sizeY = Math.abs((int)((2.6) / d))+1;
		System.out.println("size: "+sizeX+"x"+sizeY);

		ComplexIteration fractal = new MandelbrotSet(1000, .000001);
		DiscreteGaussPlane plane = new DiscreteGaussPlane(leftUp, rightDown, d);

		PlaneToArrayOfScalarOperator<Complex> op = fractal.iteratePlane(plane);

    	for (int jj = 0; jj < plane.getSizeY(); jj++)
    	{
    		for (int ii = 0; ii < plane.getSizeX(); ii++)
    		{
    			//System.out.print(((op.getValue(ii, jj)[0].modulus()<=2)?"*":" "));
    			//System.out.print("["+((op.getValue(ii, jj)[0].modulus()<=2)?"*":" ")+"]");
    			//System.out.print("["+(op.getValue(ii, jj))[0]+"]");
    			System.out.print("["+(int)(op.getValue(ii, jj))[1].getRe()+"]");
    		}
    		System.out.println();
    	}
    	System.out.println();
	}
}
