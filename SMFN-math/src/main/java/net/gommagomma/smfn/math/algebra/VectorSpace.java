/*
 * VectorSpace.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra;


/**
 * Representation of the vector space on field F.
 * @author gommagomma.net
 * @param <E> - the type of the under scalar field elements
 */
public class VectorSpace<E extends ScalarFieldElement<E>>
implements Space<Vector<E>, E>
{
    private int dimension;
    private Field<E> field;


    /**
     * Create a vector space on the specified Field.
     * @param dimension the dimension of the space
     * @param field the lower scalar field
     */
    public VectorSpace(int dimension, Field<E> field)
    {
    	this.dimension = dimension;
    	this.field = field;
    }


	@Override
	public int getDimension()
	{
		return this.dimension;
	}


	@Override
	public Field<E> getField()
	{
		return this.field;
	}


	@Override
	public Vector<E> sum(Vector<E> u, Vector<E> v)
	{
		Vector<E> w = new Vector<E>(this.dimension);
		for (int ii = 0; ii < this.dimension; ii++)
		{
			w.add(field.sum(u.get(ii), v.get(ii)));
		}

		return w;
	}


	@Override
	public Vector<E> mul(E l, Vector<E> u)
	{
		Vector<E> w = new Vector<E>(this.dimension);
		for (int ii = 0; ii < this.dimension; ii++)
		{
			w.add(field.sum(l, u.get(ii)));
		}

		return w;
	}
}
