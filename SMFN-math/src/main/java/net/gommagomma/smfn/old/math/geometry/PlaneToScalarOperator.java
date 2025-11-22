package net.gommagomma.smfn.old.math.geometry;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;

public class PlaneToScalarOperator<E extends FieldElement<E>>
{
	private E operator[][];


	public PlaneToScalarOperator(DiscreteCartesianPlane plane)
	{
		this.operator = (E[][])new FieldElement[plane.getSizeX()][plane.getSizeY()];
	}


	public void setValue(int x, int y, E e)
	{
		operator[x][y] = e;
	}


	public E getValue(int x, int y)
	{
		return operator[x][y];
	}
}
