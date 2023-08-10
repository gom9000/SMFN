/*
 * Space.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra;


/**
 * A space S of V elements over a scalar field F is a an algebraic structure with two binary operations:
 * - addition: assigns to any two vectors v and w a third vector in V which is commonly
 *             written as v + w, and called the sum of these two vectors.
 * - multiplication by a scalar: 
 * 
 * @author gommagomma.net
 * @param <V> - the type of the space elements
 * @param <E> - the type of the under scalar field elements
 */
public interface Space<V, E>
extends AlgebraicStructure
{
	public int getDimension();
	public Field<E> getField();
	public V sum(V u, V v);
	public V mul(E l, V u);
}
