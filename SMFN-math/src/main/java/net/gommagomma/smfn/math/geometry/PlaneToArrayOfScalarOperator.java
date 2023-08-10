package net.gommagomma.smfn.math.geometry;


import net.gommagomma.smfn.math.algebra.ScalarFieldElement;


public class PlaneToArrayOfScalarOperator<E extends ScalarFieldElement<E>>
{
	private E[] operator[][];


	public PlaneToArrayOfScalarOperator(DiscreteCartesianPlane plane)
	{
		this.operator = (E[][][])new ScalarFieldElement[plane.getSizeX()][plane.getSizeY()][];
	}


	public void setValue(int x, int y, E[] e)
	{
		operator[x][y] = e;
	}


	public E[] getValue(int x, int y)
	{
		return operator[x][y];
	}
}
