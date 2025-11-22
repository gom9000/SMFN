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


import net.gommagomma.smfn.old.math.algebra.Complex;
import net.gommagomma.smfn.old.math.algebra.Real;
import net.gommagomma.smfn.old.math.geometry.DiscreteGaussPlane;
import net.gommagomma.smfn.old.math.geometry.PlaneToScalarOperator;


public abstract class ComplexIteration
{
	protected int currentIteration;
	protected int maximumIterations;
	protected double iterationEpsilon;


	abstract public Complex iteration(Complex z0, Complex zn);
	abstract public void preIteration(Complex zn);
	abstract public void postIteration(Complex zold, Complex zn);
	abstract public boolean iterationCondition(Complex zold, Complex zn);
	abstract public void postIterationLoop(Complex zold, Complex zn);


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
		//this.currentIteration--;
		postIterationLoop(zold, zn);

		return zn;
	}


	public PlaneToScalarOperator<Real> iteratePlane(DiscreteGaussPlane plane)
	{
		PlaneToScalarOperator<Real> operator = new PlaneToScalarOperator<Real>(plane);

		for (int yy = 0; yy < plane.getSizeY(); yy++)
		{
			for (int xx = 0; xx < plane.getSizeX(); xx++)
			{
				iteratePoint(plane.getPoint(xx, yy));
				operator.setValue(xx, yy, new Real(currentIteration));
			}
		}

		return operator;
	}
}
