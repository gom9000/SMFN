/*
 * Vector.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra;


import java.util.ArrayList;


/**
 * Representation of a vector of scalar elements.
 * @author gommagomma.net
 * @param <E> - the type of the scalar elements
 */
public class Vector<E extends ScalarFieldElement<E>>
implements FieldElement<Vector<E>>
{
	ArrayList<E> elements;
	private int dimension;


	public Vector(int dimension)
	{
		this.dimension= dimension;
		elements = new ArrayList<E>(dimension);
	}


	public int getDimension()
	{
		return this.dimension;
	}


	public void add(E e)
	{
		elements.add(e);
	}


	public E get(int index)
	{
		return elements.get(index);
	}


	@Override
	public double modulus()
	{
		double d = 0;
		for (int ii = 0; ii < elements.size(); ii++)
		{
			d += Math.pow(((E)elements.get(ii)).modulus(), 2);
		}

		return Math.sqrt(d);
	}


    @Override
    public final boolean equals(Object z) 
    {
    	for (int ii = 0; ii < elements.size(); ii++)
    	{
    		if (!((E)elements.get(ii)).equals(z))
    		{
    			return false;
    		}
    	}

    	return true;
    }


    @Override
    public String toString()
    {
    	StringBuilder s = new StringBuilder();

    	s.append("[");
    	for (int ii = 0; ii < elements.size(); ii++)
		{
    		s.append(((ScalarFieldElement<E>)elements.get(ii))).append(",");
		}
    	s.append("]");

    	return s.toString();
    }
}
