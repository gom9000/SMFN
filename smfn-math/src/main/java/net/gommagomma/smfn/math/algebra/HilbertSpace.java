package net.gommagomma.smfn.math.algebra;


public abstract class HilbertSpace<E extends ScalarFieldElement<E>>
extends VectorSpace<E>
{
	public HilbertSpace(int dimension, Field<E> field)
	{
		super(dimension, field);
	}


	abstract public E innerProduct(Vector<E> u, Vector<E> v);
}
