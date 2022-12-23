package net.gommagomma.smfn.math.algebra;


public class HilbertSpace<F, E extends ScalarFieldElement<E>>
extends VectorSpace<F, E>
{
	public HilbertSpace(int dimension, Field<F, E> field)
	{
		super(dimension, field);
	}


	public E innerProduct(Vector<E> u, Vector<E> v)
	{
		// TODO HilbertSpace innerProduct...
		return null;
	}
}
