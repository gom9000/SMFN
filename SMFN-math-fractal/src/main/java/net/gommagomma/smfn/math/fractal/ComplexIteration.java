/*
 * ComplexIteration.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.fractal;


import net.gommagomma.smfn.math.algebra.Complex;
import net.gommagomma.smfn.math.geometry.DiscreteGaussPlane;
import net.gommagomma.smfn.math.geometry.PlaneToArrayOfScalarOperator;


public abstract class ComplexIteration
{
	private int currentIteration;
	protected int maximumIterations;
	protected double iterationEpsilon;


	abstract public Complex iteration(Complex z0, Complex zn);
	abstract public void preIteration(Complex zn);
	abstract public void postIteration(Complex zold, Complex zn);
	abstract public boolean iterationCondition(Complex zold, Complex zn);


	public ComplexIteration(int maximumIterations, double iterationEpsilon)
	{
		this.maximumIterations = maximumIterations;
		this.iterationEpsilon = iterationEpsilon;
		this.currentIteration = 1;
	}


	public int getCurrentIteration()
	{
		return this.currentIteration;
	}


	public Complex iteratePoint(Complex z0)
	{
		Complex zn = new Complex(z0);
		Complex zold = new Complex(z0.getRe() + 1, 0);

		this.currentIteration = 1;
		while (iterationCondition(zold, zn))
		{
			zold = new Complex(zn);
			preIteration(zn);
			zn = iteration(z0, zn);
			postIteration(zold, zn);
			this.currentIteration++;
		}
		this.currentIteration--;

		return zn;
	}


	public PlaneToArrayOfScalarOperator<Complex> iteratePlane(DiscreteGaussPlane plane)
	{
		PlaneToArrayOfScalarOperator<Complex> operator = new PlaneToArrayOfScalarOperator<Complex>(plane);

		for (int xx = 0; xx < plane.getSizeX(); xx++)
		{
			for (int yy = 0; yy < plane.getSizeY(); yy++)
			{
				Complex z = iteratePoint(plane.getPoint(xx, yy));
				//plane.setValue(xx, yy, new Real(getCurrentIteration()));
				Complex[] values = new Complex[2];
				values[0] = z;
				//values[1] = new Complex(currentIteration>=maximumIterations? 0 : currentIteration);
				values[1] = new Complex(currentIteration);
				operator.setValue(xx, yy, values);
			}
		}

		return operator;
	}
}
